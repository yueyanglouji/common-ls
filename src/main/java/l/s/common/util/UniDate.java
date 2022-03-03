package l.s.common.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UniDate {

	private static final Logger log = LoggerFactory.getLogger(UniDate.class);
	
	private UniDate(){
		
	}
	
	private String uniDateString;
	
	private Date date;
	
	public static synchronized UniDate getUniDate(){
		
		try {
			Thread.sleep(1);
		} catch (InterruptedException e) {
			log.error("Class UniDate error: ", e);
		}
		
		Date date = new Date();
		UniDate unidate = new UniDate();
		unidate.date = date;
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmssSSS");
		unidate.uniDateString = format.format(date);
		log.debug("create UniDate: " + unidate);
		return unidate;
		
	}

	public String getUniString() {
		return uniDateString;
	}

	public Date getDate() {
		return date;
	}
	
} 
