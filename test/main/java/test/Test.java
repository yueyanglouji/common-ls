package test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.hc.core5.util.Timeout;

import l.s.common.csv.CSV;
import l.s.common.csv.CSVRead;
import l.s.common.csv.CSVRow;
import l.s.common.csv.CSVWrite;
import l.s.common.csv.QuoteMode;
import l.s.common.httpclient.AsyncHttpClient;
import l.s.common.httpclient.Response;
import l.s.common.json.JsonNode;

public class Test{
	
	private AsyncHttpClient client;
	
	public Test(){
		this.client = AsyncHttpClient.getNewInstance().useCookeStore(false).connectTimenout(Timeout.ofSeconds(200));
	}
	
	public void wxinit() throws Exception{
		
		Response r = client.connect("http://baidu.com")
		.get();
		
		System.out.println(r.getHeader().getHeaders());
		System.out.println(r.getContent().getContent());
		
	}
	
	public void testcsv() throws Exception{
		
	}
	
	
	public static void main(String[] args) throws Exception{
		Test test = new Test();
		test.testcsv();
		test.wxinit();
		
		BeanA bean = new BeanA();
		
		bean.setAaa("aaa");
		bean.setCcc(1.223);
		BeanB b = new BeanB();
		b.setCcc(new String[]{"ccc1", "ccc2"});
		//bean.setBbb(b);
		
		
		
		Map<String, Object> map = new HashMap<>();
		map.put("key", "value");
		map.put("key1", "value");
		map.put("key2", "value");
		
		bean.setMap(map);
		
		
		JsonNode node = JsonNode.createFromBeanObject(bean);
		
		node.append("bbb", b);
		
		System.out.println(node.toJsonString());
		
		BeanA a = node.toBean(BeanA.class);
		
		System.out.println(a.getBbb().getDate().toString());
		
		node = JsonNode.createFromBeanObject(a);
		
		System.out.println(node.toJsonString());
		//{"aaa":"aaa", "bbb":{"ccc":["ccc1", "ccc2"]}}
		//{"aaa":"aaa", "bbb":{"ccc":["ccc1", "ccc2"]}}
	}
}
