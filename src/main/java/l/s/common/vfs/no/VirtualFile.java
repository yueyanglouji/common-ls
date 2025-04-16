package l.s.common.vfs.no;

import l.s.common.util.IoUtil;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class VirtualFile {
    private final Path actualCurrent;

    VirtualFile(Path base, String path) {
        actualCurrent = Paths.get(base.toString(), path);
    }

    VirtualFile(Path base) {
        actualCurrent = base;
    }

    public VirtualFile get(String path) {
        return new VirtualFile(actualCurrent, path);
    }

    public Path getPath() {
        return actualCurrent;
    }

    public List<VirtualFile> listFiles() {
        try (Stream<Path> list = Files.list(actualCurrent)) {
            return list.map(p -> get(p.getFileName().toString()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Failed to list files in " + actualCurrent, e);
        }
    }

    @Override
    public String toString() {
        return actualCurrent.toString();
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

    public VirtualFile mkdirs() {
        IoUtil.mkdirs(actualCurrent.toFile());
        return this;
    }

    public String getFileName() {
        return actualCurrent.getFileName().toString();
    }
}
