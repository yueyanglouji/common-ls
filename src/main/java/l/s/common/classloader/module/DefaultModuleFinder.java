package l.s.common.classloader.module;

import l.s.common.vfs.VirtualFile;
import org.jboss.modules.DependencySpec;
import org.jboss.modules.LocalDependencySpecBuilder;
import org.jboss.modules.ModuleFinder;
import org.jboss.modules.ModuleLoadException;
import org.jboss.modules.ModuleLoader;
import org.jboss.modules.ModuleSpec;
import org.jboss.modules.ResourceLoader;
import org.jboss.modules.ResourceLoaderSpec;
import org.jboss.modules.filter.MultiplePathFilterBuilder;
import org.jboss.modules.filter.PathFilter;
import org.jboss.modules.filter.PathFilters;

import java.io.IOException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.List;

import static l.s.common.classloader.module.JbossAccessor.MODULES_DIR;

public class DefaultModuleFinder implements ModuleFinder {

    ModuleLoader baseModuleLoader;

    AccessControlContext context;

    VirtualFile[] jarDir;

    VirtualFile[] classPaths;

    private static final PathFilter NO_MODULES_DIR;

    static {
        final MultiplePathFilterBuilder builder = PathFilters.multiplePathFilterBuilder(true);
        builder.addFilter(PathFilters.is(MODULES_DIR), false);
        builder.addFilter(PathFilters.isChildOf(MODULES_DIR), false);
        NO_MODULES_DIR = builder.create();
    }

    public DefaultModuleFinder(ModuleLoader baseModuleLoader, VirtualFile[] classPaths){
        this(baseModuleLoader, new VirtualFile[]{}, classPaths);
    }

    public DefaultModuleFinder(ModuleLoader baseModuleLoader, VirtualFile[] jarDir, VirtualFile[] classPaths){
        this.baseModuleLoader = baseModuleLoader;
        this.context = AccessController.getContext();
        this.jarDir = jarDir;
        this.classPaths = classPaths;
    }

    @Override
    public ModuleSpec findModule(final String name, final ModuleLoader delegateLoader) throws ModuleLoadException {
        //VirtualFile war = workDir.get(name);
        final ModuleSpec.Builder builder = ModuleSpec.build(name);

        if(classPaths != null){
            for(VirtualFile it : classPaths){
                dependencyClassPath(it, builder);
            }
        }
        if(jarDir != null){
            try {
                for (VirtualFile it : jarDir) {
                    dependencyLibJars(it, builder);
                }
            } catch (IOException e) {
                throw new ModuleLoadException(e);
            }
        }
        addCommonDependencies(builder);
        addSelfDependency(builder);
        addSystemDependencies(builder);

        return builder.create();
    }

    void addSelfDependency(final ModuleSpec.Builder builder) {
        // add our own dependency
        builder.addDependency(DependencySpec.OWN_DEPENDENCY);
    }

    void addSystemDependencies(final ModuleSpec.Builder builder) {
        builder.addDependency(new LocalDependencySpecBuilder()
                .setLocalLoader(JbossAccessor.SYSTEM)
                .setLoaderPaths(JbossAccessor.JDK)
                .build());
    }

    private void dependencyClassPath(VirtualFile path, ModuleSpec.Builder builder) {
        ResourceLoader resourceLoader = new VirtualFileResourceLoader(path.toString(), path, context);
        try {
            //builder.addResourceRoot(ResourceLoaderSpec.createResourceLoaderSpec(resourceLoader, NO_MODULES_DIR));
            addSelfContent(builder, resourceLoader);
        } catch (Throwable t) {
            resourceLoader.close();
            throw t;
        }
    }

    void addSelfContent(final ModuleSpec.Builder builder, final ResourceLoader resourceLoader) {
        // add our own content
        builder.addResourceRoot(ResourceLoaderSpec.createResourceLoaderSpec(resourceLoader, NO_MODULES_DIR));
    }

    private void dependencyLibJars(VirtualFile virtualFile, ModuleSpec.Builder builder) throws IOException {
        if(!virtualFile.mount().exists()){
            return;
        }
        List<VirtualFile> list = virtualFile.mount().listFiles();
        list.forEach(it -> {
            if(it.getName().endsWith(".jar")){
                ResourceLoader resourceLoader = new VirtualFileResourceLoader(it.toString(), it, context);
                try {
                    //builder.addResourceRoot(ResourceLoaderSpec.createResourceLoaderSpec(resourceLoader, NO_MODULES_DIR));
                    addSelfContent(builder, resourceLoader);
                } catch (Throwable t) {
                    resourceLoader.close();
                    throw t;
                }
            }
        });

    }

    private void addCommonDependencies(final ModuleSpec.Builder builder) {
        builder.addDependency(new LocalDependencySpecBuilder()
                .setLocalLoader(JbossAccessor.SYSTEM)
                .setLoaderPaths(LocalModuleParser.COMMON)
                .build());
    }
}
