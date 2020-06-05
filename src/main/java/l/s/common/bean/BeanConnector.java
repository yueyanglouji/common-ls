package l.s.common.bean;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class BeanConnector<T> {

	private BeanWrapper wrapper;
	
	private BeanConverter converter;

	private T bean;
	
	private BeanConnector(){
		
	}

	public static <T> BeanConnector<T> connect(T bean){
		return connect(bean, false);
	}

	public static <T> BeanConnector<T> connect(T bean, boolean supportMap){
		BeanConverter converter = BeanConverter.getDefault();

		BeanWrapper wrapper;
		if(supportMap){
			wrapper = new MapSupportBeanWrapper(bean);
		}else{
			wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);
		}

		wrapper.setConversionService(converter.service);
		wrapper.setAutoGrowNestedPaths(true);
		
		BeanConnector<T> conn = new BeanConnector<T>();
		conn.wrapper = wrapper;
		conn.converter = converter;
		conn.bean = bean;
		
		return conn;
	}

	public T getBean(){
		return this.bean;
	}

	public BeanConnector<T> setProperty(String propertyName, Object value){
		if(wrapper.isWritableProperty(propertyName)){
			wrapper.setPropertyValue(propertyName, value);
		}
		return this;
	}
	
	public Object getProperty(String propertyName){
		if(wrapper.isReadableProperty(propertyName)){
			return wrapper.getPropertyValue(propertyName);
		}
		return null;
	}
	
	public Class<?> getPropertyType(String propertyName){
		return wrapper.getPropertyType(propertyName);
	}
	
	public BeanConverter getBeanConverter(){
		return converter;
	}
	
	public PropertyDescriptor[] getAllPropertyPropertyDescriptors(){
		return wrapper.getPropertyDescriptors();
	}
//	public boolean isReadableProperty(String propertyName){
//		return wrapper.isReadableProperty(propertyName);
//	}
//	
//	public boolean isWritableProperty(String propertyName){
//		return wrapper.isWritableProperty(propertyName);
//	}
	
}
