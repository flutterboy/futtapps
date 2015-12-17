package it.negro.contab.service;

import it.negro.contab.entity.*;
import it.negro.contab.repository.FileRepository;
import it.negro.contab.repository.MovimentoContabileRepository;
import it.negro.contab.repository.Page;
import it.negro.contab.repository.SaldoRepository;
import it.negro.contab.converter.DateTimeArgument;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mongodb.gridfs.GridFSDBFile;

@Controller
public class ContabService extends AbstractContabService {
	
	private MovimentoContabileRepository movimentoContabileRepository;
	private SaldoRepository saldoRepository;
	private FileRepository fileRepository;
	
	@Autowired
	public void setSaldoRepository(SaldoRepository saldoRepository) {
		this.saldoRepository = saldoRepository;
	}
	
	@Autowired
	public void setFileRepository(FileRepository fileRepository) {
		this.fileRepository = fileRepository;
	}
	
	@Autowired
	public void setMovimentoContabileRepository(MovimentoContabileRepository movimentoContabileRepository) {
		this.movimentoContabileRepository = movimentoContabileRepository;
	}
	
	@RequestMapping(value = "/rest/getAllMovimenti", method = RequestMethod.GET)
	public @ResponseBody List<MovimentoContabile> get()throws Exception{
		return movimentoContabileRepository.read();
	}

	@RequestMapping(value = "/rest/getPaged/{pageN}/{elemN}/{dataA}", method = RequestMethod.GET)
	public @ResponseBody List<MovimentoContabile> get(@PathVariable("pageN") Integer pageN, @PathVariable("elemN") Integer elemN, @PathVariable("dataA") @DateTimeArgument DateTime dataA)throws Exception{
		Page page = new Page(pageN, elemN);
		return movimentoContabileRepository.read(page, dataA);
	}
	
	@RequestMapping(value = "/rest/get/{da}/{a}/{direzione}", method = RequestMethod.GET)
	public @ResponseBody List<MovimentoContabile> get(@PathVariable("da") @DateTimeArgument DateTime da, @PathVariable("a") @DateTimeArgument DateTime a, @PathVariable("direzione")String direzione)throws Exception{
		if ("x".equals(direzione))
			return movimentoContabileRepository.read(da, a);
		return movimentoContabileRepository.read(da, a, direzione);
	}
	
	@RequestMapping(value = "/rest/getMovimenti/{da}/{a}", method = RequestMethod.GET)
	public @ResponseBody List<MovimentoContabile> get(@PathVariable("da") @DateTimeArgument DateTime da, @PathVariable("a")  @DateTimeArgument DateTime a){
		return movimentoContabileRepository.read(da, a);
	}

	@RequestMapping(value = "/rest/getUltimiMovimenti", method = RequestMethod.GET)
	public @ResponseBody List<MovimentoContabile> getUltimiMovimenti(){
		return movimentoContabileRepository.readLast(10, new DateTime(), null);
	}

	@RequestMapping(value = "/rest/getProssimeUscite", method = RequestMethod.GET)
	public @ResponseBody List<MovimentoContabile> getProssimeUscite(){
		return movimentoContabileRepository.readNext(10, new DateTime(), Direzione.USCITA.name());
	}

	@RequestMapping(value = "/rest/saveMovimento/{da}/{a}", method = RequestMethod.POST)
	public @ResponseBody List<MovimentoContabile> save(
			@RequestBody MovimentoContabile movimento,
			@PathVariable("da") @DateTimeArgument DateTime da,
			@PathVariable("a") @DateTimeArgument DateTime a){
		if (movimento.getId() != null)
			this.movimentoContabileRepository.save(movimento);
		else
			this.movimentoContabileRepository.create(movimento);
		List<MovimentoContabile> l = this.movimentoContabileRepository.read(da, a);
		return l;
	}
	
	@RequestMapping(value = "/rest/getDocument/{id}", method = RequestMethod.GET, produces = "image/png")
	public ResponseEntity<InputStreamResource> getDocument(@PathVariable("id") String id)throws IOException{
		GridFSDBFile document = fileRepository.read(id);
		return ResponseEntity
				.ok()
				.contentLength(document.getLength())
				.contentType(MediaType.parseMediaType("application/octet-stream"))
				.body(new InputStreamResource(document.getInputStream()));
	}

	@RequestMapping(value = "/rest/getDocumento/{id}", method = RequestMethod.GET)
	public @ResponseBody Documento getDocumento(@PathVariable("id") Integer id)throws IOException{
		return movimentoContabileRepository.readDocumento(id);
	}

	@RequestMapping(value = "/rest/getMovimento/{id}", method = RequestMethod.GET)
	public @ResponseBody MovimentoContabile getMovimento(@PathVariable("id") Integer id)throws IOException{
		return movimentoContabileRepository.readMovimento(id);
	}

	@RequestMapping(value="/rest/delete/{id}/{da}/{a}", method = RequestMethod.GET)
	public @ResponseBody List<MovimentoContabile> delete(@PathVariable("id") Integer id, @PathVariable("da") @DateTimeArgument DateTime da, @PathVariable("a") @DateTimeArgument DateTime a){
		movimentoContabileRepository.delete(id);
		return get(da, a);
	}
	
	@RequestMapping(value="/rest/getSaldi/{al}", method = RequestMethod.GET)
	public @ResponseBody Map<String, Saldo> getSaldi(@PathVariable("al") @DateTimeArgument(plusDays = 1, plusMillis = -1) DateTime al){
		Map<String, Saldo> saldiMap = saldoRepository.calcola(al);
		saldiMap.get("cartaCredito").setImporto((saldiMap.get("cartaCredito").getImporto() + 1500));
		return saldiMap;
	}
	
	@RequestMapping(value = "/rest/getSaldi",method = RequestMethod.GET)
	public @ResponseBody Map<String, Saldo> getSaldi(){
		return getSaldi(new DateTime());
	}

	@RequestMapping(value = "/rest/calcolaAndamento", method = RequestMethod.GET)
	public @ResponseBody List<SaldoProgressivo> calcolaAndamento(){
		return calcolaAndamento(null, null);
	}

	@RequestMapping(value = "/rest/calcolaAndamentoDal/{dal}", method = RequestMethod.GET)
	public @ResponseBody List<SaldoProgressivo> calcolaAndamentoDal(@PathVariable("dal") @DateTimeArgument DateTime dal){
		return calcolaAndamento(dal, null);
	}

	@RequestMapping(value = "/rest/calcolaAndamentoAl/{al}", method = RequestMethod.GET)
	public @ResponseBody List<SaldoProgressivo> calcolaAndamentoAl(@PathVariable("al") @DateTimeArgument DateTime al){
		return calcolaAndamento(null, al);
	}

	@RequestMapping(value = "/rest/calcolaAndamento/{dal}/{al}", method = RequestMethod.GET)
	public @ResponseBody List<SaldoProgressivo> calcolaAndamento(@PathVariable("dal") @DateTimeArgument DateTime dal, @PathVariable("al") @DateTimeArgument DateTime al){
		DateTime minDate = new DateTime(saldoRepository.getMinDate());
		minDate = minDate.minusDays(1);
		if (dal == null || dal.compareTo(minDate) < 0)
			dal = minDate;
		if (al != null && al.compareTo(dal) < 0)
			al = new DateTime(dal);
		return saldoRepository.calcolaAndamentoSaldo(dal, al);
	}
	
}
