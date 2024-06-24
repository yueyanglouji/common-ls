package l.s.common.vfs;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Stream;

public class VirtualFile {
    private final Path current;

    VirtualFile(Path base, String path) {
        current = Paths.get(base.toString(), path);
    }

    VirtualFile(Path base) {
        current = base;
    }

    public VirtualFile get(String path) {
        return new VirtualFile(current, path);
    }

    public Path getPath() {
        return current;
    }

    public Stream<Path> listFiles() {
        try {
            return Files.list(current);
        } catch (IOException e) {
            return Arrays.stream(new Path[0]);
        }
    }

    @Override
    public String toString() {
        return current.toString();
    }
}
