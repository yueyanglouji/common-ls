package l.s.common.classloader;

import l.s.common.vfs.VirtualFile;
import org.jboss.modules.DefaultModuleFinder;
import org.jboss.modules.DefaultModuleLoader;
import org.jboss.modules.LocalModuleParser;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoader;

public class ModuleClassLoaderLoader {

    VirtualFile workDir;

    private static volatile ModuleClassLoaderLoader moduleClassLoaderLoader;

    private static final Object O = new Object();

    private ModuleClassLoaderLoader(VirtualFile workDir, VirtualFile[] commonSystemJarDir){
        this.workDir = workDir;
        for(VirtualFile it : commonSystemJarDir){
            LocalModuleParser.processJars(it);
        }
    }

    public static ModuleClassLoaderLoader getInstance(VirtualFile workDir, VirtualFile[] commonSystemJarDir){
        if(moduleClassLoaderLoader == null){
            synchronized (O){
                if(moduleClassLoaderLoader == null){
                    moduleClassLoaderLoader = new ModuleClassLoaderLoader(workDir, commonSystemJarDir);
                }
            }
        }
        return moduleClassLoaderLoader;
    }

    public Module loadModule(String name, VirtualFile workDir) throws Exception{
        return loadModule(name, workDir, new VirtualFile[]{workDir.get("lib")}, new VirtualFile[]{workDir.get("classes")});
    }

    public Module loadModule(String name, VirtualFile workDir, VirtualFile[] jarDir, VirtualFile[] classesDir) throws Exception{

        ModuleLoader base = Module.getBootModuleLoader();
        DefaultModuleFinder finder = new DefaultModuleFinder(base, workDir, jarDir, classesDir);
        DefaultModuleLoader loader = new DefaultModuleLoader(finder);
        return loader.loadModule(name);
    }

    public void unloadModule(String name, Module module){
        ((DefaultModuleLoader)module.getModuleLoader()).unloadModule(name, module);
        ((DefaultModuleLoader)module.getModuleLoader()).close();
    }

}
