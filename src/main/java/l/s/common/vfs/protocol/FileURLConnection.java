package l.s.common.vfs.protocol;

import l.s.common.vfs.VFS;
import l.s.common.vfs.VirtualFile;
import l.s.common.vfs.spi.RootFileSystem;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.Permission;

/**
 * Implementation URLConnection that will delegate to the VFS RootFileSystem.
 *
 * @author <a href=mailto:jbailey@redhat.com">John Bailey</a>
 * @version $Revision$
 */
public class FileURLConnection extends AbstractURLConnection {

    private final RootFileSystem rootFileSystem = RootFileSystem.ROOT_INSTANCE;

    private final VirtualFile mountPoint = VFS.getRoot().get();

    private final VirtualFile file;

    public FileURLConnection(URL url) throws IOException {
        super(url);
        file = mountPoint.getRoot().get(toURI(url));
    }

    public File getContent() throws IOException {
        return rootFileSystem.getFile(file);
    }

    public int getContentLength() {
        final long size = rootFileSystem.getSize(file);
        return size > (long) Integer.MAX_VALUE ? -1 : (int) size;
    }

    public long getLastModified() {
        return rootFileSystem.getLastModified(file);
    }

    public InputStream getInputStream() throws IOException {
        return rootFileSystem.openInputStream(file);
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
