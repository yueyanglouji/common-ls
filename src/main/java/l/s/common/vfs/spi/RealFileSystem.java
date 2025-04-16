/*
 * JBoss, Home of Professional Open Source
 * Copyright 2009, JBoss Inc., and individual contributors as indicated
 * by the @authors tag.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package l.s.common.vfs.spi;

import l.s.common.vfs.VFSUtils;
import l.s.common.vfs.VirtualFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.UndeclaredThrowableException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.CodeSigner;
import java.security.PrivilegedAction;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.security.AccessController.doPrivileged;

/**
 * A real filesystem.
 *
 * @author <a href="mailto:david.lloyd@redhat.com">David M. Lloyd</a>
 */
public final class RealFileSystem implements FileSystem {

    private static final boolean NEEDS_CONVERSION = File.separatorChar != '/';

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
            } catch (IOException e) {
                throw e;
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new UndeclaredThrowableException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream openInputStream(final VirtualFile target) throws IOException {
        return privileged ? doIoPrivileged(new PrivilegedExceptionAction<InputStream>() {
            public InputStream run() throws Exception {
                return new FileInputStream(getFile(target));
            }
        }) : new FileInputStream(getFile(target));
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public File getFile(VirtualFile target) {
        return target.getMountPath().toFile();
    }

    /**
     * {@inheritDoc}
     */
    public boolean delete(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return file.delete();
            }
        }) : file.delete();
    }

    /**
     * {@inheritDoc}
     */
    public long getSize(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Long>() {
            public Long run() {
                return file.length();
            }
        }) : file.length();
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModified(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Long>() {
            public Long run() {
                return file.lastModified();
            }
        }) : file.lastModified();
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return VFSUtils.exists(file);
            }
        }) : VFSUtils.exists(file);
    }


    /**
     * {@inheritDoc}
     */
    public boolean isFile(final VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return file.isFile();
            }
        }) : file.isFile();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirectory(VirtualFile target) {
        final File file = getFile(target);
        return privileged ? doPrivileged(new PrivilegedAction<Boolean>() {
            public Boolean run() {
                return file.isDirectory();
            }
        }) : file.isDirectory();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getDirectoryEntries(VirtualFile target) {
        final File file = getFile(target);
        final String[] names = privileged ? doPrivileged(new PrivilegedAction<String[]>() {
            public String[] run() {
                return file.list();
            }
        }) : file.list();
        return names == null ? Collections.<String>emptyList() : Arrays.asList(names);
    }

    @Override
    public List<VirtualFile> walkToList(VirtualFile target) throws IOException {
        final File file = getFile(target);
        List<VirtualFile> list = new ArrayList<VirtualFile>();
        walkToList(file, target, list);
        return list;
    }

    private void walkToList(File file, VirtualFile target, List<VirtualFile> list) throws IOException {
        File[] files = file.listFiles();
        if(files != null){
            for (File f : files){
                VirtualFile vf = target.get(f.getName());
                list.add(vf);
                walkToList(f, vf, list);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    public CodeSigner[] getCodeSigners(VirtualFile target) {
        return null;
    }

    /**
     * {@inheritDoc}
     */
    public File getMountSource() {
        return realRoot;
    }

    public URI getRootURI() throws URISyntaxException {
        return realRoot.toURI();
    }
}
