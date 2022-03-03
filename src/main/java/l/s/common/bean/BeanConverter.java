package l.s.common.bean;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import org.springframework.core.convert.converter.Converter;
import org.springframework.core.convert.support.DefaultConversionService;

import l.s.common.util.DateUtil;

public class BeanConverter {

	DefaultConversionService service;
	
	private static BeanConverter defalult;
	
	public static BeanConverter getDefault(){
		if(defalult == null){
			defalult = new BeanConverter();
		}
		return defalult;
	}
	
	public BeanConverter(){
		service = new DefaultConversionService();
		
		addConverter((Converter<Date, String>) source -> {
			SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
			return f.format(source);
		});
		
		addConverter((Converter<String, Date>) source -> {
			try {
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				return f.parse(source);
			} catch (ParseException e) {
				throw new RuntimeException(e);
			}
		});
		
		addConverter((Converter<LocalDateTime, String>) source -> {
			ZonedDateTime zdt = DateUtil.toZoneDateTime(source);
			return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
		});
		
		addConverter((Converter<String, LocalDateTime>) source -> {
			ZonedDateTime zdt = ZonedDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
			return DateUtil.toLocalDateTime(zdt);
		});
		
		addConverter((Converter<ZonedDateTime, String>) source -> source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")));
		
		addConverter((Converter<String, ZonedDateTime>) source -> ZonedDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")));
		
		addConverter((Converter<LocalDate, String>) source -> source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		
		addConverter((Converter<String, LocalDate>) source -> LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
		
		addConverter((Converter<LocalTime, String>) source -> source.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
		
		addConverter((Converter<String, LocalTime>) source -> LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss.SSS")));
	}
	
	public void addConverter(Converter<?, ?> converter){
		service.addConverter(converter);
	}

	public<S, T> void addConverter(Class<S> sourceType, Class<T> targetType, Converter<? super S, ? extends T> converter){
		service.addConverter(sourceType, targetType, converter);
	}
	
	public<T> T convert(Object source, Class<T> targetType){
		return service.convert(source, targetType);
	}
	
	public boolean canConvert(Object source, Class<?> targetType){
		return service.canConvert(source.getClass(), targetType);
	}
	
}
