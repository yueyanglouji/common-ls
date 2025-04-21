package l.s.common.classloader;

import groovy.lang.GroovyClassLoader;
import org.jboss.modules.ModuleClassLoader;

import java.net.URL;

public class GroovyModuleClassLoader extends GroovyClassLoader {

    public GroovyModuleClassLoader(ModuleClassLoader loader) {
        super(loader);
    }

    public void add(URL url) {
        super.addURL(url);
    }

    public void add(URL[] url) {
        if(url == null){
            return;
        }
        for(URL u : url){
            this.add(u);
        }
    }
}
