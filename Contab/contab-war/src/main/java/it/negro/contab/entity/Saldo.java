package it.negro.contab.entity;

import org.springframework.data.annotation.Id;

import java.util.Date;

public class Saldo {
	
	@Id
	private String target;
	private Double importo = 0.0;
	private Date data;
	
	public Saldo() {}
	
	public Saldo(String target) {
		this.target = target;
	}
	
	public Double getImporto() {
		return importo;
	}
	public void setImporto(Double importo) {
		this.importo = importo;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public Date getData() {
		return data;
	}
	public void setData(Date data) {
		this.data = data;
	}
	
	public void sum(Saldo s){
		this.importo += s.getImporto();
	}
	
}
