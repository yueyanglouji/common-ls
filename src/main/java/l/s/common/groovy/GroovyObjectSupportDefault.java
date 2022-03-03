package l.s.common.groovy;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.codehaus.groovy.runtime.NullObject;

import groovy.lang.Closure;
import groovy.lang.GroovyObjectSupport;
import groovy.lang.MissingPropertyException;
import l.s.common.util.ReflectUtil;

public abstract class GroovyObjectSupportDefault extends GroovyObjectSupport implements DelegateClosure{

	private Map<String, DefaultGroovyObject> fieldmap;
	
	public GroovyObjectSupportDefault() {
		fieldmap = new HashMap<>();
	}
	
	@Override
	public Object call(Closure<Object> c) {
		c.setDelegate(this);
		c.setResolveStrategy(Closure.DELEGATE_ONLY);
		return c.call();
	}

	@Override
	public void setProperty(String property, Object newValue) {
		
		if(newValue != null && newValue.getClass() == DefaultGroovyObject.class){
			DefaultGroovyObject defaultGroovyObject = (DefaultGroovyObject)newValue;
			newValue = defaultGroovyObject._Value();
		}
		
		DefaultGroovyObject o = fieldmap.get(property);
		if(o!=null){
			o._Value(newValue);
		}
		
		try{
			super.setProperty(property, newValue);
		}catch(MissingPropertyException e){
			ReflectUtil reflectUtil = new ReflectUtil();
			Field f = reflectUtil.getDeclaredField(getClass(), property);
			if(f != null){
				throw e;
			}
			DefaultGroovyObject defaultGroovyObject = new DefaultGroovyObject();
			defaultGroovyObject._Value(newValue);
			fieldmap.put(property, defaultGroovyObject);
		}
	}

	@Override
	public Object getProperty(String property) {
		Object o = null;
		try {
			o = super.getProperty(property);
		} catch (MissingPropertyException e) {
			o = fieldmap.get(property);
			if(o != null){
				return o;
			}
			
			ReflectUtil reflectUtil = new ReflectUtil();
			Field f = reflectUtil.getDeclaredField(getClass(), property);
			if(f != null){
				throw e;
			}
			DefaultGroovyObject defaultGroovyObject = new DefaultGroovyObject();
			fieldmap.put(property, defaultGroovyObject);
			o = defaultGroovyObject;
			return o;
		}
		
		if(o == null || o.getClass() == NullObject.class){
			try {
				ReflectUtil reflectUtil = new ReflectUtil();
				Field f =reflectUtil.getDeclaredField(this.getClass(), property);
				if(f != null){
					f.setAccessible(true);
					Object value = f.get(this);
					if(value == null || value.getClass() == NullObject.class){
						Object newInstance = f.getType().newInstance();
						f.set(this, newInstance);
						o = newInstance;	
						return o;
					}else{
						return value;
					}
					
				}else{
					return null;
				}
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
			
		}else{
			return o;
		}
	}

	@SuppressWarnings({"unchecked" })
	@Override
	public Object invokeMethod(String name, Object args) {
		if(args.getClass().isArray()){
			Object[] arr = (Object[])args;
			if(arr.length == 1 && Closure.class.isAssignableFrom(arr[0].getClass())){
				Closure<Object> c = (Closure<Object>)arr[0];
				ReflectUtil reflectUtil = new ReflectUtil();
				Method m = reflectUtil.getDeclaredMethod(this.getClass(), name, Closure.class);
				if(m == null){
					Object o = getProperty(name);
					if(o != null && DelegateClosure.class.isAssignableFrom(o.getClass())){
						DelegateClosure dc = (DelegateClosure)o;
						return dc.call(c);
					}
				}
			}
		}
		return super.invokeMethod(name, args);
	}
	
	public DefaultGroovyObject getUndeffinitionField(String property){
		return (DefaultGroovyObject)getProperty(property);
	}
	
	public Object rightShift(String property){
		Object o = getProperty(property);
		if(o.getClass() == DefaultGroovyObject.class){
			DefaultGroovyObject defaultGroovyObject = (DefaultGroovyObject)o;
			return defaultGroovyObject._Value();
		}
		return o;
	}
}
