package l.s.common.groovy;

import groovy.lang.GroovyClassLoader;

public class GroovyS {
	
	public static GroovyConnect connect(){
		return new GroovyConnect(null);
	}
	
	public static GroovyConnect connect(GroovyClassLoader loader){
		return new GroovyConnect(loader);
	}

}
