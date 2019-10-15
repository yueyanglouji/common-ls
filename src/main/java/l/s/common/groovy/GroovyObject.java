package l.s.common.groovy;

public class GroovyObject{

	private groovy.lang.GroovyObject obj;
	
	public GroovyObject(groovy.lang.GroovyObject obj){
		this.obj = obj;
	}
	
	public Object getProperty(String propertyName){
		return this.obj.getProperty(propertyName);
	}
	
	public Object invokeMethod(String methodName, Object...args){
		return this.obj.invokeMethod(methodName, args);
	}
	
	public void setProperty(String propertyName, Object value){
		this.obj.setProperty(propertyName, value);
	}
}
