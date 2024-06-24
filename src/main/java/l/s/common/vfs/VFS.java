package l.s.common.vfs;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VFS {
    private static Path home;

    static {
        String path = System.getenv("VFS_HOME");
        if(path == null || path.equals("")){
            path = System.getProperty("vfs_home_dir");
        }
        if(path == null || path.equals("")){
            try {
                home = Paths.get(Paths.get("").toFile().getCanonicalPath());
            } catch (IOException e) {
                home = Paths.get("");
            }
        }else{
            home = Paths.get(path);
        }
    }

    public static void configuration(Path home){
        VFS.home = home;
    }

    public static VirtualFile getBase(String path){
        return new VirtualFile(Paths.get(path));
    }
    public static VirtualFile getBase(URI path){
        return new VirtualFile(Paths.get(path));
    }
    public static VirtualFile getBase(Path path){
        return new VirtualFile(path);
    }
    public static VirtualFile getBase(){
        return new VirtualFile(home);
    }
}

