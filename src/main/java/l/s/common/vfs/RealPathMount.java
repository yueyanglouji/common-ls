package l.s.common.vfs;

import l.s.common.vfs.spi.FileSystem;
import l.s.common.vfs.spi.RealFileSystem;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class RealPathMount extends Mount{
    Path realPath;

    VirtualFile mountPoint;

    RealFileSystem realFileSystem;

    public RealPathMount(TempFileProvider tempFileProvider, VirtualFile virtualFile, VirtualFileType type, Path realPath) {
        super(tempFileProvider, virtualFile, type);
        this.realPath = realPath;
        this.mountPoint = virtualFile.getRoot().get();
        this.realFileSystem = new RealFileSystem(virtualFile.getRoot().mountPath.toFile());
    }

    public Path getRealPath() {
        return realPath;
    }

    @Override
    public List<VirtualFile> listFiles() {
        try (Stream<Path> list = Files.list(realPath)) {
            return list.map(p -> {
                        return virtualFile.get(p.getFileName().toString());
                    })
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files in " + realPath, e);
        }
    }

    @Override
    public List<VirtualFile> listFiles(VirtualFileFilter filter) {
        return Collections.emptyList();
    }

    @Override
    public boolean exists() {
        return realFileSystem.exists(virtualFile);
    }

    @Override
    public boolean isDirectory() {
        return Files.isDirectory(realPath);
    }

    @Override
    public boolean isFile() {
        return Files.isRegularFile(realPath);
    }

    @Override
    public boolean isSymbolicLink() {
        return false;
    }

    @Override
    public void mkdirs() {
        if(type == VirtualFileType.NOT_EXISTS){
            //Failed to create directory Ignored;
            realPath.toFile().mkdirs();
        }
    }

    @Override
    public InputStream openInputStream() throws IOException {
        return Files.newInputStream(realPath);
    }

    @Override
    public OutputStream openOutputStream() throws IOException {
        return Files.newOutputStream(realPath);
    }

    @Override
    public void mkdir() {
        if(type == VirtualFileType.NOT_EXISTS){
            //Failed to create directory Ignored;
            realPath.toFile().mkdir();
        }
    }

    @Override
    public FileSystem getFileSystem() {
        return realFileSystem;
    }

    @Override
    public long getSize() {
        return realFileSystem.getSize(virtualFile);
    }

    @Override
    public long getLastModified() {
        return realFileSystem.getLastModified(virtualFile);
    }

    @Override
    public URI toURI() throws URISyntaxException {
        return virtualFile.toURI();
    }
}
