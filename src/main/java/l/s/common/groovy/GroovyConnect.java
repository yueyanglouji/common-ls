package l.s.common.groovy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;

public class GroovyConnect {

	private Logger log = LoggerFactory.getLogger(getClass());
	
	private Map<String, Object> param;
	
	private String charset;
	
	private GroovyClassLoader classLoader;
	
	public GroovyConnect(GroovyClassLoader classLoader){
		this.param = new HashMap<>();
		this.charset = "UTF-8";
		
		if(classLoader == null){
			this.classLoader = new GroovyClassLoader(GroovyConnect.class.getClassLoader());
		}else{
			this.classLoader = classLoader;
		}
	}
	
	public GroovyConnect setClassLoader(GroovyClassLoader loader){
		this.classLoader = loader;
		
		return this;
	}
	
	public GroovyConnect loadGroovySource(String file) throws Exception{
		File f = new File(file);
		if(!f.exists()){
			throw new FileNotFoundException(f.getAbsolutePath());
		}
		
		load(f);
		return this;
	}
	
	public Class<?> loadClass(String className) throws Exception{
		return classLoader.loadClass(className);
	}
	
	public GroovyObject newInstance(String className) throws Exception{
		Class<?> c = loadClass(className);
		groovy.lang.GroovyObject go = (groovy.lang.GroovyObject)c.newInstance();
		
		return new GroovyObject(go);
	}
	
	@SuppressWarnings("unchecked")
	public <T> T newInstance(String className, Class<T> retType) throws Exception{
		Class<?> c = loadClass(className);
		return (T)c.newInstance();
	}
	
	private void load(File f) throws Exception{
		if(f.isDirectory()){
			File[] files = f.listFiles(ff->{
				if(ff.isDirectory()){
					return true;
				}
				if(ff.getName().toLowerCase().endsWith(".groovy")){
					return true;
				}
				return false;
			});
			
			if(files != null){
				for(File ff : files){
					load(ff);
				}
			}
		}else{
			Class<?> c = classLoader.parseClass(f);
			log.debug("load class : " + c);
		}
		
	}
	
	public GroovyConnect param(String key, Object value){
		param.put(key, value);
		return this;
	}
	
	public GroovyConnect charset(String charset){
		this.charset = charset;
		return this;
	}
	
	public Object runFile(String scriptFile) throws Exception{
		
		if(scriptFile == null){
			return null;
		}
		
	    Binding bb = new Binding(); 
	    if(param != null){
	    	for(Entry<String, Object> e : param.entrySet()){
	    		String key = e.getKey();
	    		Object value = e.getValue();
	    		
	    		bb.setProperty(key, value);
	    	}
	    }
	    
	    GroovyShell gs = new GroovyShell(classLoader,bb);
	    
	    InputStream in = new FileInputStream(scriptFile);
	    Reader reader = new InputStreamReader(in, charset);
	    Object ret = gs.evaluate(reader); 
	    reader.close();
	    in.close();
	    return ret;
	}

	public Object run(String scriptText) throws Exception{

		if(scriptText == null || scriptText.equals("")){
			return null;
		}

		Binding bb = new Binding();
		if(param != null){
			for(Entry<String, Object> e : param.entrySet()){
				String key = e.getKey();
				Object value = e.getValue();

				bb.setProperty(key, value);
			}
		}

		GroovyShell gs = new GroovyShell(classLoader,bb);

		Object ret = gs.evaluate(scriptText);
		return ret;
	}
}
