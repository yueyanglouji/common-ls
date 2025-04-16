package l.s.common.vfs.protocol;

import java.io.IOException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashSet;
import java.util.Set;

public abstract class AbstractLocalURLStreamHandler extends URLStreamHandler {

    private static final Set<String> locals;

    static {
        Set<String> set = new HashSet<String>();
        set.add(null);
        set.add("");
        set.add("~");
        set.add("localhost");
        locals = set;
    }

    private static String toLower(String str) {
        return str == null ? null : str.toLowerCase();
    }

    @Override
    protected URLConnection openConnection(URL u, Proxy p) throws IOException {
        return openConnection(u);
    }

    @Override
    protected boolean hostsEqual(URL url1, URL url2) {
        return locals.contains(toLower(url1.getHost())) && locals.contains(toLower(url2.getHost())) || super.hostsEqual(url1, url2);
    }

    protected void ensureLocal(URL url) throws IOException {
        if (!locals.contains(toLower(url.getHost()))) {
            throw new IOException("Not support protocol : " + url.getProtocol());
        }
    }

}
