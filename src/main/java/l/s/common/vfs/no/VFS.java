package l.s.common.vfs.no;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VFS {
    private static Path defaultVfsHome;

    static {
        String path = System.getenv("VFS_HOME");
        if(path == null || path.isEmpty()){
            path = System.getProperty("vfs_home_dir");
        }
        if(path == null || path.isEmpty()){
            try {
                defaultVfsHome = Paths.get(Paths.get("").toFile().getCanonicalPath());
            } catch (IOException e) {
                defaultVfsHome = Paths.get("");
            }
        }else{
            defaultVfsHome = Paths.get(path);
        }
    }

    public static VirtualFileBuilder<?> builder(){
        return new VirtualFileBuilder<>().virtualRoot(defaultVfsHome).actualRoot(defaultVfsHome);
    }

    public static VirtualFile getBase(String path){
        return builder().virtualRoot(path).actualRoot(path).buildVirtualFile();
    }
    public static VirtualFile getBase(URI path){
        return builder().virtualRoot(path).actualRoot(path).buildVirtualFile();
    }
    public static VirtualFile getBase(Path path){
        return builder().virtualRoot(path).actualRoot(path).buildVirtualFile();
    }
    public static VirtualFile getBase(){
        return builder().buildVirtualFile();
    }
}

