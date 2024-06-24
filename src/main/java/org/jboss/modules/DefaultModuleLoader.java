package org.jboss.modules;

public class DefaultModuleLoader extends ModuleLoader{

    DefaultModuleFinder finder;

    public DefaultModuleLoader(final DefaultModuleFinder finder) {
        super(new ModuleFinder[] { finder });
        this.finder = finder;
    }

    public boolean unloadModule(final String moduleId, final Module module) throws SecurityException {
        return module.getModuleLoader().unloadModuleLocal(moduleId, module);
    }

    public void close(){
        finder.close();
    }
}
