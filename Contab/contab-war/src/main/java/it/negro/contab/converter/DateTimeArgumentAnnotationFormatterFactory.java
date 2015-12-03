package it.negro.contab.converter;

import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;
import org.springframework.format.AnnotationFormatterFactory;
import org.springframework.format.Parser;
import org.springframework.format.Printer;

public class DateTimeArgumentAnnotationFormatterFactory implements AnnotationFormatterFactory<DateTimeArgument> {
	
	private static final Set<Class<?>> FIELDS = new HashSet<Class<?>>();
	{
		FIELDS.add(DateTime.class);
	}
	@Override
	public Set<Class<?>> getFieldTypes() {
		return FIELDS;
	}

	@Override
	public Parser<?> getParser(DateTimeArgument arg0, Class<?> arg1) {
		return new DateTimeArgumentFormatter(arg0);
	}

	@Override
	public Printer<?> getPrinter(DateTimeArgument arg0, Class<?> arg1) {
		return new DateTimeArgumentFormatter(arg0);
	}

	
	
}
