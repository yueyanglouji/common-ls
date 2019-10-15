package l.s.common.config;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import l.s.common.bean.BeanConverter;
import org.springframework.core.io.Resource;

public class ConfigProperty {

	private static ConfigProperty configProperty;

    private Resource[] locations;
    
    private String fileEncoding = "UTF-8";

	protected Map<String, Object> map;

	private BeanConverter converter;

	public void setConverter(BeanConverter converter) {
		this.converter = converter;
	}

	private ConfigProperty(){
		map = new HashMap<>();
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
			Scanner in = null;
			try{
				Resource resource = locations[i];
				File file = resource.getFile();
				in = new Scanner(file, fileEncoding);
				while(in.hasNextLine()){
					String line = in.nextLine();
					if(line.startsWith("#")){
						continue;
					}
					int index = line.indexOf('=');
					if(index == -1){
						continue;
					}
					String key = line.substring(0,index);
					String value = line.substring(index + 1);
					map.put(key, value);
				}
			}catch (Exception e){

			}finally {
				if(in != null){
					try {
						in.close();
					}catch (Exception e){
					}
				}
			}

		}
		
	}

	public Object getProperty(String key){
		return  map.get(key);
	}

	public<T> T getProperty(String key, Class<T> clazz){
		Object value = map.get(key);
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

	public<T> T getProperty(String key, T defaultvalue){
		Object value = map.get(key);
		if(value == null){
			return defaultvalue;
		}
		if(converter.canConvert(value, defaultvalue.getClass())){
			return (T)converter.convert(value, defaultvalue.getClass());
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
