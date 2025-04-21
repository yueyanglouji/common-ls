package l.s.common.classloader.module;

import l.s.common.vfs.Mount;
import l.s.common.vfs.VirtualFile;
import org.jboss.modules.AbstractResourceLoader;
import org.jboss.modules.ClassSpec;
import org.jboss.modules.IterableResourceLoader;
import org.jboss.modules.PackageSpec;
import org.jboss.modules.PathUtils;
import org.jboss.modules.Resource;
import org.jboss.modules.ResourceLoader;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.InvalidPathException;
import java.security.AccessControlContext;
import java.security.AccessController;
import java.security.CodeSigner;
import java.security.CodeSource;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Manifest;

public class VirtualFileResourceLoader extends AbstractResourceLoader implements IterableResourceLoader {

    private final String rootName;
    protected final VirtualFile root;
    protected final AccessControlContext context;

    private final Manifest manifest;
    private final CodeSource codeSource;

    public VirtualFileResourceLoader(final String rootName, final VirtualFile root, final AccessControlContext context) {
        if (rootName == null) {
            throw new IllegalArgumentException("rootName is null");
        }
        if (root == null) {
            throw new IllegalArgumentException("root is null");
        }
        if (context == null) {
            throw new IllegalArgumentException("context is null");
        }
        this.rootName = rootName;
        this.root = root;
        this.context = context;
        final VirtualFile manifestFile = root.get("META-INF").get("MANIFEST.MF");
        manifest = readManifestFile(manifestFile);

        try {
            codeSource = doPrivilegedIfNeeded(context, MalformedURLException.class, () -> new CodeSource(root.toURL(), (CodeSigner[]) null));
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid root file specified", e);
        }
    }

    private Manifest readManifestFile(final VirtualFile manifestFile) {
        try {
            return doPrivilegedIfNeeded(context, IOException.class, () -> {
                Mount mount = manifestFile.mount();
                if (mount.isDirectory()) {
                    return null;
                }

                try (InputStream is = mount.openInputStream()) {
                    return new Manifest(is);
                }
            });
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    @Deprecated
    public String getRootName() {
        return rootName;
    }

    @Override
    public String getLibrary(String name) {
        try{
            final String mappedName = System.mapLibraryName(name);
            for (String path : JbossAccessor.NATIVE_SEARCH_PATHS) {
                VirtualFile testFile;
                try {
                    testFile = root.get(path).get(mappedName);
                } catch (InvalidPathException ignored) {
                    return null;
                }
                Mount mount = testFile.mount();
                if (mount.exists()) {
                    return mount.getFileSystem().getFile(testFile).toPath().toAbsolutePath().toString();
                }
            }
        }catch (Exception ignored){
            return null;
        }
        return null;
    }

    @Override
    public ClassSpec getClassSpec(final String fileName) throws IOException {
        final VirtualFile file;
        try {
            file = root.get(fileName);
        } catch (InvalidPathException ignored) {
            return null;
        }

        return doPrivilegedIfNeeded(context, IOException.class, () -> {
            Mount mount = file.mount();
            if (!mount.exists()) {
                return null;
            }
            final ClassSpec spec = new ClassSpec();
            spec.setCodeSource(codeSource);
            try(
                InputStream inputStream = mount.getFileSystem().openInputStream(file);
                BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ){
                int n = -1;
                byte[] buffer = new byte[1024];
                while ((n = bufferedInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, n);
                }
                spec.setBytes(outputStream.toByteArray());
            }
            return spec;
        });
    }

    @Override
    public PackageSpec getPackageSpec(final String name) throws IOException {
        URL rootUrl = doPrivilegedIfNeeded(context, IOException.class, root::toURL);
        return getPackageSpec(name, manifest, rootUrl);
    }

    @Override
    public Resource getResource(final String name) {
        final String cleanName = PathUtils.canonicalize(PathUtils.relativize(name));
        final VirtualFile file;
        try {
            file = root.get(cleanName);
        } catch (InvalidPathException ignored) {
            return null;
        }

        if (!doPrivilegedIfNeeded(context, () -> file.mount().exists())) {
            return null;
        }

        return new VirtualFileResource(file, cleanName, context);
    }

    @Override
    public Iterator<Resource> iterateResources(final String startPath, final boolean recursive) {
        try {
            VirtualFile virtualFile;
            try {
                virtualFile = root.get(PathUtils.canonicalize(PathUtils.relativize(startPath)));
            } catch (InvalidPathException ignored) {
                return Collections.emptyIterator();
            }

            return walk(virtualFile, recursive).iterator();
        } catch (Exception e) {
            return Collections.emptyIterator();
        }
    }

    private List<Resource> walk(VirtualFile virtualFile, boolean recursive) throws IOException {
        List<Resource> list = new ArrayList<>();
        Mount mount = virtualFile.mount();
        for (VirtualFile file : mount.listFiles()) {
            if (!mount.isDirectory()){
                list.add(new VirtualFileResource(file, PathUtils.toGenericSeparators(file.getPathRelativeTo(root)), context));
            }
            else if(recursive) {
                list.addAll(walk(file, true));
            }
        }
        return list;
    }

    @Override
    public Collection<String> getPaths() {
        try {
            List<String> list = new ArrayList<>();
            root.mount().walk(vf -> {
                final String result = vf.getPathRelativeTo(root);
                String canonical = PathUtils.toGenericSeparators(result);

                // JBoss modules expect folders not to end with a slash, so we have to strip it.
                if (canonical.endsWith("/")) {
                    canonical = canonical.substring(0, canonical.length() - 1);
                }
                list.add(canonical);
            });
            return list;
        } catch (IOException e) {
            return Collections.emptyList();
        }
    }

    @Override
    public URI getLocation() {
        return doPrivilegedIfNeeded(context, root::toURI);
    }

    public ResourceLoader createSubloader(final String relativePath, final String rootName) {
        return new VirtualFileResourceLoader(rootName, root.get(PathUtils.relativize(PathUtils.canonicalize(relativePath))), context);
    }

    static <T, E extends Throwable> T doPrivilegedIfNeeded(AccessControlContext context, Class<E> exceptionType, PrivilegedExceptionAction<T> action) throws E {
        SecurityManager sm = System.getSecurityManager();

        if (sm == null) {
            try {
                return action.run();
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                if (exceptionType.isInstance(e)) {
                    throw exceptionType.cast(e);
                }
                throw new UndeclaredThrowableException(e);
            }
        } else {
            try {
                return AccessController.doPrivileged(action, context);
            } catch (PrivilegedActionException e) {
                try {
                    throw e.getException();
                } catch (RuntimeException re) {
                    throw re;
                } catch (Exception e1) {
                    if (exceptionType.isInstance(e1)) {
                        throw exceptionType.cast(e1);
                    }
                    throw new UndeclaredThrowableException(e1);
                }
            }
        }
    }

    static <T> T doPrivilegedIfNeeded(AccessControlContext context, PrivilegedExceptionAction<T> action) {
        SecurityManager sm = System.getSecurityManager();

        if (sm == null) {
            try {
                return action.run();
            } catch (RuntimeException re) {
                throw re;
            } catch (Exception e) {
                throw new UndeclaredThrowableException(e);
            }
        } else {
            try {
                return AccessController.doPrivileged(action, context);
            } catch (PrivilegedActionException e) {
                try {
                    throw e.getException();
                } catch (RuntimeException re) {
                    throw re;
                } catch (Exception e1) {
                    throw new UndeclaredThrowableException(e1);
                }
            }
        }
    }
}
