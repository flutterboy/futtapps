package it.negro.contab.entity;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import it.negro.contab.converter.ContabDateDeserializer;
import it.negro.contab.converter.ContabDateSerializer;

import java.util.Date;

public class SaldoProgressivo {

    private Date data;
    private Double importo = 0.0;
    @JsonSerialize(using=ContabDateSerializer.class)
    public Date getData() {
        return data;
    }
    @JsonDeserialize(using=ContabDateDeserializer.class)
    public void setData(Date data) {
        this.data = data;
    }

    public Double getImporto() {
        return importo;
    }

    public void setImporto(Double importo) {
        this.importo = importo;
    }

    public void sum (Double s){
        this.importo += s;
    }

    public void subtract (Double s){
        this.importo -= s;
    }
}