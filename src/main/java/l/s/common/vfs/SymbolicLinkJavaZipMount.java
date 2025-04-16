package l.s.common.vfs;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public class SymbolicLinkJavaZipMount extends JavaZipMount{

    Path symbolicLinkPath;

    public SymbolicLinkJavaZipMount(TempFileProvider tempFileProvider, VirtualFile virtualFile, VirtualFileType type, Path zipFile, List<String> entryList, Path symbolicLinkPath) throws IOException {
        super(tempFileProvider, virtualFile, type, zipFile, entryList);
        this.symbolicLinkPath = symbolicLinkPath;
    }

    public Path getSymbolicLinkPath(){
        return symbolicLinkPath;
    }

    @Override
    protected VirtualFile getMountPoint(VirtualFile virtualFile) {
        if(virtualFile.getMountPath().equals(symbolicLinkPath)){
            return virtualFile;
        }else{
            if(virtualFile.isRoot()){
                return null;
            }
            return getMountPoint(virtualFile.getParent());
        }
    }
}
