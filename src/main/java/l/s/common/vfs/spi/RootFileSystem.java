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

import l.s.common.vfs.VirtualFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSigner;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A special FileSystem which supports multiple roots.
 * <p/>
 * This is currently accomplished by requiring that VirtualFile.getPathName()
 * produce output that is consumable by java.io.File as a path.
 */
public final class RootFileSystem implements FileSystem {

    public static final RootFileSystem ROOT_INSTANCE = new RootFileSystem();

    private RootFileSystem() {
    }

    /**
     * {@inheritDoc}
     */
    public InputStream openInputStream(VirtualFile target) throws IOException {
        return new FileInputStream(getFile(target));
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
        return new File(target.getPath());
    }

    /**
     * {@inheritDoc}
     */
    public boolean delete(VirtualFile target) {
        return getFile(target).delete();
    }

    /**
     * {@inheritDoc}
     */
    public long getSize(VirtualFile target) {
        return getFile(target).length();
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModified(VirtualFile target) {
        return getFile(target).lastModified();
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(VirtualFile target) {
        return getFile(target).exists();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFile(final VirtualFile target) {
        return getFile(target).isFile();
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirectory(VirtualFile target) {
        return getFile(target).isDirectory();
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getDirectoryEntries(VirtualFile target) {
        final String[] names = getFile(target).list();
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
        return null;
    }

    public URI getRootURI() throws URISyntaxException {
        return null;
    }
}
