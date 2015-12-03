package it.negro.contabilitapp.entity;

import java.util.Calendar;
import java.util.Date;

public class MovimentoContabile {
    private Integer id;
    private Integer idPadre;
    private Date data;
    private Double importo;
    private String descrizione;
    private String direzione;
    private String target;
    private Documento documento;

    public MovimentoContabile() {
        this.data = Calendar.getInstance().getTime();
        this.importo = 0.0;
        this.descrizione = "";
        this.direzione = "USCITA";
        this.target = "CONTO_CORRENTE";
    }

    public void setDocumento(Documento documento) {
        this.documento = documento;
    }

    public Documento getDocumento() {
        return documento;
    }

    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public Date getData() {
        return data;
    }

    public void setData(Date data) {
        this.data = data;
    }
    public Double getImporto() {
        return importo;
    }
    public void setImporto(Double importo) {
        this.importo = importo;
    }
    public String getDescrizione() {
        return descrizione;
    }
    public void setDescrizione(String descrizione) {
        this.descrizione = descrizione;
    }
    public String getDirezione() {
        return direzione;
    }
    public void setDirezione(String direzione) {
        this.direzione = direzione;
    }
    public String getTarget() {
        return target;
    }
    public void setTarget(String target) {
        this.target = target;
    }
    public void setIdPadre(Integer idPadre) {
        this.idPadre = idPadre;
    }
    public Integer getIdPadre() {
        return idPadre;
    }
    public boolean isEntrata(){
        return direzione.equals("ENTRATA");
    }
    public boolean isUscita(){
        return direzione.equals("USCITA");
    }
}
