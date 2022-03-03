package l.s.common.csv;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.csv.CSVRecord;

import l.s.common.util.ReflectUtil;

public class CSVRow {

	private final CSVRecord r;
	
	public CSVRow(CSVRecord r){
		this.r = r;
	}
	
	public String get(String columnName){
		return r.get(columnName);
	}
	
	public String get(int index){
		return r.get(index);
	}
	
	public int size(){
		return r.size();
	}
	
	public List<String> toList(){
		List<String> ret = new ArrayList<>();
		for(int i=0;i<r.size();i++){
			ret.add(r.get(i));
		}
		
		return ret;
	}
	
	public String[] toArray(){
		String[] ret = new String[r.size()];
		for(int i=0;i<r.size();i++){
			ret[i] = r.get(i);
		}
		
		return ret;
	}
	
	public <T> T toObject(Class<T> clazz)throws Exception{
		T o = clazz.newInstance();
		
		ReflectUtil reflectUtil = new ReflectUtil();
		Field[] fields = reflectUtil.getDeclaredFields(clazz);
		if(fields != null){
			for(Field f : fields){
				CSVBeanValue bv = f.getAnnotation(CSVBeanValue.class);
				if(bv == null){
					continue;
				}
				int index = bv.columnIndex();
				if(index == -1){
					String columnName = bv.columnName();
					if(columnName == null || columnName.equals("")){
						continue;
					}
					String value = r.get(columnName);
					f.setAccessible(true);
					f.set(o, value);
				}else{
					String value = r.get(index);
					f.setAccessible(true);
					f.set(o, value);
				}
			}
		}
		
		List<Method> methods = reflectUtil.getDeclaredMethods(clazz);
		if(methods != null){
			for(Method m : methods){
				CSVBeanValue bv = m.getAnnotation(CSVBeanValue.class);
				if(bv == null){
					continue;
				}
				int index = bv.columnIndex();
				if(index == -1){
					String columnName = bv.columnName();
					if(columnName == null || columnName.equals("")){
						continue;
					}
					String value = r.get(columnName);
					m.setAccessible(true);
					m.invoke(o, value);
				}else{
					String value = r.get(index);
					m.setAccessible(true);
					m.invoke(o, value);
				}
			}
		}
		
		return o;
	}
}
