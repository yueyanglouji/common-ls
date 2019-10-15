package l.s.common.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

public class DateUtil {
	
	public static ZonedDateTime toZoneDateTime(LocalDateTime ldt){
		ZonedDateTime zdt = ZonedDateTime.of(ldt, ZoneId.systemDefault());
		return zdt;
	}
	
	public static ZonedDateTime toZoneDateTime(ZonedDateTime zdt, ZoneId zoneid){
		ZonedDateTime copy = ZonedDateTime.ofInstant(zdt.toInstant(), zoneid);
		return copy;
	}
	
	public static ZonedDateTime toZoneDateTime(LocalDateTime zdt, ZoneId zoneid){
		ZonedDateTime copy = ZonedDateTime.ofInstant(zdt.atZone(ZoneId.systemDefault()).toInstant(), zoneid);
		return copy;
	}
	
	public static Date toDate(LocalDateTime ldt){
		Instant instant = ldt.atZone(ZoneId.systemDefault()).toInstant();
		Date res = Date.from(instant);
		return res;
	}
	
	public static LocalDateTime toLocalDateTime(Date dt){
		Instant instant = Instant.ofEpochMilli(dt.getTime());
		LocalDateTime res = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return res;
	}
	
	public static LocalDateTime toLocalDateTime(ZonedDateTime zdt){
		Instant instant = zdt.toInstant();
		LocalDateTime ldt = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
		return ldt;
	}
	
	public static int compare(ZonedDateTime zdt1, ZonedDateTime zdt2){
		return toLocalDateTime(zdt1).compareTo(toLocalDateTime(zdt2));
	}
	
	public static void main(String[] args) {
		ZonedDateTime zdt1 = DateUtil.toZoneDateTime(LocalDateTime.now());
		ZonedDateTime zdt2 = DateUtil.toZoneDateTime(zdt1, ZoneId.of("+09:00"));
		System.out.println(DateUtil.compare(zdt1, zdt2));
	}
}
