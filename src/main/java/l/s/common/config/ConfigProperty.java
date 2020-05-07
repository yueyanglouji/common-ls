package l.s.common.config;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import l.s.common.bean.BeanConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

public class ConfigProperty {

	private Logger log = LoggerFactory.getLogger(getClass());

	private static ConfigProperty configProperty;

    private Resource[] locations;
    
    private String fileEncoding = "UTF-8";

	protected Properties map;

	protected List<Properties> allMap;

	private BeanConverter converter;

	public void setConverter(BeanConverter converter) {
		this.converter = converter;
	}

	private ConfigProperty(){
		map = new Properties();
		this.allMap = new ArrayList<>();
		this.converter = BeanConverter.getDefault();
	}

	public static ConfigProperty getInstance(){
		if(configProperty == null){
			configProperty = new ConfigProperty();
		}
		return configProperty;
	}

	public void loadProperties(){
		
		for(int i=0;i<locations.length;i++){
			try{
				Resource resource = locations[i];
				if(resource.getClass() == ClassPathResource.class){
					URL baseURL = resource.getURL();
					ClassPathResource classPathResource = (ClassPathResource)resource;
					Enumeration<URL> enumeration = classPathResource.getClassLoader().getResources(classPathResource.getPath());
					while (enumeration.hasMoreElements()){
						URL url = enumeration.nextElement();
						if(url.equals(baseURL)){
							continue;
						}
						loadResource(url);
					}
					loadResource(baseURL);
				}else{
					URL url = resource.getURL();
					loadResource(url);
				}
			}catch (Exception e){
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		
	}

	private void loadResource(URL url) throws Exception{
		try(
				InputStream fin = url.openStream();
				InputStreamReader in = new InputStreamReader(fin, fileEncoding);
		){
			Properties properties = new Properties();
			properties.load(in);
			this.allMap.add(properties);

			for(Map.Entry<Object, Object> en : properties.entrySet()){
				map.put(en.getKey(),en.getValue());
			}
		}
	}

	public List<Properties> getAllPropertiesLoadedHistoryIncludeOverwrite(){
		return allMap;
	}

	public Map<Object, Object> getAllProperties(){
		return map;
	}

	public Object getProperty(String key){
		return  map.get(key);
	}

	public<T> T getProperty(String key, Class<T> clazz){
		Object value = map.get(key);
		if(clazz == null){
			return (T)getProperty(key);
		}
		if(value == null){
			if(!clazz.isPrimitive()){
				return null;
			}
		}
		if(converter.canConvert(value, clazz)){
			return converter.convert(value, clazz);
		}else{
			return (T)value;
		}
	}

	public<T> T getProperty(String key, T defaultValue){
		if(defaultValue == null){
			return (T)getProperty(key);
		}
		return getPropertyWithDefault(key, defaultValue);
	}

	private <T> T getPropertyWithDefault(String key, T defaultValue){
		Object value = map.get(key);
		if(value == null){
			return defaultValue;
		}
		if(converter.canConvert(value, defaultValue.getClass())){
			return (T)converter.convert(value, defaultValue.getClass());
		}else{
			return (T)value;
		}
	}

	public void setProperty(String key, Object value){
		this.map.put(key, value);
	}

	public void setLocations(Resource[] locations) {
		this.locations = locations;
	}
	
	public void setLocation(Resource location) {
		this.locations = new Resource[1];
		this.locations[0] = location;
	}
	
	public void setFileEncoding(String fileEncoding) {
		this.fileEncoding = fileEncoding;
	}
	
}
