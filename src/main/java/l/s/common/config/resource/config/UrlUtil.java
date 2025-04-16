package l.s.common.config.resource.config;

import java.net.MalformedURLException;
import java.net.URL;

class UrlUtil {
    public static URL toUrl(String path) throws MalformedURLException {
        path = path.replace('\\', '/');
        return new URL(path);
    }
}
