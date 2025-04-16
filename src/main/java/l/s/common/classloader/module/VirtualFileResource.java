package l.s.common.classloader.module;

import l.s.common.vfs.VirtualFile;
import org.jboss.modules.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.security.AccessControlContext;

import static l.s.common.classloader.module.VirtualFileResourceLoader.doPrivilegedIfNeeded;

public class VirtualFileResource implements Resource {

    VirtualFile virtualFile;
    private final String name;
    private final AccessControlContext context;

    VirtualFileResource(VirtualFile virtualFile, final String name, AccessControlContext context) {
        this.virtualFile = virtualFile;
        this.name = name;
        this.context = context;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public URL getURL() {
        return doPrivilegedIfNeeded(context, () -> {
            final URL url = virtualFile.toURL();
            url.openConnection().connect();
            return url;
        });
    }

    @Override
    public InputStream openStream() throws IOException {
        return doPrivilegedIfNeeded(context, IOException.class, () -> virtualFile.mount().openInputStream());
    }

    @Override
    public long getSize() {
        try {
            return doPrivilegedIfNeeded(context, IOException.class, () -> virtualFile.mount().getSize());
        } catch (IOException e) {
            return 0;
        }
    }
}
