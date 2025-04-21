package l.s.common.vfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class VFS {

    private static final List<VirtualRoot> virtualRootList;

    private static final Map<String, Path> SOFT_LINKS;
    private static final Map<String, List<String>> SOFT_LINKS_CHILDREN;

    static {
        init();
        virtualRootList = new ArrayList<>();
        SOFT_LINKS = new ConcurrentHashMap<>();
        SOFT_LINKS_CHILDREN = new ConcurrentHashMap<>();
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

    public static VirtualFile getVirtualFile(URL url) {
        try {
            return getVirtualFile(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    public static VirtualFile getVirtualFile(URI uri) {
        try {
            return getRoot(uri).getVirtualFile(uri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static VirtualRoot getRoot(URL url) {
        try {
            return getRoot(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    static VirtualRoot getRoot(URI uri) {
        String path = uri.getPath();
        if(path == null) {
            path = uri.getSchemeSpecificPart();
        }
        URI fileURI;
        try {
            fileURI = new URI("file", path, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Path normalize = Paths.get(fileURI).normalize();
        for(VirtualRoot vr: virtualRootList){
            if(normalize.startsWith(vr.mountPath)){
                return vr;
            }
        }
        throw new RuntimeException("Not find a mounted Virtual Root. Use VFS.mountRoot() method to Mount a new Virtual Root.");
    }

    public static VirtualFile mountRoot(Path path) throws IOException {
        path = path.normalize();
        if(!Files.exists(path)){
            throw new RuntimeException("Path is not exists. path: " + path);
        }
        VirtualRoot virtualRoot = findVirtualRoot(path);
        if(virtualRoot == null){
            synchronized (VFS.class){
                virtualRoot = findVirtualRoot(path);
                if(virtualRoot == null){
                    virtualRoot = VirtualFile.getRoot(path);
                    virtualRootList.add(virtualRoot);
                }
            }
        }
        return virtualRoot.getVirtualFile();
    }

    private static VirtualRoot findVirtualRoot(Path path){
        for(VirtualRoot vr: virtualRootList){
            if(vr.mountPath.equals(path)){
                return vr;
            }
        }
        return null;
    }

    static void addSoftLink(VirtualFile virtualFile, Path path){
        SOFT_LINKS.put(virtualFile.getPath(), path.normalize());
        if(virtualFile.getParent() != null){
            List<String> children = SOFT_LINKS_CHILDREN.computeIfAbsent(virtualFile.getParent().getPath(), k -> new ArrayList<>());
            children.add(virtualFile.getName());
        }
    }

    static Path getSoftLink(VirtualFile virtualFile){
        return SOFT_LINKS.get(virtualFile.getPath());
    }

    static List<String> getSoftLinkChildren(VirtualFile virtualFile){
        List<String> children = SOFT_LINKS_CHILDREN.get(virtualFile.getPath());
        if(children == null){
            children = Collections.emptyList();
        }
        return children;
    }

    static void removeSoftLink(VirtualFile virtualFile){
        SOFT_LINKS.remove(virtualFile.getPath());
        List<String> children = SOFT_LINKS_CHILDREN.get(virtualFile.getParent().getPath());
        if(children != null){
            children.remove(virtualFile.getName());
        }
    }
}

