package it.negro.contab.service;

import it.negro.contab.converter.DateTimeArgument;
import it.negro.contab.entity.Direzione;
import it.negro.contab.entity.SaldoProgressivo;
import it.negro.contab.repository.SaldoRepository;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class StatisticheService extends AbstractContabService {

    private SaldoRepository saldoRepository;

    @Autowired
    public void setSaldoRepository(SaldoRepository saldoRepository) {
        this.saldoRepository = saldoRepository;
    }

    @RequestMapping(value = "/rest/calcolaAndamentoEntrateUscite", method = RequestMethod.GET)
    public @ResponseBody
    Map<String, List<SaldoProgressivo>> getAndamentoEntrateUscite(){
        return getAndamentoEntrateUscite(null, null);
    }

    @RequestMapping(value = "/rest/calcolaAndamentoEntrateUsciteAl/{al}", method = RequestMethod.GET)
    public @ResponseBody Map<String, List<SaldoProgressivo>> getAndamentoEntrateUsciteAl(@PathVariable("al") @DateTimeArgument DateTime al){
        return getAndamentoEntrateUscite(null, al);
    }

    @RequestMapping(value = "/rest/calcolaAndamentoEntrateUsciteDal/{dal}", method = RequestMethod.GET)
    public @ResponseBody Map<String, List<SaldoProgressivo>> getAndamentoEntrateUsciteDal(@PathVariable("dal") @DateTimeArgument DateTime dal){
        return getAndamentoEntrateUscite(dal, null);
    }

    @RequestMapping(value = "/rest/calcolaAndamentoEntrateUscite/{dal}/{al}", method = RequestMethod.GET)
    public @ResponseBody Map<String, List<SaldoProgressivo>> getAndamentoEntrateUscite(@PathVariable("dal") @DateTimeArgument DateTime dal, @PathVariable("al") @DateTimeArgument DateTime al){
        DateTime minDate = new DateTime(saldoRepository.getMinDate());
        minDate = minDate.plusDays(1);
        if (dal == null)
            dal = minDate;
        if (dal.compareTo(minDate) < 0)
            dal = minDate;
        if (al == null)
            al = new DateTime();
        if (al.compareTo(dal) < 0)
            al = new DateTime(dal);
        List<SaldoProgressivo> andamentoEntrate = saldoRepository.calcolaAndamentoEntrate(dal, al);
        List<SaldoProgressivo> andamentoUscite = saldoRepository.calcolaAndamentoUscite(dal, al);
        Map<String, List<SaldoProgressivo>> result = new HashMap<>();
        result.put("andamentoEntrate", andamentoEntrate);
        result.put("andamentoUscite", andamentoUscite);
        return result;
    }

    @RequestMapping(value = "/rest/getIndiceDiRisparmio", method = RequestMethod.GET)
    public @ResponseBody Double getIndiceRisparmio(){
        return getIndiceRisparmio(null);
    }

    @RequestMapping(value = "/rest/getIndiceDiRisparmio/{al}", method = RequestMethod.GET)
    public @ResponseBody Double getIndiceRisparmio(@PathVariable("al") @DateTimeArgument DateTime al){
        DateTime minDate = new DateTime(saldoRepository.getMinDate());
        minDate = minDate.plusDays(1);
        if (al == null)
            al = minDate;
        if (al.compareTo(minDate) < 0)
            al = new DateTime(minDate);
        SaldoProgressivo saldoEntrate = saldoRepository.calcolaSaldoProgressivo(minDate, al, Direzione.ENTRATA.name());
        SaldoProgressivo saldoUscite = saldoRepository.calcolaSaldoProgressivo(minDate, al, Direzione.USCITA.name());
        Double result = saldoEntrate.getImporto() / saldoUscite.getImporto();
        result = Math.floor(result * 100) / 100;
        return result;
    }

    @RequestMapping(value = "/rest/getAndamentoIndiceDiRisparmio", method = RequestMethod.GET)
    public @ResponseBody List<SaldoProgressivo> getAndamentoIndiceRisparmio(){
        return getAndamentoIndiceRisparmio(null, null);
    }

    @RequestMapping(value = "/rest/getAndamentoIndiceDiRisparmioDal/{dal}", method = RequestMethod.GET)
    public @ResponseBody List<SaldoProgressivo> getAndamentoIndiceRisparmioDal(@PathVariable("dal") @DateTimeArgument DateTime dal){
        return getAndamentoIndiceRisparmio(dal, null);
    }

    @RequestMapping(value = "/rest/getAndamentoIndiceDiRisparmioAl/{al}", method = RequestMethod.GET)
    public @ResponseBody List<SaldoProgressivo> getAndamentoIndiceRisparmioAl(@PathVariable("al") @DateTimeArgument DateTime al){
        return getAndamentoIndiceRisparmio(null, al);
    }

    @RequestMapping(value = "/rest/getAndamentoIndiceDiRisparmio/{dal}/{al}", method = RequestMethod.GET)
    public @ResponseBody List<SaldoProgressivo> getAndamentoIndiceRisparmio(@PathVariable("dal") @DateTimeArgument DateTime dal, @PathVariable("al") @DateTimeArgument DateTime al){
        DateTime minDate = new DateTime(saldoRepository.getMinDate());
        minDate = minDate.plusDays(1);
        if (dal == null || dal.compareTo(minDate) < 0)
            dal = minDate;
        if (al == null)
            al = new DateTime();
        if (al.compareTo(dal) < 0)
            al = new DateTime(dal);
        List<SaldoProgressivo> result = new LinkedList<>();
        while (dal.compareTo(al) <= 0){
            SaldoProgressivo saldoEntrate = saldoRepository.calcolaSaldoProgressivo(minDate, dal, Direzione.ENTRATA.name());
            SaldoProgressivo saldoUscite = saldoRepository.calcolaSaldoProgressivo(minDate, dal, Direzione.USCITA.name());
            double importoUscita = saldoUscite.getImporto();
            Double index = null;
            if (importoUscita == 0.0)
                index = 0.0;
            else
                index = saldoEntrate.getImporto() / importoUscita;
            index = Math.floor(index * 100) / 100;
            SaldoProgressivo saldoIndex = new SaldoProgressivo();
            saldoIndex.setData(dal.toDate());
            saldoIndex.setImporto(index);
            result.add(saldoIndex);
            dal = dal.plusDays(1);
        }
        return result;
    }

}
