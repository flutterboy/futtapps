package it.negro.contab.entity;

public class ErrorInfo {
	
	private String message;
	private String developerMessage;
	private String stackTrace;
	private String exceptionName;
	private Exception exception;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getDeveloperMessage() {
		return developerMessage;
	}
	public void setDeveloperMessage(String developerMessage) {
		this.developerMessage = developerMessage;
	}
	public String getStackTrace() {
		return stackTrace;
	}
	public void setStackTrace(String stackTrace) {
		this.stackTrace = stackTrace;
	}
	public String getExceptionName() {
		return exceptionName;
	}
	public void setExceptionName(String exceptionName) {
		this.exceptionName = exceptionName;
	}
	public Exception getException() {
		return exception;
	}
	public void setException(Exception exception) {
		this.exception = exception;
	}
	public static ErrorInfo defaultErrorInfo(Exception e){
		ErrorInfo res = new ErrorInfo();
		res.setMessage("Errore Sconosciuto");
		res.setDeveloperMessage("Fatal error during exception handling!");
		res.setException(e);
		return res;
	}
	
}
