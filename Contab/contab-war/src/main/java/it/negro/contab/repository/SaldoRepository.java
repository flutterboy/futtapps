package it.negro.contab.repository;

import java.util.*;

import it.negro.contab.entity.*;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

public class SaldoRepository {
	
	private static final Map<String, String> SALDI_MAP_NAMES = new HashMap<String, String>();
	{
		SALDI_MAP_NAMES.put(Target.BUONI_PASTO.name(), "buoniPasto");
		SALDI_MAP_NAMES.put(Target.CARTA_CREDITO.name(), "cartaCredito");
		SALDI_MAP_NAMES.put(Target.CONTANTI.name(), "contanti");
		SALDI_MAP_NAMES.put(Target.CONTO_CORRENTE.name(), "contoCorrente");
		SALDI_MAP_NAMES.put(Target.CONTO_CORRENTE_COMUNE.name(), "contoCorrenteComune");
	}
	
	private MongoTemplate mongo;
	
	public void setMongo(MongoTemplate mongo) {
		this.mongo = mongo;
	}
	
	public Map<String, Saldo> calcola(DateTime al){
		Map<String, Saldo> result = calcolaInternal(al);
		DateTime al30 = al.plusDays(30);
		Map<String, Saldo> result30 = calcolaInternal(al30);
		result.put("globaleA30", result30.get("globale"));
		return result;
	}
	
	private Map<String, Saldo> calcolaInternal(DateTime al){
		Aggregation agg = Aggregation.newAggregation(
					Aggregation.match(Criteria.where("data").lte(al.toDate())),
					Aggregation.match(Criteria.where("direzione").is("ENTRATA")),
					Aggregation.group("target").first("$target").as("target").sum("$importo").as("importo")
				);

		AggregationResults<Saldo> saldiEntrate = mongo.aggregate(agg, "movimenti", Saldo.class);
		Map<String, Saldo> mapEntrate = new HashMap<String, Saldo>();
		saldiEntrate.forEach(s -> {
			mapEntrate.put(s.getTarget(), s);
		});

		agg = Aggregation.newAggregation(
				Aggregation.match(Criteria.where("data").lte(al.toDate())),
				Aggregation.match(Criteria.where("direzione").is("USCITA")),
				Aggregation.group("target").first("$target").as("target").sum("$importo").as("importo")
		);

		AggregationResults<Saldo> saldiUscite = mongo.aggregate(agg, "movimenti", Saldo.class);
		Map<String, Saldo> mapUscite = new HashMap<String, Saldo>();
		saldiUscite.forEach(s -> {
			s.setImporto((s.getImporto() * -1));
			mapUscite.put(s.getTarget(), s);
		});

		Map<String, Saldo> result = new HashMap<String, Saldo>();
		for (Target t : Target.values()) {
			if (t.equals(Target.GLOBALE))
				continue;
			String key = t.toString();
			String finalKey = SALDI_MAP_NAMES.get(key);
			if (!mapEntrate.containsKey(key) && !mapUscite.containsKey(key))
				result.put(finalKey, new Saldo(key));
			else if (mapEntrate.containsKey(key) && !mapUscite.containsKey(key))
				result.put(finalKey, mapEntrate.get(key));
			else if (mapUscite.containsKey(key) && !mapEntrate.containsKey(key))
				result.put(finalKey, mapUscite.get(key));
			else{
				Saldo se = mapEntrate.get(key);
				Saldo su = mapUscite.get(key);
				se.sum(su);
				result.put(finalKey, se);
			}

		}

		Saldo global = new Saldo(Target.GLOBALE.toString());
		result.forEach((k, v) -> {
			if (!k.equals(SALDI_MAP_NAMES.get(Target.CARTA_CREDITO.name())))
				global.sum(v);
		});

		result.put("globale", global);
		result.forEach((k, v) -> {
			v.setImporto((Long.valueOf(Math.round(v.getImporto() * 100)).doubleValue() / 100));
		});
		return result;
	}

	public List<SaldoProgressivo> calcolaAndamentoSaldo(DateTime dal, DateTime al){
		return calcolaAndamentoDirezione(null, dal, al);
	}

	public List<SaldoProgressivo> calcolaAndamentoEntrate(DateTime dal, DateTime al){
		return calcolaAndamentoDirezione(Direzione.ENTRATA.name(), dal, al);
	}

	public List<SaldoProgressivo> calcolaAndamentoUscite(DateTime dal, DateTime al){
		return calcolaAndamentoDirezione(Direzione.USCITA.name(), dal, al);
	}

	public List<SaldoProgressivo> calcolaAndamentoDirezione (String direzione, DateTime dal,  DateTime al){
		DateTime minDate = new DateTime(getMinDate()).minusDays(1);
		if (dal.compareTo(minDate) < 0)
			dal = new DateTime(minDate);
		if (al == null)
			al = new DateTime(getMaxDate());
		if (al.compareTo(dal) < 0)
			al = new DateTime(dal);
		DateTime progDate = new DateTime(dal);

		List<SaldoProgressivo> result = new LinkedList<>();
		while (dal.compareTo(al) <= 0){
			result.add(calcolaSaldoProgressivo(progDate, dal, direzione));
			dal = dal.plusDays(1);
		}
		return result;
	}

	public Date getMinDate(){
		Query query = Query.query(Criteria.where("target").ne("CARTA_CREDITO"));
		query.with(new Sort(Sort.Direction.ASC, "data"));
		query.limit(1);
		query.fields().include("data");
		return mongo.findOne(query, MovimentoContabile.class, "movimenti").getData();
	}

	public Date getMaxDate(){
		Query query = Query.query(Criteria.where("target").ne("CARTA_CREDITO"));
		query.with(new Sort(Sort.Direction.DESC, "data"));
		query.limit(1);
		query.fields().include("data");
		return mongo.findOne(query, MovimentoContabile.class, "movimenti").getData();
	}

	public SaldoProgressivo calcolaSaldoProgressivo(DateTime dal, DateTime al, String direzione){
		List<AggregationOperation> aggregationOperations = new ArrayList<>();

		aggregationOperations.add(Aggregation.match(Criteria.where("data").lte(al.toDate())));
		if (dal != null)
			aggregationOperations.add(Aggregation.match(Criteria.where("data").gt(dal.toDate())));
		aggregationOperations.add(Aggregation.match(Criteria.where("target").ne("CARTA_CREDITO")));
		if (direzione != null)
			aggregationOperations.add(Aggregation.match(Criteria.where("direzione").is(direzione)));
		aggregationOperations.add(Aggregation.group("direzione").first("direzione").as("direzione").sum("$importo").as("importo"));

		Aggregation agg = Aggregation.newAggregation(aggregationOperations);

		List<HashMap> saldiParziali = mongo.aggregate(agg, "movimenti", HashMap.class).getMappedResults();

		SaldoProgressivo result = new SaldoProgressivo();
		result.setData(al.toDate());

		saldiParziali.forEach(saldoParziale -> {
			if (saldoParziale.get("direzione").equals(Direzione.ENTRATA.name()) || direzione != null)
				result.sum((Double) saldoParziale.get("importo"));
			else
				result.subtract((Double) saldoParziale.get("importo"));
		});
		result.setImporto((Long.valueOf(Math.round(result.getImporto() * 100)).doubleValue() / 100));
		return result;
	}

}
