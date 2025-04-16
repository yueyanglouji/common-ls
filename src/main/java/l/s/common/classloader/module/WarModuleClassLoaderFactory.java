package l.s.common.classloader.module;

import l.s.common.vfs.VFS;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.modules.ModuleClassLoaderFactory;
import org.jboss.modules.ModuleLoadException;

public class WarModuleClassLoaderFactory implements ModuleClassLoaderFactory {
    @Override
    public ModuleClassLoader create(ModuleClassLoader.Configuration configuration) {
        if (JbossAccessor.getModule(configuration).getName().endsWith("WEB-INF/classes") || JbossAccessor.getModule(configuration).getName().endsWith("WEB-INF\\classes")) {
            return null;
        }else{
            try {
                Module module = Module.getBootModuleLoader().loadModule(VFS.getRoot().get().get(JbossAccessor.getModule(configuration).getName()).get("../../../WEB-INF/classes").getPath());
                return module.getClassLoader();
            } catch (ModuleLoadException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
