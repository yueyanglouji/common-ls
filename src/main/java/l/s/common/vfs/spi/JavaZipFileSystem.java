package l.s.common.vfs.spi;

import l.s.common.vfs.TempDir;
import l.s.common.vfs.VFSUtils;
import l.s.common.vfs.VirtualFile;
import l.s.common.vfs.util.PathTokenizer;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.security.CodeSigner;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public final class JavaZipFileSystem implements FileSystem {

    //private final File zipFile;
    private final File archiveFile;
    private final ZipNode rootNode;
    private final TempDir tempDir;
    private final File contentsDir;
    VirtualFile zipVirtualFile;

    private static final Map<String, ZipNode> ZIP_NODE_CACHE;

    static {
        ZIP_NODE_CACHE = new ConcurrentHashMap<>();
    }

    /**
     * Create a new instance.
     *
     * @param name        the name of the source archive
     * @param inputStream an input stream from the source archive
     * @param tempDir     the temp dir into which zip information is stored
     * @throws IOException if an I/O error occurs
     */
    public JavaZipFileSystem(String name, InputStream inputStream, TempDir tempDir, VirtualFile zipVirtualFile) throws IOException {
        this(tempDir.createFile(name, inputStream), tempDir, zipVirtualFile);
    }

    public JavaZipFileSystem(Path archiveFile, TempDir tempDir, VirtualFile zipVirtualFile) throws IOException {
        this(archiveFile.toFile(), tempDir, zipVirtualFile);
    }

    /**
     * Create a new instance.
     *
     * @param archiveFile the original archive file
     * @param tempDir     the temp dir into which zip information is stored
     * @throws IOException if an I/O error occurs
     */
    public JavaZipFileSystem(File archiveFile, TempDir tempDir, VirtualFile zipVirtualFile) throws IOException {
        this.archiveFile = archiveFile;
        this.tempDir = tempDir;
        this.zipVirtualFile = zipVirtualFile;
        String fileKey = archiveFile.getAbsolutePath();
        ZipNode cachedZipNode = ZIP_NODE_CACHE.get(fileKey);
        if(cachedZipNode != null) {
            this.rootNode = cachedZipNode;
            contentsDir = tempDir.getFile("contents");
            return;
        }
        try(
            JarFile zipFile = new JarFile(archiveFile)
        ){
            final Enumeration<? extends JarEntry> entries = zipFile.entries();
            final ZipNode rootNode = new ZipNode(new HashMap<String, ZipNode>(), "", null);
            FILES:
            for (JarEntry entry : iter(entries)) {
                final String entryName = entry.getName();
                final boolean isDirectory = entry.isDirectory();
                final List<String> tokens = PathTokenizer.getTokens(entryName);
                ZipNode node = rootNode;
                final Iterator<String> it = tokens.iterator();
                while (it.hasNext()) {
                    String token = it.next();
                    if (PathTokenizer.isCurrentToken(token) || PathTokenizer.isReverseToken(token)) {
                        // invalid file name
                        continue FILES;
                    }
                    final Map<String, ZipNode> children = node.children;
                    if (children == null) {
                        // todo - log bad zip entry
                        continue FILES;
                    }
                    ZipNode child = children.get(token);
                    if (child == null) {
                        child = it.hasNext() || isDirectory ? new ZipNode(new HashMap<String, ZipNode>(), token, null) : new ZipNode(null, token, entryName);
                        children.put(token, child);
                    }
                    node = child;
                }
            }
            ZIP_NODE_CACHE.put(fileKey, rootNode);
            this.rootNode = rootNode;
            contentsDir = tempDir.getFile("contents");
            contentsDir.mkdir();
        }
    }

    /**
     * {@inheritDoc}
     */
    private static <T> Iterable<T> iter(final Enumeration<T> entries) {
        return new EnumerationIterable<T>(entries);
    }

    /**
     * {@inheritDoc}
     */
    public File getFile(VirtualFile target) throws IOException {
        final ZipNode zipNode = getExistingZipNode(target);
        // check if we have cached one already
        File cachedFile = zipNode.cachedFile;
        if (cachedFile != null) {
            return cachedFile;
        }
        synchronized (zipNode) {
            // double-check
            cachedFile = zipNode.cachedFile;
            if (cachedFile != null) {
                return cachedFile;
            }

            // nope, create a cached temp
            final String zipEntry = zipNode.entry;
            String name = target.getPathRelativeTo(zipVirtualFile);
            cachedFile = buildFile(contentsDir, name);
            if (zipEntry == null) {
                cachedFile.mkdir();
            } else {
                try(
                    JarFile zipFile = new JarFile(archiveFile);
                    FileOutputStream fileOutputStream = new FileOutputStream(cachedFile);
                    BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream)
                ){
                    VFSUtils.copyStreamAndClose(zipFile.getInputStream(zipFile.getEntry(zipEntry)), bufferedOutputStream);
                }
            }
            zipNode.cachedFile = cachedFile;
            return cachedFile;
        }
    }

    /**
     * {@inheritDoc}
     */
    public InputStream openInputStream(VirtualFile target) throws IOException {
        final ZipNode zipNode = getExistingZipNode(target);
        File cachedFile = zipNode.cachedFile;
        if (cachedFile != null) {
            return new FileInputStream(cachedFile);
        }
        if (rootNode == zipNode) {
            return new FileInputStream(archiveFile);
        }
        cachedFile = getFile(target);
        if (cachedFile == null) {
            throw new IOException("notAFile " + target.getPath());
        }
        return new FileInputStream(cachedFile);
    }

    /**
     * {@inheritDoc}
     */
    public boolean delete(VirtualFile target) {
        final ZipNode zipNode = getZipNode(target);
        if (zipNode == null) {
            return false;
        }
        final File cachedFile = zipNode.cachedFile;
        return cachedFile != null && cachedFile.delete();
    }

    /**
     * {@inheritDoc}
     */
    public long getSize(VirtualFile target) {
        final ZipNode zipNode = getZipNode(target);
        if (zipNode == null) {
            return 0L;
        }
        File cachedFile = zipNode.cachedFile;
        if (zipNode == rootNode) {
            return archiveFile.length();
        }
        if (cachedFile == null) {
            try{
                cachedFile = getFile(target);
            }catch (Exception ignored){}
        }
        if (cachedFile == null) {
            return 0L;
        }
        return cachedFile.length();
    }

    /**
     * {@inheritDoc}
     */
    public long getLastModified(VirtualFile target) {
        final ZipNode zipNode = getZipNode(target);
        if (zipNode == null) {
            return 0L;
        }
        File cachedFile = zipNode.cachedFile;
        if (zipNode == rootNode) {
            return archiveFile.lastModified();
        }
        if (cachedFile == null) {
            try{
                cachedFile = getFile(target);
            }catch (Exception ignored){}
        }
        if (cachedFile == null) {
            return 0L;
        }
        return cachedFile.lastModified();
    }

    /**
     * {@inheritDoc}
     */
    public boolean exists(VirtualFile target) {
        final ZipNode zipNode = rootNode.find(zipVirtualFile, target);
        if (zipNode == null) {
            return false;
        } else {
            final File cachedFile = zipNode.cachedFile;
            return cachedFile == null || cachedFile.exists();
        }
    }

    /**
     * {@inheritDoc}
     */
    public boolean isFile(final VirtualFile target) {
        final ZipNode zipNode = rootNode.find(zipVirtualFile, target);
        return zipNode != null && zipNode.entry != null;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isDirectory(VirtualFile target) {
        final ZipNode zipNode = rootNode.find(zipVirtualFile, target);
        return zipNode != null && zipNode.entry == null;
    }

    /**
     * {@inheritDoc}
     */
    public List<String> getDirectoryEntries(VirtualFile target) {
        final ZipNode zipNode = rootNode.find(zipVirtualFile, target);
        if (zipNode == null) {
            return Collections.emptyList();
        }
        final Map<String, ZipNode> children = zipNode.children;
        if (children == null) {
            return Collections.emptyList();
        }
        final Collection<ZipNode> values = children.values();
        final List<String> names = new ArrayList<String>(values.size());
        for (ZipNode node : values) {
            names.add(node.name);
        }
        return names;
    }

    @Override
    public List<VirtualFile> walkToList(VirtualFile target) throws IOException {
        final ZipNode zipNode = rootNode.find(zipVirtualFile, target);
        if (zipNode == null) {
            return Collections.emptyList();
        }
        List<VirtualFile> list = new ArrayList<>();
        walkToList(zipNode, target, list);
        return list;
    }

    private void walkToList(ZipNode node, VirtualFile target, List<VirtualFile> list) throws IOException {
        final Map<String, ZipNode> children = node.children;
        if (children == null) {
            return;
        }
        final Collection<ZipNode> values = children.values();
        for(ZipNode child : values) {
            VirtualFile vf = target.get(child.name);
            list.add(vf);
            walkToList(child, vf, list);
        }
    }

    /**
     * {@inheritDoc}
     */
    public CodeSigner[] getCodeSigners(VirtualFile target) {
        final ZipNode zipNode = getZipNode(target);
        if (zipNode == null) {
            return null;
        }
        try(
            JarFile zipFile = new JarFile(archiveFile)
        ){
            String entry = zipNode.entry;
            JarEntry jarEntry = zipFile.getJarEntry(entry);
            if(jarEntry != null){
                return null;
            }
            return jarEntry.getCodeSigners();
        }catch (IOException e) {
            return null;
        }

    }

    private ZipNode getZipNode(VirtualFile target) {
        return rootNode.find(zipVirtualFile, target);
    }

    private ZipNode getExistingZipNode(VirtualFile target)
            throws FileNotFoundException {
        final ZipNode zipNode = rootNode.find(zipVirtualFile, target);
        if (zipNode == null) {
            throw new FileNotFoundException(target.getPath());
        }
        return zipNode;
    }

    /**
     * {@inheritDoc}
     */
    public boolean isReadOnly() {
        return true;
    }

    /**
     * {@inheritDoc}
     */
    public File getMountSource() {
        return archiveFile;
    }

    public URI getRootURI() throws URISyntaxException {
        return new URI("jar", archiveFile.toURI().toString() + "!/", null);
    }

    private File buildFile(File contentsDir, String name) {
        List<String> tokens = PathTokenizer.getTokens(name);
        File currentFile = contentsDir;
        for (String token : tokens) {
            currentFile = new File(currentFile, token);
        }
        currentFile.getParentFile().mkdirs();
        return currentFile;
    }

    private static final class ZipNode {

        // immutable child map
        private final Map<String, ZipNode> children;
        private final String name;
        private final String entry;
        private volatile File cachedFile;

        private ZipNode(Map<String, ZipNode> children, String name, String entry) {
            this.children = children;
            this.name = name;
            this.entry = entry;
        }

        private ZipNode find(VirtualFile zip, VirtualFile target) {
            if (zip == target) {
                return this;
            } else {
                final ZipNode parent = find(zip, target.getParent());
                if (parent == null) {
                    return null;
                }
                final Map<String, ZipNode> children = parent.children;
                if (children == null) {
                    return null;
                }
                return children.get(target.getName());
            }
        }
    }
}