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

		addConverter(new Converter<Date, String>() {
			@Override
			public String convert(Date source) {
				SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
				return f.format(source);
			}
		});

		addConverter(new Converter<String, Date>() {
			@Override
			public Date convert(String source) {
				try {
					SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
					return f.parse(source);
				} catch (ParseException e) {
					throw new RuntimeException(e);
				}
			}
		});

		addConverter(new Converter<LocalDateTime, String>() {
			@Override
			public String convert(LocalDateTime source) {
				ZonedDateTime zdt = DateUtil.toZoneDateTime(source);
				return zdt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
			}
		});

		addConverter(new Converter<String, LocalDateTime>() {
			@Override
			public LocalDateTime convert(String source) {
				ZonedDateTime zdt = ZonedDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
				return DateUtil.toLocalDateTime(zdt);
			}
		});

		addConverter(new Converter<ZonedDateTime, String>() {
			@Override
			public String convert(ZonedDateTime source) {
				return source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
			}
		});

		addConverter(new Converter<String, ZonedDateTime>() {
			@Override
			public ZonedDateTime convert(String source) {
				ZonedDateTime zdt = ZonedDateTime.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"));
				return zdt;
			}
		});

		addConverter(new Converter<LocalDate, String>() {
			@Override
			public String convert(LocalDate source) {
				return source.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
		});

		addConverter(new Converter<String, LocalDate>() {
			@Override
			public LocalDate convert(String source) {
				return LocalDate.parse(source, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
			}
		});

		addConverter(new Converter<LocalTime, String>() {
			@Override
			public String convert(LocalTime source) {
				return source.format(DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
			}
		});

		addConverter(new Converter<String, LocalTime>() {
			@Override
			public LocalTime convert(String source) {
				return LocalTime.parse(source, DateTimeFormatter.ofPattern("HH:mm:ss.SSS"));
			}
		});
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
