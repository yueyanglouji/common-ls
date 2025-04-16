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

    boolean closed;

    VirtualRoot(VirtualFile root, Path mountPath){
        this.root = root;
        this.mountPath = mountPath;
    }

    public VirtualFile get(){
        return root;
    }

    public VirtualFile get(URL url) throws IOException, URISyntaxException {
        return get(url.toURI());
    }

    public VirtualFile get(URI uri) throws IOException {
        String path = uri.getPath();
        if(path == null) {
            path = uri.getSchemeSpecificPart();
        }
        URI fileURI = null;
        try {
            fileURI = new URI("file", path, null);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        String relative = mountPath.relativize(Paths.get(fileURI).normalize()).normalize().toString();
        return root.get(relative);
    }

    public boolean isClosed(){
        return closed;
    }


}
