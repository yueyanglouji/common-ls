package l.s.common.vfs.no;

import l.s.common.util.IoUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VirtualFileAndMappingSystemFile {

    private final Path actualCurrent;

    private final Path virtualCurrent;

    private final Path virtualRoot;

    private final Path actualRoot;

    VirtualFileAndMappingSystemFile(Path virtualBase, Path actualBase, String path, Path virtualRoot, Path actualRoot) {
        String string = virtualBase.toString();
        actualCurrent = Paths.get(actualBase.toString(), path);
        virtualCurrent = new VirtualPath(Paths.get(string, path), actualCurrent, virtualRoot, actualRoot);
        this.virtualRoot = virtualRoot;
        this.actualRoot = actualRoot;
    }

    VirtualFileAndMappingSystemFile(Path virtualBase, Path actualBase, Path virtualRoot, Path actualRoot) {
        this(virtualBase, actualBase, "", virtualRoot, actualRoot);
    }

    public VirtualFileAndMappingSystemFile get(String path) {
        return new VirtualFileAndMappingSystemFile(virtualCurrent, actualCurrent, path, virtualRoot, actualRoot);
    }

    public Path getVirtualPath() {
        return new VirtualPath(virtualCurrent, actualCurrent, virtualRoot, actualRoot);
    }

    public Path getActualPath() {
        return actualCurrent;
    }

    public List<VirtualFileAndMappingSystemFile> listFiles() {
        try (Stream<Path> list = Files.list(actualCurrent)) {
            return list.map(p -> get(p.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files in " + actualCurrent, e);
        }
    }

    @Override
    public String toString() {
        return virtualPathToString();
    }
    public String virtualPathToString() {
        return getVirtualPath().toString();
    }
    public String actualPathToString() {
        return actualCurrent.toString();
    }

    public Path getPath() {
        return getVirtualPath();
    }

    public boolean exists() {
        return Files.exists(actualCurrent);
    }

    public boolean isDirectory() {
        return Files.isDirectory(actualCurrent);
    }

    public boolean isFile() {
        return Files.isRegularFile(actualCurrent);
    }

    public boolean isSymbolicLink() {
        return Files.isSymbolicLink(actualCurrent);
    }

    public VirtualFileAndMappingSystemFile mkdirs() {
        IoUtil.mkdirs(actualCurrent.toFile());
        return this;
    }
}
