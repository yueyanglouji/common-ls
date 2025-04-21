package l.s.common.vfs;

import java.io.IOException;
import java.io.InputStream;
import java.security.CodeSigner;
import java.security.cert.Certificate;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.Iterator;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;

public class VirtualJarInputStream extends JarInputStream {
    private static final String MANIFEST_NAME = "MANIFEST.MF";
    private static final String META_INF_DIR = "META-INF";
    private static final VirtualFileFilter MANIFEST_FILTER = new VirtualFileFilter() {
        public boolean accepts(VirtualFile file) {
            return !MANIFEST_NAME.equalsIgnoreCase(file.getName());
        }
    };
    private final Deque<Iterator<VirtualFile>> entryItr = new ArrayDeque<>();
    private final VirtualFile root;
    private final Manifest manifest;
    private VirtualFile currentVirtualFile = null;
    private InputStream currentEntryStream = VFSUtils.emptyStream();
    private boolean closed;

    /**
     * Construct a {@link VirtualJarInputStream} from a {@link VirtualFile} root
     *
     * @param root VirtualFile directory to use as the base of the virtual Jar.
     * @throws IOException IOException
     */
    public VirtualJarInputStream(VirtualFile root) throws IOException {
        super(VFSUtils.emptyStream());
        this.root = root;
        final VirtualFile manifest = root.get(JarFile.MANIFEST_NAME);

        Mount manifestMount = manifest.mount();
        Mount rootMount = this.root.mount();
        if (manifestMount.exists()) {
            entryItr.add(Collections.singleton(manifest).iterator());
            this.manifest = VFSUtils.readManifest(manifest);
        } else {
            this.manifest = null;
        }
        entryItr.add(rootMount.listFiles().iterator());

    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public ZipEntry getNextEntry() throws IOException {
        return getNextJarEntry();
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public JarEntry getNextJarEntry() throws IOException {
        closeEntry();

        final Iterator<VirtualFile> topItr = entryItr.peekFirst();
        if (topItr == null) {
            return null;
        }
        if (!topItr.hasNext()) {
            entryItr.pop();
            return getNextJarEntry();
        }

        final VirtualFile nextEntry = topItr.next();
        String entryName = getEntryName(nextEntry);

        Mount mount = nextEntry.mount();
        if (mount.isDirectory()) {
            List<VirtualFile> children = mount.listFiles();
            if (entryName.equalsIgnoreCase(META_INF_DIR)) {
                children = mount.listFiles(MANIFEST_FILTER);
            }
            entryItr.add(children.iterator());
            entryName = fixDirectoryName(entryName);
        }

        openCurrent(nextEntry);

        Attributes attributes = null;
        final Manifest manifest = getManifest();
        if (manifest != null) {
            attributes = manifest.getAttributes(entryName);
        }
        return new VirtualJarEntry(entryName, nextEntry, attributes, root);
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public Manifest getManifest() {
        return manifest;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public int read() throws IOException {
        ensureOpen();
        return checkForEoSAndReturn(currentEntryStream.read());
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public int read(byte[] b) throws IOException {
        return read(b, 0, b.length);
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        ensureOpen();
        return checkForEoSAndReturn(currentEntryStream.read(b, off, len));
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public int available() throws IOException {
        ensureOpen();
        return currentEntryStream.available() > 0 ? 1 : 0;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public void close() throws IOException {
        closed = true;
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public void closeEntry() throws IOException {
        currentVirtualFile = null;
        if (currentEntryStream != null) {
            currentEntryStream.close();
        }
    }

    /**
     * {@inheritDoc} *
     */
    @Override
    public long skip(long n) throws IOException {
        ensureOpen();
        return currentEntryStream.skip(n);
    }

    /**
     * {@inheritDoc} *
     */
    private void ensureOpen() throws IOException {
        if (closed) {
            throw new IOException("streamIsClosed");
        }
        if (currentEntryStream == null) {
            if (currentVirtualFile != null) {
                Mount mount = currentVirtualFile.mount();
                currentEntryStream = mount.openInputStream();
            } else {
                currentEntryStream = VFSUtils.emptyStream();
            }
        }
    }

    /**
     * Check to see if the result is the EOF and if so exchange the current entry stream with the empty stream.
     *
     * @param result int
     * @return int result
     * @throws IOException IOException
     */
    private int checkForEoSAndReturn(int result) throws IOException {
        if (result == -1) {
            closeEntry();
            currentEntryStream = VFSUtils.emptyStream();
        }
        return result;
    }

    /**
     * Open the current virtual file as the current JarEntry stream.
     *
     * @param current VirtualFile
     * @throws IOException IOException
     */
    private void openCurrent(VirtualFile current) throws IOException {
        Mount mount = current.mount();
        if (mount.isDirectory()) {
            currentVirtualFile = null;
            currentEntryStream = VFSUtils.emptyStream();
        } else {
            currentVirtualFile = current;
            currentEntryStream = null;
        }
    }

    /**
     * Get the entry name from a VirtualFile.
     *
     * @param entry VirtualFile
     * @return String
     */
    private String getEntryName(VirtualFile entry) {
        return entry.getPathRelativeTo(root);
    }

    /**
     * Make sure directory names end with a trailing slash
     *
     * @param name String
     * @return String
     */
    private String fixDirectoryName(String name) {
        if (!name.endsWith("/")) {
            return name + "/";
        }
        return name;
    }

    public static class VirtualJarEntry extends JarEntry {
        private final VirtualFile virtualFile;
        private final VirtualFile root;
        private final Attributes attributes;

        /**
         * Construct a new
         *
         * @param name String
         * @param virtualFile VirtualFile
         * @param attributes Attributes
         * @param root VirtualFile
         */
        public VirtualJarEntry(String name, VirtualFile virtualFile, Attributes attributes, VirtualFile root) {
            super(name);
            this.virtualFile = virtualFile;
            this.attributes = attributes;
            this.root = root;
        }

        /**
         * {@inheritDoc} *
         */
        @Override
        public Attributes getAttributes() {
            return attributes;
        }

        /**
         * {@inheritDoc} *
         */
        @Override
        public long getSize() {
            try{
                Mount mount = virtualFile.mount();
                return mount.getSize();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        @Override
        public long getTime() {
            try{
                Mount mount = virtualFile.mount();
                return mount.getLastModified();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * {@inheritDoc} *
         */
        @Override
        public boolean isDirectory() {
            try{
                Mount mount = virtualFile.mount();
                return mount.isDirectory();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        /**
         * {@inheritDoc} *
         */
        @Override
        public Certificate[] getCertificates() {
            final CodeSigner[] signers = getCodeSigners();
            if (signers == null) {
                return null;
            }
            final List<Certificate> certs = new ArrayList<>();
            for (CodeSigner signer : signers) {
                certs.addAll(signer.getSignerCertPath().getCertificates());
            }
            return certs.toArray(new Certificate[0]);
        }

        /**
         * {@inheritDoc} *
         */
        @Override
        public CodeSigner[] getCodeSigners() {
            try{
                Mount mount = virtualFile.mount();
                return mount.getFileSystem().getCodeSigners(virtualFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
