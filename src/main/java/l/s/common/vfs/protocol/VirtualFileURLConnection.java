package l.s.common.vfs.protocol;

import l.s.common.vfs.Mount;
import l.s.common.vfs.VFS;
import l.s.common.vfs.VirtualFile;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Permission;

class VirtualFileURLConnection extends AbstractURLConnection {
    static final String JAR_CONTENT_TYPE = "application/java-archive";

    private final VirtualFile file;

    VirtualFileURLConnection(URL url) throws IOException {
        super(url);
        file = VFS.getVirtualFile(toURI(url));
    }

    public void connect() throws IOException {
    }

    public Object getContent() throws IOException {
        if (JAR_CONTENT_TYPE.equals(getContentType()) || getContentType() == null) {
            return file;
        }
        return super.getContent();
    }

    public int getContentLength() {
        final long size;
        try {
            Mount mount = file.mount();
            size = mount.getSize();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return size > (long) Integer.MAX_VALUE ? -1 : (int) size;
    }

    public long getLastModified() {
        try {
            Mount mount = file.mount();
            return mount.getLastModified();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public InputStream getInputStream() throws IOException {
        Mount mount = file.mount();
        return mount.openInputStream();
    }

    public Permission getPermission() throws IOException {
        String decodedPath = toURI(url).getPath();
        if (File.separatorChar != '/') { decodedPath = decodedPath.replace('/', File.separatorChar); }

        return new FilePermission(decodedPath, "read");
    }

    @Override
    protected String getName() {
        return file.getName();
    }

    @Override
    public Object getContent(Class[] classes) throws IOException {
        Object obj = super.getContent(classes);

        for (int i = 0; i < classes.length; i++) {
            if (classes[i] == VirtualFile.class) {
                return file;
            } else if (classes[i].isInstance(obj)) {
                return obj;
            }
        }
        return obj;
    }
}
