package org.jboss.modules;

import l.s.common.vfs.VFS;

import java.io.IOException;

public class WarModuleClassLoaderFactory implements ModuleClassLoaderFactory{
    @Override
    public ModuleClassLoader create(ModuleClassLoader.Configuration configuration) {
        if (configuration.getModule().getName().endsWith("WEB-INF/classes") || configuration.getModule().getName().endsWith("WEB-INF\\classes")) {
            return null;
        }else{
            try {
                Module module = Module.getBootModuleLoader().loadModule(VFS.getBase(configuration.getModule().getName()).get("../../../WEB-INF/classes").getPath().toFile().getCanonicalPath());
                return module.getClassLoader();
            } catch (IOException | ModuleLoadException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
