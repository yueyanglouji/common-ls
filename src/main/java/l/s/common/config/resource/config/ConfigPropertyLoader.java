package l.s.common.config.resource.config;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.status.StatusLogger;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class ConfigPropertyLoader {

	private static final Logger log = StatusLogger.getLogger();

    private Resource[] locations;

    private String fileEncoding = "UTF-8";

	protected Properties map;

	protected List<Properties> allMap;

	public ConfigPropertyLoader(){
		map = new Properties();
		this.allMap = new ArrayList<>();
	}

	public void loadProperties(){

		for (Resource location : locations) {
			try {
				URL baseURL = location.getURL();
				if (location.getClass() == ClassPathResource.class) {
					ClassPathResource classPathResource = (ClassPathResource) location;
					ClassLoader classLoader = classPathResource.getClassLoader();
					if(classLoader == null){
						throw new RuntimeException("classloader not found.");
					}
					Enumeration<URL> enumeration = classLoader.getResources(classPathResource.getPath());
					while (enumeration.hasMoreElements()) {
						URL url = enumeration.nextElement();
						if (url.equals(baseURL)) {
							continue;
						}
						loadResource(url);
					}
				}
				loadResource(baseURL);
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
