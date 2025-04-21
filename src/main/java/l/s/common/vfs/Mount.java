package l.s.common.vfs;

import l.s.common.vfs.spi.FileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Stream;

public abstract class Mount {

    TempFileProvider tempFileProvider;

    VirtualFile virtualFile;

    VirtualFileType type;

    public abstract List<VirtualFile> listFiles();

    public abstract List<VirtualFile> listFiles(VirtualFileFilter filter);

    public Mount(TempFileProvider tempFileProvider, VirtualFile virtualFile, VirtualFileType type){
        this.tempFileProvider = tempFileProvider;
        this.virtualFile = virtualFile;
        this.type = type;
    }

    public abstract boolean exists();

    public abstract boolean isDirectory();

    public abstract boolean isFile();

    public abstract boolean isSymbolicLink();

    public abstract void mkdirs() throws IOException;

    public abstract InputStream openInputStream() throws IOException;

    public abstract OutputStream openOutputStream() throws IOException;

    public abstract void mkdir();

    public abstract FileSystem getFileSystem();

    public abstract long getSize();

    public abstract long getLastModified();

    public abstract URI toURI() throws URISyntaxException;

    public void walk(WalkFunction function) throws IOException {
        getFileSystem().walk(virtualFile, function);
    }

    public List<VirtualFile> walkToList() throws IOException {
        return getFileSystem().walkToList(virtualFile);
    }

    public Mount reMount() throws IOException {
        return virtualFile.mount();
    }
}
