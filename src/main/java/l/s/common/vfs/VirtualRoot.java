package l.s.common.vfs;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

public class VirtualRoot {

    VirtualFile root;

    Path mountPath;

    VirtualRoot(VirtualFile root, Path mountPath){
        this.root = root;
        this.mountPath = mountPath;
    }

    public VirtualFile getVirtualFile(){
        return root;
    }

    public VirtualFile getVirtualFile(URL url) throws IOException, URISyntaxException {
        return getVirtualFile(url.toURI());
    }

    public VirtualFile getVirtualFile(URI uri) throws IOException {
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
        String relative = mountPath.relativize(Paths.get(fileURI).normalize()).normalize().toString();
        return root.get(relative);
    }

}
