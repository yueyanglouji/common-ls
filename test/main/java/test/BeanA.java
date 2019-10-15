package test;

import java.util.Map;

public class BeanA {
	
	private String aaa;
	
	private BeanB bbb;
	
	private double ccc;
	
	private Map<String, Object> map;

	public String getAaa() {
		return aaa;
	}

	public void setAaa(String aaa) {
		this.aaa = aaa;
	}

	public BeanB getBbb() {
		return bbb;
	}

	public void setBbb(BeanB bbb) {
		this.bbb = bbb;
	}

	public Map<String, Object> getMap() {
		return map;
	}

	public void setMap(Map<String, Object> map) {
		this.map = map;
	}

	public double getCcc() {
		return ccc;
	}

	public void setCcc(double ccc) {
		this.ccc = ccc;
	}

}
