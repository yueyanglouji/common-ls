package l.s.common.vfs;

import l.s.common.vfs.spi.FileSystem;
import l.s.common.vfs.spi.JavaZipFileSystem;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;

public class JavaZipMount extends Mount{

    Path zipFile;

    VirtualFile mountPoint;

    List<String> entryList;

    JavaZipFileSystem javaZipFileSystem;

    public JavaZipMount(TempFileProvider tempFileProvider, VirtualFile virtualFile, VirtualFileType type, Path zipFile, List<String> entryList) throws IOException {
        super(tempFileProvider, virtualFile, type);
        this.zipFile = zipFile;
        if(entryList == null || entryList.isEmpty()){
            this.entryList = Collections.singletonList("/");
        }
        mountPoint = getMountPoint(virtualFile);
        if(mountPoint == null){
            throw new IOException("Zip file mount point is not found.");
        }
        javaZipFileSystem = new JavaZipFileSystem(zipFile, tempFileProvider.createOrGetTempDir(zipFile), mountPoint);
    }

    protected VirtualFile getMountPoint(VirtualFile virtualFile) {
        if(virtualFile.getMountPath().equals(zipFile)){
            return virtualFile;
        }else{
            if(virtualFile.isRoot()){
                return null;
            }
            return getMountPoint(virtualFile.getParent());
        }
    }


    public Path getZipFile() {
        return mountPoint.getMountPath();
    }

    public VirtualFile getMountPoint() {
        return mountPoint;
    }

    public List<String> getEntryList() {
        return entryList;
    }

    @Override
    public List<VirtualFile> listFiles() {
        return Collections.emptyList();
    }

    @Override
    public List<VirtualFile> listFiles(VirtualFileFilter filter) {
        return Collections.emptyList();
    }

    @Override
    public boolean exists() {
        return javaZipFileSystem.exists(virtualFile);
    }

    @Override
    public boolean isDirectory() {
        return javaZipFileSystem.isDirectory(virtualFile);
    }

    @Override
    public boolean isFile() {
        return javaZipFileSystem.isFile(virtualFile);
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public void mkdirs() throws IOException {
        //TODO: implement this method
        //javaZipFileSystem.mkdirs(mountPoint, virtualFile);
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return javaZipFileSystem.openInputStream(virtualFile);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return Files.newOutputStream(javaZipFileSystem.getFile(virtualFile).toPath());
    }

    @Override
    public void mkdir() {
        //TODO: implement this method
        return;
    }

    @Override
    public FileSystem getFileSystem() {
        return javaZipFileSystem;
    }

    @Override
    public long getSize() {
        return javaZipFileSystem.getSize(virtualFile);
    }

    @Override
    public long getLastModified() {
        return javaZipFileSystem.getLastModified(virtualFile);
    }

    @Override
    public URI toURI() throws URISyntaxException {
        return virtualFile.toURI();
    }
}
