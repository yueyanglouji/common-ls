package l.s.common.classloader;

import l.s.common.classloader.module.DefaultModuleFinder;
import l.s.common.classloader.module.DefaultModuleLoader;
import l.s.common.classloader.module.LocalModuleParser;
import l.s.common.vfs.VirtualFile;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleLoader;

import java.io.IOException;
import java.util.List;

public class ModuleClassLoaderLoader {

    private static volatile ModuleClassLoaderLoader moduleClassLoaderLoader;

    private static final Object O = new Object();

    private ModuleClassLoaderLoader(VirtualFile[] commonSystemJarDir, VirtualFile[] commonSystemClassPath){
        if(commonSystemClassPath != null){
            for(VirtualFile virtualFile : commonSystemClassPath){
                try {
                    LocalModuleParser.process(virtualFile);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        if(commonSystemJarDir != null){
            for(VirtualFile virtualFile : commonSystemJarDir){
                try {
                    List<VirtualFile> list = virtualFile.mount().listFiles();
                    list.forEach(it -> {
                        try {
                            LocalModuleParser.process(it);
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static ModuleClassLoaderLoader getInstance(VirtualFile[] commonSystemJarDir, VirtualFile[] commonSystemClassPath){
        if(moduleClassLoaderLoader == null){
            synchronized (O){
                if(moduleClassLoaderLoader == null){
                    moduleClassLoaderLoader = new ModuleClassLoaderLoader(commonSystemJarDir, commonSystemClassPath);
                }
            }
        }
        return moduleClassLoaderLoader;
    }

    public Module loadModule(String name, VirtualFile workDir) throws Exception{
        return loadModule(name, new VirtualFile[]{workDir.get("lib")}, new VirtualFile[]{workDir.get("classes")});
    }

    public Module loadModule(String name, VirtualFile[] jarDir, VirtualFile[] classPaths) throws Exception{
        ModuleLoader base = Module.getBootModuleLoader();
        DefaultModuleFinder finder = new DefaultModuleFinder(base, jarDir, classPaths);
        DefaultModuleLoader loader = new DefaultModuleLoader(finder);
        return loader.loadModule(name);
    }

    public void unloadModule(String name, Module module){
        ((DefaultModuleLoader)module.getModuleLoader()).unloadModule(name, module);
    }

}
