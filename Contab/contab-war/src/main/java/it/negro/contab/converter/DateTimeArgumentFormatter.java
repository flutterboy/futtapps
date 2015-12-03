package it.negro.contab.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.joda.time.DateTime;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

@Component
public class DateTimeArgumentFormatter implements Formatter<DateTime> {

	private DateTimeArgument dateTimeArgument;

	public DateTimeArgumentFormatter (DateTimeArgument argument){
		this.dateTimeArgument = argument;
	}
	
	@Override
	public DateTime parse(String s, Locale locale) throws ParseException {
		DateTime result = parse(s, "dd-MM-yyyy");
		if (this.dateTimeArgument.plusDays() > 0)
			result = result.plusDays(dateTimeArgument.plusDays());
		else if (this.dateTimeArgument.plusDays() < 0)
			result = result.minusDays((dateTimeArgument.plusDays() * -1));
		if (this.dateTimeArgument.plusMillis() > 0)
			result = result.plus(dateTimeArgument.plusMillis());
		else if (this.dateTimeArgument.plusMillis() < 0)
			result = result.minus((dateTimeArgument.plusMillis() * -1));
		if (this.dateTimeArgument.plusYears() > 0)
			result = result.plusYears(dateTimeArgument.plusYears());
		else if (this.dateTimeArgument.plusYears() < 0)
			result = result.minusYears((dateTimeArgument.plusYears() * -1));
		return result;
	}
	
	@Override
	public String print(DateTime da, Locale locale) {
		return format(da, "dd-MM-yyyy");
	}

	private static DateTime parse (String s, String pattern)throws ParseException{
		DateTime d = new DateTime();
		if (s != null && s.length() > 0 && !s.equals("x"))
			d = new DateTime((new SimpleDateFormat("dd-MM-yyyy").parse(s)));
		return d;
	}
	private static String format (DateTime da, String pattern){
		if (da == null)
			return "";
		return new SimpleDateFormat("dd-MM-yyyy").format(da.toDate());
	}
	
}
