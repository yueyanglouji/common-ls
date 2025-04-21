package l.s.common.vfs;

import java.io.IOException;
import java.nio.file.Path;

public class SymbolicLinkJavaZipMount extends JavaZipMount{

    Path symbolicLinkPath;

    public SymbolicLinkJavaZipMount(TempFileProvider tempFileProvider, VirtualFile virtualFile, VirtualFileType type, VirtualFile mountPoint, Path zipFile, Path symbolicLinkPath) throws IOException {
        super(tempFileProvider, virtualFile, type, mountPoint, zipFile);
        this.symbolicLinkPath = symbolicLinkPath;
    }

    public Path getSymbolicLinkPath(){
        return symbolicLinkPath;
    }

}
