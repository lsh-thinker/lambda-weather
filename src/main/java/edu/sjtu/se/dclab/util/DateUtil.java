package edu.sjtu.se.dclab.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DateUtil {
	public static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

	
	public static final String DATE_FORMATE = "yyyy-MM-dd HH:mm";
	private final static DateTimeFormatter DAY_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd/HH:mm");
	
	public static DateTime parse(String dateString) throws ParseException{
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		Date date = dateFormat.parse(dateString);
		DateTime oldDateTime = new DateTime(date);
		
		DateTime currentDatetime = new DateTime();
		DateTime dateTime = new DateTime(
				currentDatetime.year().get(),
				currentDatetime.monthOfYear().get(), 
				currentDatetime.dayOfMonth().get(), 
				oldDateTime.hourOfDay().get(),
				oldDateTime.minuteOfHour().get());
		LOG.info("Parse date String: " + dateString + " -> " + dateTime.toString(DATE_FORMATE));
		return dateTime;
	}
	
	public static DateTime parseTime(String dateString){
		return DAY_FORMAT.parseDateTime(dateString);
	}
	
//	public static String format(Date date){
//		SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMATE);
//		String dateString = dateFormat.format(date);
//		return dateString;
//	}
}
