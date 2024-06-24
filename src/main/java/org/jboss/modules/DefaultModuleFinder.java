package org.jboss.modules;

import l.s.common.vfs.VFS;
import l.s.common.vfs.VirtualFile;
import org.jboss.modules.filter.PathFilters;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

public class DefaultModuleFinder extends JarFileModuleFinder {

    ModuleLoader baseModuleLoader;

    AccessControlContext context;

    VirtualFile[] jarDir;

    VirtualFile[] classesDir;

    VirtualFile workDir;

    List<ResourceLoader> resourceLoaderList = new ArrayList<>();

    public DefaultModuleFinder(ModuleLoader baseModuleLoader, VirtualFile workDir, VirtualFile[] jarDir, VirtualFile[] classesDir){
        super(baseModuleLoader);
        this.baseModuleLoader = baseModuleLoader;
        this.context = AccessController.getContext();
        this.jarDir = jarDir;
        this.classesDir = classesDir;
        this.workDir = workDir;
    }

    @Override
    public ModuleSpec findModule(final String name, final ModuleLoader delegateLoader) throws ModuleLoadException {
        VirtualFile war = workDir.get(name);
        final Path path = war.getPath();
        if (! path.isAbsolute()) {
            return null;
        }
        final Path normalizedPath = path.normalize();
        if (! path.equals(normalizedPath)) {
            return null;
        }
        final ModuleSpec.Builder builder = ModuleSpec.build(name);

        for(VirtualFile it : classesDir){
            dependencyClasses(it.getPath(), builder);
        }
        try {
            for (VirtualFile it : jarDir) {
                dependencyLibJars(it.getPath(), builder);
            }
        } catch (IOException e) {
            throw new ModuleLoadException();
        }
        addCommonDependencies(builder);
        addSelfDependency(builder);
        addSystemDependencies(builder);

        return builder.create();
    }

    private void dependencyClasses(Path path, ModuleSpec.Builder builder) {
        ResourceLoader resourceLoader = new PathResourceLoader(path.toString(), path, context);
        try {
            //builder.addResourceRoot(ResourceLoaderSpec.createResourceLoaderSpec(resourceLoader, NO_MODULES_DIR));
            addSelfContent(builder, resourceLoader);
        } catch (Throwable t) {
            resourceLoader.close();
            throw t;
        }
    }

    private void dependencyLibJars(Path path, ModuleSpec.Builder builder) throws IOException {
        if(!path.toFile().exists()){
            return;
        }
        Files.list(path).forEach(it -> {
            if(it.toFile().getName().endsWith(".jar")){
                final JarFile jarFile;
                try {
                    jarFile = JDKSpecific.getJarFile(it.toFile(), true);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                ResourceLoader resourceLoader = new JarFileResourceLoader(it.toString(), jarFile);
                try {
                    //builder.addResourceRoot(ResourceLoaderSpec.createResourceLoaderSpec(resourceLoader, NO_MODULES_DIR));
                    addSelfContent(builder, resourceLoader);
                    this.resourceLoaderList.add(resourceLoader);
                } catch (Throwable t) {
                    resourceLoader.close();
                    throw t;
                }
            }
        });

    }

    private void addCommonDependencies(final ModuleSpec.Builder builder) {
        builder.addDependency(new LocalDependencySpecBuilder()
                .setLocalLoader(ClassLoaderLocalLoader.SYSTEM)
                .setLoaderPaths(LocalModuleParser.COMMON)
                .build());
    }

    void addClassPathDependencies(final ModuleSpec.Builder builder, final ModuleLoader moduleLoader, final Path path, final Attributes mainAttributes) {
        final String[] classPathEntries = dependencies.toArray(new String[]{});
        for (String entry : classPathEntries) {
            if (! entry.isEmpty()) {
                URI uri = VFS.getBase(entry).getPath().toUri();
                final Path depPath = path.resolveSibling(VFS.getBase(uri).getPath()).normalize();
                // simple dependency; class path deps are always optional
                builder.addDependency(new ModuleDependencySpecBuilder()
                        .setImportFilter(PathFilters.acceptAll())
                        .setModuleLoader(moduleLoader)
                        .setName(depPath.toString())
                        .setOptional(true)
                        .build());
            }
        }
    }

    public void close(){
        resourceLoaderList.forEach(ResourceLoader::close);
    }
}
