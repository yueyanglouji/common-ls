package l.s.common.vfs;

import l.s.common.vfs.spi.FileSystem;
import l.s.common.vfs.spi.JavaZipFileSystem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

public class JavaZipMount extends Mount{

    Path zipFile;

    VirtualFile mountPoint;

    JavaZipFileSystem javaZipFileSystem;

    public JavaZipMount(TempFileProvider tempFileProvider, VirtualFile virtualFile, VirtualFileType type, VirtualFile mountPoint, Path zipFile) throws IOException {
        super(tempFileProvider, virtualFile, type);
        init(tempFileProvider, virtualFile, type, mountPoint, zipFile);
    }

    private void init(TempFileProvider tempFileProvider, VirtualFile virtualFile, VirtualFileType type, VirtualFile mountPoint, Path zipFile) throws IOException {
        this.zipFile = zipFile;
        this.mountPoint = mountPoint;
        if(this.mountPoint == null){
            throw new IOException("Zip file mount point is not found.");
        }
        javaZipFileSystem = new JavaZipFileSystem(zipFile, tempFileProvider.createOrGetTempDir(zipFile), mountPoint);
        if(virtualFile.equals(mountPoint)){
            return;
        }
        if(!javaZipFileSystem.exists(virtualFile)){
            VirtualFile internalMountPoint = javaZipFileSystem.getExistsParent(virtualFile);
            if(mountPoint.equals(internalMountPoint)){
                return;
            }
            File maybeInternalZipFile = mountPoint.mount().getFileSystem().getFile(internalMountPoint);
            if(VirtualFile.isJavaZipFile(maybeInternalZipFile)){
                init(tempFileProvider, virtualFile, type, internalMountPoint, maybeInternalZipFile.toPath());
            }
        }else{
            File maybeInternalZipFile = mountPoint.mount().getFileSystem().getFile(virtualFile);
            if(VirtualFile.isJavaZipFile(maybeInternalZipFile)){
                init(tempFileProvider, virtualFile, type, virtualFile, maybeInternalZipFile.toPath());
            }
        }
    }

    public Mount reMount() throws IOException {
        JavaZipFileSystem.clearCache(zipFile.toFile());
        return super.reMount();
    }

    public Path getZipFile() {
        return zipFile;
    }

    public VirtualFile getMountPoint() {
        return mountPoint;
    }

    @Override
    public List<VirtualFile> listFiles() {
        List<String> directoryEntries = getFileSystem().getDirectoryEntries(virtualFile);
        return directoryEntries.stream().map(x -> virtualFile.get(x)).collect(Collectors.toList());
    }

    @Override
    public List<VirtualFile> listFiles(VirtualFileFilter filter) {
        List<String> directoryEntries = getFileSystem().getDirectoryEntries(virtualFile);
        return directoryEntries.stream().map(x -> virtualFile.get(x)).filter(filter::accepts).collect(Collectors.toList());
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
