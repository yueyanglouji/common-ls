package l.s.common.vfs.protocol;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class VirtualFileURLStreamHandler extends AbstractLocalURLStreamHandler {

    @Override
    protected URLConnection openConnection(URL url) throws IOException {
        ensureLocal(url);
        return new VirtualFileURLConnection(url);
    }
}
