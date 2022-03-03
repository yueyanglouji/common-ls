package l.s.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {
	
	public static ZonedDateTime toZoneDateTime(LocalDateTime ldt){
		return ZonedDateTime.of(ldt, ZoneId.systemDefault());
	}
	
	public static ZonedDateTime toZoneDateTime(ZonedDateTime zdt, ZoneId zoneid){
		return ZonedDateTime.ofInstant(zdt.toInstant(), zoneid);
	}
	
	public static ZonedDateTime toZoneDateTime(LocalDateTime zdt, ZoneId zoneid){
		return ZonedDateTime.ofInstant(zdt.atZone(ZoneId.systemDefault()).toInstant(), zoneid);
	}
	
	public static Date toDate(LocalDateTime ldt){
		Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
		return Date.from(instant);
	}
	
	public static LocalDateTime toLocalDateTime(Date dt){
		Instant instant = Instant.ofEpochMilli(dt.getTime());
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
	
	public static LocalDateTime toLocalDateTime(ZonedDateTime zdt){
		Instant instant = zdt.toInstant();
		return LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
	}
	
	public static int compare(ZonedDateTime zdt1, ZonedDateTime zdt2){
		return toLocalDateTime(zdt1).compareTo(toLocalDateTime(zdt2));
	}
}
