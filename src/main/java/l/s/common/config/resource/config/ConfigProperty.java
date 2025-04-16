package l.s.common.config.resource.config;

import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigProperty {

	private final ConfigPropertyLoader configPropertyLoader;

	private volatile static ConfigProperty configProperty;

	private static final Object o = new Object();

	private ConfigProperty(){
		this.configPropertyLoader = new ConfigPropertyLoader();
	}

	public static ConfigProperty getInstance(){
		if(configProperty == null){
			synchronized (o){
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
