package it.negro.contab.converter;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class ContabDateSerializer extends JsonSerializer<Date> {

	@Override
	public void serialize(Date value, JsonGenerator jgen, SerializerProvider provider) throws IOException, JsonProcessingException {
		if (value == null)
			jgen.writeNull();
		else{
			DateFormat format = new SimpleDateFormat("dd/MM/yyyy");
			format.setTimeZone(TimeZone.getTimeZone("UTC"));
			jgen.writeString(format.format(value));
		}
	}
	
}
