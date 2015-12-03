package it.negro.contab.repository;

import it.negro.contab.entity.MovimentoContabile;
import it.negro.contab.entity.Direzione;
import it.negro.contab.entity.Target;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.joda.time.DateTime;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.util.StringUtils;

public class MovimentoContabileRepository {
	
	private MongoTemplate mongo;
	private CounterRepository counterRepository;
	
	public void setCounterRepository(CounterRepository counterRepository) {
        this.counterRepository = counterRepository;
	}
	
	public void setMongo(MongoTemplate mongo) {
        this.mongo = mongo;
	}
	
	public MovimentoContabile select (Integer id){
        return mongo.findById(id, MovimentoContabile.class, "movimenti");
	}
	
	public List<MovimentoContabile> read(DateTime da, DateTime a, String direzione){
        return read(null , direzione, da, a);
	}
	
	public List<MovimentoContabile> read(){
		return read(null , null, null, null);
	}

	public List<MovimentoContabile> read(DateTime da, DateTime a){
        return read(null , null, da, a);
	}

    public List<MovimentoContabile> read(String target, String direction, DateTime da, DateTime a){
        List<AggregationOperation> aggregations = new ArrayList<AggregationOperation>();
        if (StringUtils.hasLength(target))
            aggregations.add(Aggregation.match(Criteria.where("target").is(target)));
        if(StringUtils.hasLength(direction))
            aggregations.add(Aggregation.match(Criteria.where("direzione").is(direction)));
        if (da != null)
            aggregations.add(Aggregation.match(Criteria.where("data").gte(da.toDate())));
        if (a != null)
            aggregations.add(Aggregation.match(Criteria.where("data").lte(a.toDate())));
        aggregations.add(Aggregation.sort(Direction.DESC, "data"));
        Aggregation aggregation = Aggregation.newAggregation(aggregations);
        AggregationResults<MovimentoContabile> aggregationResults = mongo.aggregate(aggregation, "movimenti", MovimentoContabile.class);
        return aggregationResults.getMappedResults();
    }

	public void create(MovimentoContabile movimento){
        int id = counterRepository.getNextSequence();
        movimento.setId(id);
        mongo.insert(movimento, "movimenti");
        if (movimento.isUscita() && movimento.getTarget().equals(Target.CARTA_CREDITO.name())){
            int idFiglio = counterRepository.getNextSequence();

            Calendar dataMovimento = Calendar.getInstance();
            dataMovimento.set(Calendar.MONTH, (dataMovimento.get(Calendar.MONTH) + 1));
            dataMovimento.set(Calendar.DATE, 11);

            MovimentoContabile movFiglio = new MovimentoContabile();
            movFiglio.setIdPadre(id);
            movFiglio.setId(idFiglio);
            movFiglio.setTarget(Target.CONTO_CORRENTE.name());
            movFiglio.setDescrizione("Addebito Carta di credito (" + movimento.getDescrizione() + ")");
            movFiglio.setDirezione(Direzione.USCITA.name());
            movFiglio.setDocumento(movimento.getDocumento());
            movFiglio.setData(dataMovimento.getTime());
            movFiglio.setImporto(movimento.getImporto());
            mongo.insert(movFiglio, "movimenti");

            idFiglio = counterRepository.getNextSequence();

            movFiglio.setId(idFiglio);
            movFiglio.setTarget(Target.CARTA_CREDITO.name());
            movFiglio.setDescrizione("Addebitato su conto corrente - ritorno in plafond (" + movimento.getDescrizione() + ")");
            movFiglio.setDirezione(Direzione.ENTRATA.name());
            mongo.insert(movFiglio, "movimenti");

        }
    }
	public void save(MovimentoContabile movimento){
		mongo.save(movimento, "movimenti");
        if (movimento.isUscita() && movimento.getTarget().equals(Target.CARTA_CREDITO.name())){
            List<MovimentoContabile> figli = mongo.find(new Query(Criteria.where("idPadre").is(movimento.getId())), MovimentoContabile.class, "movimenti");
            figli.forEach(movimentoContabile ->  {
                if (movimentoContabile.isEntrata())
                    movimentoContabile.setDescrizione("Addebitato su conto corrente - ritorno in plafond (" + movimento.getDescrizione() + ")");
                else
                    movimentoContabile.setDescrizione("Addebito Carta di credito (" + movimento.getDescrizione() + ")");
                movimentoContabile.setDocumento(movimento.getDocumento());
                movimentoContabile.setImporto(movimento.getImporto());
                Calendar dataMovimento = Calendar.getInstance();
                dataMovimento.set(Calendar.MONTH, (dataMovimento.get(Calendar.MONTH) + 1));
                dataMovimento.set(Calendar.DATE, 11);
                movimentoContabile.setData(dataMovimento.getTime());
                mongo.save(movimentoContabile, "movimenti");
            });
        }
	}
	
	public void delete (Integer id) {
        List<MovimentoContabile> figli = mongo.find(new Query(Criteria.where("idPadre").is(id)), MovimentoContabile.class, "movimenti");
        figli.forEach(movimentoContabile ->  {
            mongo.remove(new Query(Criteria.where("_id").is(movimentoContabile.getId())), "movimenti");
        });
		mongo.remove(new Query(Criteria.where("_id").is(id)), "movimenti");
	}
	
	public List<MovimentoContabile> readLast(Integer num, DateTime dateArg, String direzione){
		return readLastNext(num, dateArg, direzione, "last");
	}
	
	public List<MovimentoContabile> readNext(Integer num, DateTime dateArg, String direzione){
		return readLastNext(num, dateArg, direzione, "next");
	}
	
	private List<MovimentoContabile> readLastNext(Integer num, DateTime dateArg, String direzione, String lastNext){
		List<AggregationOperation> aggOps = new ArrayList<AggregationOperation>();
		if (lastNext.equals("last")){
			aggOps.add(Aggregation.match(Criteria.where("data").lte(dateArg.toDate())));
			aggOps.add(Aggregation.sort(Direction.DESC, "data"));
		}else{
			aggOps.add(Aggregation.match(Criteria.where("data").gte(dateArg.toDate())));
			aggOps.add(Aggregation.sort(Direction.ASC, "data"));
		}
        if (direzione != null)
            aggOps.add(Aggregation.match(Criteria.where("direzione").is(direzione)));
		aggOps.add(Aggregation.limit(num));
		Aggregation agg = Aggregation.newAggregation(aggOps);
		
		return mongo.aggregate(agg, "movimenti", MovimentoContabile.class).getMappedResults();
	}

}