package l.s.common.bean;

import java.beans.PropertyDescriptor;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.PropertyAccessorFactory;

public class BeanConnector {

	private BeanWrapper wrapper;
	
	private BeanConverter converter;
	
	private BeanConnector(){
		
	}
	
	public static BeanConnector connect(Object bean){
		BeanConverter converter = BeanConverter.getDefault();
		
		BeanWrapper wrapper = PropertyAccessorFactory.forBeanPropertyAccess(bean);

		wrapper.setConversionService(converter.service);
		wrapper.setAutoGrowNestedPaths(true);
		
		BeanConnector conn = new BeanConnector();
		conn.wrapper = wrapper;
		conn.converter = converter;
		
		return conn;
	}
	
	public BeanConnector setProperty(String propertyName, Object value){
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
