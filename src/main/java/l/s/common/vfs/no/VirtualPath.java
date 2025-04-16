package l.s.common.vfs.no;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Iterator;

public class VirtualPath implements Path {

    private final Path actualCurrent;

    private final Path virtualCurrent;

    private final Path virtualRoot;

    private final Path actualRoot;

    VirtualPath(Path virtualCurrent, Path actualCurrent, Path virtualRoot, Path actualRoot) {
        this.virtualCurrent = virtualCurrent.normalize();
        this.actualCurrent = actualCurrent.normalize();
        this.virtualRoot = virtualRoot.normalize();
        this.actualRoot = actualRoot.normalize();

        if (!this.virtualCurrent.startsWith(this.virtualRoot)) {
            throw new IllegalArgumentException("Virtual path must be a sub path of the virtual root");
        }
        if (!this.actualCurrent.startsWith(this.actualRoot)) {
            throw new IllegalArgumentException("Actual path must be a sub path of the actual root");
        }
    }

    @Override
    public FileSystem getFileSystem() {
        return actualCurrent.getFileSystem();
    }

    @Override
    public boolean isAbsolute() {
        return true;
    }

    @Override
    public Path getRoot() {
        return new VirtualPath(virtualRoot, actualRoot, virtualRoot, actualRoot);
    }

    @Override
    public Path getFileName() {
        return virtualCurrent.getFileName();
    }

    @Override
    public Path getParent() {
        return new VirtualPath(virtualCurrent.getParent(), actualCurrent.getParent(), virtualRoot, actualRoot);
    }

    @Override
    public int getNameCount() {
        return virtualCurrent.getNameCount();
    }

    @Override
    public Path getName(int index) {
        return virtualCurrent.getName(index);
    }

    @Override
    public Path subpath(int beginIndex, int endIndex) {
        return virtualCurrent.subpath(beginIndex, endIndex);
    }

    @Override
    public boolean startsWith(Path other) {
        return virtualCurrent.startsWith(other);
    }

    @Override
    public boolean startsWith(String other) {
        return virtualCurrent.startsWith(other);
    }

    @Override
    public boolean endsWith(Path other) {
        return virtualCurrent.endsWith(other);
    }

    @Override
    public boolean endsWith(String other) {
        return virtualCurrent.endsWith(other);
    }

    @Override
    public Path normalize() {
        return this;
    }

    @Override
    public Path resolve(Path other) {
        if(other instanceof VirtualPath){
            return new VirtualPath(virtualCurrent.resolve(((VirtualPath) other).virtualCurrent), actualCurrent.resolve(((VirtualPath) other).actualCurrent), virtualRoot, actualRoot);
        }else {
            return new VirtualPath(virtualCurrent.resolve(other), actualCurrent.resolve(other), virtualRoot, actualRoot);
        }
    }

    @Override
    public Path resolve(String other) {
        return new VirtualPath(virtualCurrent.resolve(other), actualCurrent.resolve(other), virtualRoot, actualRoot);
    }

    @Override
    public Path resolveSibling(Path other) {
        if(other instanceof VirtualPath){
            return new VirtualPath(virtualCurrent.resolveSibling(((VirtualPath) other).virtualCurrent), actualCurrent.resolveSibling(((VirtualPath) other).actualCurrent), virtualRoot, actualRoot);
        }else {
            return new VirtualPath(virtualCurrent.resolveSibling(other), actualCurrent.resolveSibling(other), virtualRoot, actualRoot);
        }

    }

    @Override
    public Path resolveSibling(String other) {
        return new VirtualPath(virtualCurrent.resolveSibling(other), actualCurrent.resolveSibling(other), virtualRoot, actualRoot);
    }

    @Override
    public Path relativize(Path other) {
        Path path;
        if(other instanceof VirtualPath){
            path = virtualCurrent.relativize(((VirtualPath) other).virtualCurrent);
        }else{
            path = virtualCurrent.relativize(other);
        }
        return path;
    }

    @Override
    public URI toUri() {
        return actualCurrent.toUri();
    }

    @Override
    public Path toAbsolutePath() {
        return this;
    }

    @Override
    public Path toRealPath(LinkOption... options) throws IOException {
        return actualCurrent.toRealPath(options);
    }

    @Override
    public File toFile() {
        return actualCurrent.toFile();
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>[] events, WatchEvent.Modifier... modifiers) throws IOException {
        return actualCurrent.register(watcher, events, modifiers);
    }

    @Override
    public WatchKey register(WatchService watcher, WatchEvent.Kind<?>... events) throws IOException {
        return actualCurrent.register(watcher, events);
    }

    @Override
    public Iterator<Path> iterator() {
        return virtualCurrent.iterator();
    }

    @Override
    public int compareTo(Path other) {
        return virtualCurrent.compareTo(other);
    }

    @Override
    public String toString() {
        String str = virtualCurrent.toString().replace('\\', '/');
        if(!Files.exists(actualCurrent)){
            return str;
        }
        if(Files.isDirectory(actualCurrent) && !str.endsWith("/" )){
            return str + "/";
        }else{
            return str;
        }
    }
}
