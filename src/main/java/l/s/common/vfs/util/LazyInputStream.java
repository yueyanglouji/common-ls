package l.s.common.vfs.util;

import l.s.common.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;

public class LazyInputStream extends InputStream {
    private VirtualFile file;
    private InputStream stream;

    public LazyInputStream(VirtualFile file) {
        if (file == null) {
            throw new NullPointerException("file");
        }
        this.file = file;
    }

    /**
     * Open stream.
     *
     * @return file's stream
     * @throws IOException for any IO error
     */
    protected synchronized InputStream openStream() throws IOException {
        if (stream == null) { stream = file.mount().openInputStream(); }
        return stream;
    }

    @Override
    public int read() throws IOException {
        return openStream().read();
    }

    @Override
    public int read(byte[] b) throws IOException {
        return openStream().read(b);
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        return openStream().read(b, off, len);
    }

    @Override
    public long skip(long n) throws IOException {
        return openStream().skip(n);
    }

    @Override
    public int available() throws IOException {
        return openStream().available();
    }

    @Override
    public synchronized void close() throws IOException {
        if (stream == null) { return; }

        openStream().close();
        stream = null; // reset the stream
    }

    @Override
    public void mark(int readlimit) {
        try {
            openStream().mark(readlimit);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void reset() throws IOException {
        openStream().reset();
    }

    @Override
    public boolean markSupported() {
        try {
            return openStream().markSupported();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
