package l.s.common.vfs.spi;

import l.s.common.vfs.RealPathMount;
import l.s.common.vfs.VFSUtils;
import l.s.common.vfs.VirtualFile;
import l.s.common.vfs.WalkFunction;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.nio.file.Paths;
import java.security.CodeSigner;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static java.security.AccessController.doPrivileged;

public final class RealFileSystem implements FileSystem {

    private final File realRoot;
    private final boolean privileged;

    /**
     * Construct a real filesystem with the given real root.
     *
     * @param realRoot the real root
     */
    public RealFileSystem(File realRoot) {
        this(realRoot, true);
    }

    /**
     * Construct a real filesystem with the given real root.
     *
     * @param realRoot   the real root
     * @param privileged {@code true} to check permissions once up front, {@code false} to check at access time
     */
    public RealFileSystem(File realRoot, boolean privileged) {
        if (privileged) {
            final SecurityManager sm = System.getSecurityManager();
            if (sm != null) {
                sm.checkPermission(new FilePermission(new File(realRoot, "-").getPath(), "read,delete"));
            }
        }
        // the transformation is for case insensitive file systems. This helps to ensure that the rest of the path matches exactly the canonical form
        File canonicalRoot = realRoot;
        try {
            canonicalRoot = canonicalRoot.getCanonicalFile();
        } catch(IOException e) {
            canonicalRoot = Paths.get(canonicalRoot.getAbsolutePath()).normalize().toFile();
        }
        this.realRoot = canonicalRoot;
        this.privileged = privileged;
    }

    private static <T> T doIoPrivileged(PrivilegedExceptionAction<T> action) throws IOException {
        try {
            return doPrivileged(action);
        } catch (PrivilegedActionException pe) {
            try {
                throw pe.getException();
            } catch (IOException | RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UndeclaredThrowableException(e);
            }
        }
    }

    public InputStream openInputStream(final VirtualFile target) throws IOException {
        return privileged ? doIoPrivileged(new PrivilegedExceptionAction<InputStream>() {
            public InputStream run() throws Exception {
                return new FileInputStream(getFile(target));
            }
        }) : new FileInputStream(getFile(target));
    }

    public boolean isReadOnly() {
        return false;
    }

    public File getFile(VirtualFile target) {
        try {
            return ((RealPathMount)target.mount()).getRealPath().toFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean delete(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return file.delete();
            }
        }) : file.delete();
    }

    public long getSize(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Long>() {
            public Long run() {
                return file.length();
            }
        }) : file.length();
    }

    public long getLastModified(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Long>() {
            public Long run() {
                return file.lastModified();
            }
        }) : file.lastModified();
    }

    public boolean exists(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return VFSUtils.exists(file);
            }
        }) : VFSUtils.exists(file);
    }

    public boolean isFile(final VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return file.isFile();
            }
        }) : file.isFile();
    }

    public boolean isDirectory(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return file.isDirectory();
            }
        }) : file.isDirectory();
    }

    public List<String> getDirectoryEntries(VirtualFile target) {
        final File file = getFile(target);
        final String[] names = privileged ? doPrivileged(new PrivilegedAction<String[]>() {
            public String[] run() {
                return file.list();
            }
        }) : file.list();
        Set<String> ret = new HashSet<>();
        if(names != null){
            ret.addAll(Arrays.asList(names));
        }
        List<String> softLinkChildren = VFSUtils.getSoftLinkChildren(target);
        ret.addAll(softLinkChildren);
        ArrayList<String> list = new ArrayList<>(ret);
        list.sort(String::compareToIgnoreCase);
        return list;
    }

    @Override
    public void walk(VirtualFile target, WalkFunction function) {
        List<String> entries = getDirectoryEntries(target);
        for (String f : entries){
            VirtualFile vf = target.get(f);
            function.apply(vf);
            walk(vf, function);
        }
    }

    @Override
    public List<VirtualFile> walkToList(VirtualFile target) throws IOException {
        List<VirtualFile> list = new ArrayList<VirtualFile>();
        walkToList(target, list);
        return list;
    }

    private void walkToList(VirtualFile target, List<VirtualFile> list) throws IOException {
        List<String> entries = getDirectoryEntries(target);
        for (String f : entries){
            VirtualFile vf = target.get(f);
            list.add(vf);
            walkToList(vf, list);
        }

    }

    public CodeSigner[] getCodeSigners(VirtualFile target) {
        return null;
    }

    public File getMountSource() {
        return realRoot;
    }

    public URI getRootURI() {
        return realRoot.toURI();
    }
}
