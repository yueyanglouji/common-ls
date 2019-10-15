package l.s.common.groovy;

import groovy.lang.GroovyClassLoader;

public class GroovyS {
	
	public static GroovyConnect connect(){
		return new GroovyConnect(null);
	}
	
	public static GroovyConnect connect(GroovyClassLoader loader){
		return new GroovyConnect(loader);
	}
	
	public static void main(String[] args) throws Exception{
//		GroovyConnect c = GroovyS.connect().loadGroovySource("./apps/csv/classes");
//		App app = new App();
//		c.param("app", app);
//		c.run("./apps/csv/app.groovy");
//		
//		System.out.println();
//		
//		Class<?> cl = c.loadClass("Test");
//		
//		System.out.println(cl);
//		
//		GroovyObject g = c.newInstance("Test");
//		
//		System.out.println(g.invokeMethod("doit"));
		
	}
}
