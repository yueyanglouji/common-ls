package l.s.common.config;

import l.s.common.bean.BeanConverter;
import l.s.common.config.resource.config.ClassPathResource;
import l.s.common.config.resource.config.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigPropertyLoader {

	private final Logger log = LoggerFactory.getLogger(getClass());

    private Resource[] locations;

    private String fileEncoding = "UTF-8";

	protected Properties map;

	protected List<Properties> allMap;

	private BeanConverter converter;

	public void setConverter(BeanConverter converter) {
		this.converter = converter;
	}

	public ConfigPropertyLoader(){
		map = new Properties();
		this.allMap = new ArrayList<>();
		this.converter = BeanConverter.getDefault();
	}

	public void loadProperties(){

		for (Resource location : locations) {
			try {
				URL baseUrl = location.getURL();
				if (location.getClass() == ClassPathResource.class) {
					ClassPathResource classPathResource = (ClassPathResource) location;
					ClassLoader classLoader = classPathResource.getClassLoader();
					if(classLoader == null){
						throw new RuntimeException("classloader not found.");
					}
					Enumeration<URL> enumeration = classLoader.getResources(classPathResource.getPath());
					while (enumeration.hasMoreElements()) {
						URL url = enumeration.nextElement();
						if (url.equals(baseUrl)) {
							continue;
						}
						loadResource(url);
					}
				}
				loadResource(baseUrl);
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new RuntimeException(e);
			}
		}
		
	}

	private void loadResource(URL url) throws Exception{
		try(
				InputStream fin = url.openStream();
				InputStreamReader in = new InputStreamReader(fin, fileEncoding)
		){
			Properties properties = new Properties();
			properties.load(in);
			this.allMap.add(properties);

			map.putAll(properties);
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

	@SuppressWarnings("unchecked")
	public<T> T getProperty(String key, Class<T> clazz){
		Object value = map.get(key);
		if(clazz == null){
			return (T)getProperty(key);
		}
		if(value == null){
			if(!clazz.isPrimitive()){
				return null;
			}else {
				throw new RuntimeException("cannot convert null to " + clazz.getName());
			}
		}
		if(converter.canConvert(value, clazz)){
			return converter.convert(value, clazz);
		}else{
			return (T)value;
		}
	}

	@SuppressWarnings("unchecked")
	public<T> T getProperty(String key, T defaultValue){
		if(defaultValue == null){
			return (T)getProperty(key);
		}
		return getPropertyWithDefault(key, defaultValue);
	}

	@SuppressWarnings("unchecked")
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
