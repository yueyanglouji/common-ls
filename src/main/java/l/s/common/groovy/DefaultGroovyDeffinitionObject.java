package l.s.common.groovy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.HashMap;
import java.util.Map;

public final class DefaultGroovyDeffinitionObject<T> extends GroovyObjectSupportDefault{
	
	private Map<String, T> map;
	
	private Class<T> c;
	
	private DefaultGroovyDeffinitionObject(Class<T> c) {
		this.map = new HashMap<>();
		this.c = c;
	}

	public T getDeffField(String property){
		return map.get(property);
	}

	public Map<String, T> getAllDeffField(){
		return this.map;
	}
	
	public void putProperty(String property, T value){
		map.put(property, value);
	}
	
	@Override
	public T getProperty(String property) {
		try {
			T df = map.get(property);
			if(df == null){
				df = c.newInstance();
				map.put(property, df);
			}
			return df;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public static <T> DefaultGroovyDeffinitionObject<T> newInstance(Class<T> c){
		return new DefaultGroovyDeffinitionObject<>(c);
	}

}
