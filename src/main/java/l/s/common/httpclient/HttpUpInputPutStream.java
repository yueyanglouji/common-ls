package l.s.common.httpclient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class HttpUpInputPutStream{

	private List<Object> sourceList;
	
	public HttpUpInputPutStream() {
		
		this.sourceList = new ArrayList<>();
		
	}
	
	public void add(String text){
		sourceList.add(text);
	}
	
	public void add(InputStream stream){
		sourceList.add(stream);
	}
	
	public void addFirst(String text){
		this.sourceList.add(0, text);
	}
	
	public void addFirst(InputStream stream){
		sourceList.add(0,stream);
	}
	
	public void write(HttpUpStreamEntity out, String charset) throws Exception{
		for(int i=0;i<sourceList.size();i++){
			Object s = sourceList.get(i);
			if(s.getClass() == String.class){
				String str = (String)s;
				out.add(str, charset);
			}
			else if(InputStream.class.isAssignableFrom(s.getClass())){
				InputStream in = (InputStream)s;
				out.add(in);
			}
		}
	}
	
	public int size(){
		return sourceList.size();
	}
	
}
