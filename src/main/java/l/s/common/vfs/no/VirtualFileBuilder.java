package l.s.common.vfs.no;

import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VirtualFileBuilder<B extends VirtualFileBuilder<B>> {

    private Path actualCurrent;

    private Path virtualCurrent;


    public VirtualFileBuilder() {

    }

    public B actualRoot(String actualPath){
        this.actualCurrent = Paths.get(actualPath);
        return asBuilder();
    }
    public B actualRoot(URI actualPath){
        this.actualCurrent = Paths.get(actualPath);
        return asBuilder();
    }
    public B actualRoot(Path actualPath){
        this.actualCurrent = actualPath;
        return asBuilder();
    }

    public B virtualRoot(String virtualPath){
        this.virtualCurrent = Paths.get(virtualPath);
        return asBuilder();
    }
    public B virtualRoot(URI virtualPath){
        this.virtualCurrent = Paths.get(virtualPath);
        return asBuilder();
    }
    public B virtualRoot(Path virtualPath){
        this.virtualCurrent = virtualPath;
        return asBuilder();
    }

    @SuppressWarnings("unchecked")
    public B asBuilder() {
        return (B) this;
    }

    public VirtualFileAndMappingSystemFile buildVirtualFileAndMappingSystemFile(){
        return new VirtualFileAndMappingSystemFile(virtualCurrent, actualCurrent, virtualCurrent, actualCurrent);
    }

    public VirtualFile buildVirtualFile(){
        return new VirtualFile(actualCurrent);
    }
}
