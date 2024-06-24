package org.jboss.modules;

import l.s.common.vfs.VFS;
import org.jboss.modules.filter.PathFilters;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

public class JarFileModuleFinder extends FileSystemClassPathModuleFinder {
    ModuleLoader baseModuleLoader;
    List<String> dependencies;
    public JarFileModuleFinder(ModuleLoader baseModuleLoader) {
        this(baseModuleLoader, null);
    }
    public JarFileModuleFinder(ModuleLoader baseModuleLoader, List<String> dependencies) {
        super(baseModuleLoader);
        this.baseModuleLoader = baseModuleLoader;
        this.dependencies = dependencies;
    }
    public ModuleSpec findModule(final String name, final ModuleLoader delegateLoader) throws ModuleLoadException {
        final Path path = VFS.getBase(name).getPath();
        if (! path.isAbsolute()) {
            return null;
        }
        final Path normalizedPath = path.normalize();
        if (! path.equals(normalizedPath)) {
            return null;
        }
        try {
            final Manifest manifest;
            final String fileName = path.toString();
            final ModuleSpec.Builder builder = ModuleSpec.build(fileName);
            final ResourceLoader resourceLoader;
            final ModuleLoader fatModuleLoader;
            final ModuleLoader baseModuleLoader = this.baseModuleLoader;

            // assume some kind of JAR file
            final JarFile jarFile = JDKSpecific.getJarFile(path.toFile(), true);
            try {
                try {
                    manifest = jarFile.getManifest();
                } catch (IOException e) {
                    throw new ModuleLoadException("Failed to load MANIFEST from " + path, e);
                }
                resourceLoader = new JarFileResourceLoader(fileName, jarFile);
            } catch (Throwable t) {
                try {
                    jarFile.close();
                } catch (Throwable e2) {
                    e2.addSuppressed(t);
                    throw e2;
                }
                throw t;
            }
            fatModuleLoader = new DelegatingModuleLoader(baseModuleLoader, new JarFileModuleFinder(baseModuleLoader));
            // now build the module specification from the manifest information
            try {
                builder.setModuleClassLoaderFactory(new WarModuleClassLoaderFactory());
                addSelfContent(builder, resourceLoader);
                addSelfDependency(builder);
                final Attributes mainAttributes = manifest.getMainAttributes();
                setMainClass(builder, mainAttributes);
                addClassPathDependencies(builder, delegateLoader, path, mainAttributes);
                final ModuleLoader extensionModuleLoader = EMPTY_MODULE_LOADER_SUPPLIER.get();
                addExtensionDependencies(builder, mainAttributes, extensionModuleLoader);
                addModuleDependencies(builder, fatModuleLoader, mainAttributes);
                setModuleVersion(builder, mainAttributes);
                addCommonDependencies(builder);
                addSystemDependencies(builder);
                addPermissions(builder, resourceLoader, delegateLoader);
            } catch (Throwable t) {
                resourceLoader.close();
                throw t;
            }
            return builder.create();
        } catch (IOException e) {
            throw new ModuleLoadException(e);
        }
    }

    private void addCommonDependencies(final ModuleSpec.Builder builder) {

        builder.addDependency(new LocalDependencySpecBuilder()
                .setLocalLoader(ClassLoaderLocalLoader.SYSTEM)
                .setLoaderPaths(LocalModuleParser.COMMON)
                .build());
    }

//    void addModuleDependencies(final ModuleSpec.Builder builder, final ModuleLoader fatModuleLoader, final Attributes mainAttributes) {
//        final String dependencies = mainAttributes.getValue(DEPENDENCIES);
//        String[] dependencyEntries = Utils.NO_STRINGS;
//        if(this.dependencies != null){
//            dependencyEntries = this.dependencies.toArray(new String[]{});
//        }else if(dependencies != null){
//            dependencyEntries = dependencies.split("\\s*,\\s*");
//        }
//        for (String dependencyEntry : dependencyEntries) {
//            boolean optional = false;
//            boolean export = false;
//            boolean services = false;
//            dependencyEntry = dependencyEntry.trim();
//            if (! dependencyEntry.isEmpty()) {
//                String[] fields = dependencyEntry.split("\\s+");
//                if (fields.length < 1) {
//                    continue;
//                }
//                String moduleName = fields[0];
//                for (int i = 1; i < fields.length; i++) {
//                    String field = fields[i];
//                    if (field.equals(OPTIONAL)) {
//                        optional = true;
//                    } else if (field.equals(EXPORT)) {
//                        export = true;
//                    } else if (field.equals(SERVICES)) {
//                        services = true;
//                    }
//                    // else ignored
//                }
//                builder.addDependency(new ModuleDependencySpecBuilder()
//                        .setImportServices(services)
//                        .setExport(export)
//                        .setModuleLoader(fatModuleLoader)
//                        .setName(moduleName)
//                        .setOptional(optional)
//                        .build());
//            }
//        }
//    }
    void addClassPathDependencies(final ModuleSpec.Builder builder, final ModuleLoader moduleLoader, final Path path, final Attributes mainAttributes) {
        final String classPath = mainAttributes.getValue(Attributes.Name.CLASS_PATH);
        String[] classPathEntries = Utils.NO_STRINGS;
        if(dependencies != null){
            classPathEntries = dependencies.toArray(new String[]{});
        }else if(classPath != null){
            classPathEntries = classPath.split("\\s+");
        }
        for (String entry : classPathEntries) {
            if (!entry.isEmpty()) {
                URI uri;
                try {
                    uri = new URI(entry);
                    if (uri.getScheme() == null) {
                        Path p = VFS.getBase(entry).getPath();
                        if (p.toFile().exists()) {
                            uri = p.toFile().getCanonicalFile().toURI();
                        } else {
                            p = VFS.getBase(path).get("../" + entry).getPath();
                            if (p.toFile().exists()) {
                                uri = p.toFile().getCanonicalFile().toURI();
                            } else {
                                continue;
                            }
                        }

                    }
                } catch (URISyntaxException | IOException e) {
                    // ignore invalid class path entries
                    continue;
                }
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

}
