package l.s.common.csv;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class CSVHeader {
	
	List<String> list;
	
	private Map<String, Integer> map;
	
	public CSVHeader(Map<String, Integer> map){
		this.map = map;
		this.list = new ArrayList<>();
		if(map == null){
			return;
		}
		
		List<Map<String, Object>> temp = new ArrayList<>();
		for(Entry<String, Integer> e : map.entrySet()){
			String header = e.getKey();
			Integer index = e.getValue();
			
			Map<String, Object> tempmap = new HashMap<>();
			tempmap.put("header", header);
			tempmap.put("index", index);
			
			temp.add(tempmap);
		}
		
		Collections.sort(temp,  new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				int index1 = (int)o1.get("index");
				int index2 = (int)o2.get("index");
				return index1 - index2;
			}
		});
		
		for(Map<String, Object> m : temp){
			String header = (String)m.get("header");
			list.add(header);
		}
	}

	public String[] getHeaderArray(){
		String[] ret = new String[list.size()];
		return list.toArray(ret);
	}
	
	public List<String> getHeaderList(){
		return list;
	}
	
	public int size(){
		return list.size();
	}
	
	public Map<String, Integer>getHeaderMap(){
		return map;
	}
	
}
