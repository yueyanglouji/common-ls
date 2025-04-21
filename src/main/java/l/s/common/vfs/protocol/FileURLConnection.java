package l.s.common.vfs.protocol;

import l.s.common.vfs.VFS;
import l.s.common.vfs.VirtualFile;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Permission;

public class FileURLConnection extends AbstractURLConnection {

    private final VirtualFile file;

    public FileURLConnection(URL url) throws IOException {
        super(url);
        VirtualFile mountPoint = VFS.getVirtualFile(url);
        file = mountPoint.getRoot().getVirtualFile(toURI(url));
    }

    public File getContent() throws IOException {
        return file.mount().getFileSystem().getFile(file);
    }

    public int getContentLength() {
        final long size;
        try {
            size = file.mount().getFileSystem().getSize(file);
        } catch (IOException e) {
            return -1;
        }
        return size > (long) Integer.MAX_VALUE ? -1 : (int) size;
    }

    public long getLastModified() {
        try {
            return file.mount().getFileSystem().getLastModified(file);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getInputStream() throws IOException {
        return file.mount().getFileSystem().openInputStream(file);
    }

    @Override
    public Permission getPermission() throws IOException {
        return new FilePermission(file.getPath(), "read");
    }

    public void connect() throws IOException {
    }

    @Override
    protected String getName() {
        return file.getName();
    }
}
