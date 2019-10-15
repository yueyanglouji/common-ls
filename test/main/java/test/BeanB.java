package test;

import java.time.LocalDateTime;
import java.util.Date;

public class BeanB {

	private String[]ccc;
	
	private BeanA ddd;
	
	private Date date = new Date();

	public String[] getCcc() {
		return ccc;
	}

	public void setCcc(String[] ccc) {
		this.ccc = ccc;
	}

	public BeanA getDdd() {
		return ddd;
	}

	public void setDdd(BeanA ddd) {
		this.ddd = ddd;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
}
