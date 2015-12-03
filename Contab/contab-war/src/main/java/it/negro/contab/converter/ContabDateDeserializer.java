package it.negro.contab.converter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

public class ContabDateDeserializer extends JsonDeserializer<Date> {
	
	@Override
	public Date deserialize(JsonParser jp, DeserializationContext ctxt)	throws IOException, JsonProcessingException {
		if (jp.getText() == null || jp.getText().length() == 0 || "x".equals(jp.getText()))
			return null;
		try {
			DateFormat format = new SimpleDateFormat("dd-MM-yyyy'T'HH:mm:ss.SSS'Z'");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			return format.parse(jp.getText());
		} catch (ParseException e) {
			throw new IOException(e);
		}
	}
	
}
