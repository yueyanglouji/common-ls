package l.s.common.classloader.module;

import org.jboss.modules.Module;
import org.jboss.modules.ModuleFinder;
import org.jboss.modules.ModuleLoader;

public class DefaultModuleLoader extends ModuleLoader {

    DefaultModuleFinder finder;

    public DefaultModuleLoader(final DefaultModuleFinder finder) {
        super(new ModuleFinder[] { finder });
        this.finder = finder;
    }

    public boolean unloadModule(final String moduleId, final Module module) throws SecurityException {
        return JbossAccessor.unloadModule(module.getModuleLoader(), moduleId, module);
    }

    public void close(){
        finder.close();
    }
}
