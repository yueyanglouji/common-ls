package l.s.common.vfs;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TempDir implements Closeable {

    private final TempFileProvider provider;
    private final File root;
    private final AtomicBoolean open = new AtomicBoolean(true);

    TempDir(TempFileProvider provider, File root) {
        this.provider = provider;
        this.root = root;
    }

    /**
     * Get the {@code File} that represents the root of this temporary directory.  The returned file is only valid as
     * long as the tempdir exists.
     *
     * @return the root file
     * @throws IOException if the directory was closed at the time of this invocation
     */
    public File getRoot() throws IOException {
        if (!open.get()) {
            throw new IOException("tempDirectoryClosed");
        }
        return root;
    }

    /**
     * Get the {@code File} for a relative path.  The returned file is only valid as long as the tempdir exists.
     *
     * @param relativePath the relative path
     * @return the corresponding file
     * @throws IOException if the directory was closed at the time of this invocation
     */
    public File getFile(String relativePath) throws IOException {
        if (!open.get()) {
            throw new IOException("tempDirectoryClosed");
        }
        return new File(root, relativePath);
    }

    /**
     * Create a file within this temporary directory, prepopulating the file from the given input stream.
     *
     * @param relativePath the relative path name
     * @param sourceData   the source input stream to use
     * @return the file
     * @throws IOException if the directory was closed at the time of this invocation or an error occurs
     */
    public File createFile(String relativePath, InputStream sourceData) throws IOException {
        final File tempFile = getFile(relativePath);
        boolean ok = false;
        try {
            final FileOutputStream fos = new FileOutputStream(tempFile);
            try {
                VFSUtils.copyStream(sourceData, fos);
                fos.close();
                sourceData.close();
                ok = true;
                return tempFile;
            } finally {
                VFSUtils.safeClose(fos);
            }
        } finally {
            VFSUtils.safeClose(sourceData);
            if (!ok) {
                tempFile.delete();
            }
        }
    }

    /**
     * Close this directory.  The contents of the directory will be removed.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public void close() throws IOException {
        if (open.getAndSet(false)) {
            provider.delete(root);
        }
    }

    protected void finalize() throws Throwable {
        VFSUtils.safeClose(this);
    }
}
