package l.s.common.config;

import java.util.*;

import l.s.common.bean.BeanConverter;
import l.s.common.config.resource.config.Resource;

public class ConfigProperty {

	private final ConfigPropertyLoader configPropertyLoader;

	private static volatile ConfigProperty configProperty;

	private static final Object O = new Object();

	public void setConverter(BeanConverter converter) {
		configPropertyLoader.setConverter(converter);
	}

	private ConfigProperty(){
		this.configPropertyLoader = new ConfigPropertyLoader();
	}

	public static ConfigProperty getInstance(){
		if(configProperty == null){
			synchronized (O){
				if(configProperty == null){
					configProperty = new ConfigProperty();
				}
			}
		}
		return configProperty;
	}

	public void loadProperties(){
		this.configPropertyLoader.loadProperties();
	}

	public List<Properties> getAllPropertiesLoadedHistoryIncludeOverwrite(){
		return this.configPropertyLoader.getAllPropertiesLoadedHistoryIncludeOverwrite();
	}

	public Map<Object, Object> getAllProperties(){
		return this.configPropertyLoader.getAllProperties();
	}

	public Object getProperty(String key){
		return this.configPropertyLoader.getProperty(key);
	}

	public<T> T getProperty(String key, Class<T> clazz){
		return this.configPropertyLoader.getProperty(key, clazz);
	}

	public<T> T getProperty(String key, T defaultValue){
		return this.configPropertyLoader.getProperty(key, defaultValue);
	}

	public void setProperty(String key, Object value){
		this.configPropertyLoader.setProperty(key, value);
	}

	public void setLocations(Resource[] locations) {
		this.configPropertyLoader.setLocations(locations);
	}
	
	public void setLocation(Resource location) {
		this.configPropertyLoader.setLocation(location);
	}
	
	public void setFileEncoding(String fileEncoding) {
		this.configPropertyLoader.setFileEncoding(fileEncoding);
	}
	
}
