package l.s.common.vfs.protocol;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class FileURLStreamHandler extends AbstractLocalURLStreamHandler {
    @Override
    protected URLConnection openConnection(final URL url) throws IOException {
        ensureLocal(url);
        return new FileURLConnection(url);
    }
}
