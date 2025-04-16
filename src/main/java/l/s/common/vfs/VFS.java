package l.s.common.vfs;

import java.io.IOException;
import java.nio.file.Path;

public class VFS {

    private static VirtualRoot virtualRoot;

    static {
        init();
    }

    private static void init() {
        String pkgs = System.getProperty("java.protocol.handler.pkgs");
        if (pkgs == null || pkgs.trim().isEmpty()) {
            pkgs = "l.s.common.vfs.protocol";
            System.setProperty("java.protocol.handler.pkgs", pkgs);
        } else if (!pkgs.contains("l.s.common.vfs.protocol")) {
            pkgs += "|l.s.common.vfs.protocol";
            System.setProperty("java.protocol.handler.pkgs", pkgs);
        }
    }

    public static VirtualRoot getRoot() {
        if(virtualRoot == null){
            throw new RuntimeException("Not mounted Virtual root is exists. Use mountRoot() method.");
        }
        return virtualRoot;
    }

    public static VirtualRoot mountRoot(Path path) throws IOException {
        if(virtualRoot == null || virtualRoot.isClosed()){
            synchronized (VFS.class){
                if(virtualRoot == null || virtualRoot.isClosed()){
                    virtualRoot = VirtualFile.getRoot(path);
                }
            }
        }else{
            if(!virtualRoot.mountPath.equals(path)){
                throw new RuntimeException("Virtual root is already exists.");
            }
        }
        return virtualRoot;
    }

    public static void close() {
        virtualRoot.closed = true;
        virtualRoot = null;
    }
}

