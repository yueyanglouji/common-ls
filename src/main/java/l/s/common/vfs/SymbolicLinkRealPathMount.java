package l.s.common.vfs;

import java.nio.file.Path;

public class SymbolicLinkRealPathMount extends RealPathMount{

    Path symbolicLinkPath;

    public SymbolicLinkRealPathMount(TempFileProvider tempFileProvider, VirtualFile virtualFile, VirtualFileType type, Path realPath, Path symbolicLinkPath) {
        super(tempFileProvider, virtualFile, type, realPath);
        this.symbolicLinkPath = symbolicLinkPath;
    }

    public Path getSymbolicLinkPath() {
        return symbolicLinkPath;
    }
}
