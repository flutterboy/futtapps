package it.negro.contab.entity;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.INTERNAL_SERVER_ERROR, reason="sticazzi")
public class ContabException extends RuntimeException {

	private static final long serialVersionUID = 1640980374021647466L;
	
	private String developerMessage;
	
	public ContabException() {
		super();
	}
	
	public ContabException(String m){
		super(m);
		this.developerMessage = m;
	}
	
	public ContabException(String m, String dm){
		super(m);
		this.developerMessage = dm;
	}
	
	public ContabException(String m, Throwable cause){
		super(m, cause);
		this.developerMessage = m;
	}
	
	public ContabException(String m, String dm, Throwable cause){
		super(m, cause);
		this.developerMessage = dm;
	}
	
	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	
	public String getDeveloperMessage() {
		return developerMessage;
	}

}
