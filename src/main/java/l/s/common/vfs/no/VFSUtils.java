package l.s.common.vfs.no;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

public class VFSUtils {

    private VFSUtils() {
    }

    /**
     * Deal with urls that may include spaces.
     *
     * @param url the url
     * @return uri the uri
     * @throws URISyntaxException for any error
     */
    public static URI toURI(URL url) throws URISyntaxException {
        if (url == null) {
            throw new NullPointerException("url");
        }
        try {
            return url.toURI();
        } catch (URISyntaxException e) {
            String urispec = url.toExternalForm();
            // Escape percent sign and spaces
            urispec = urispec.replaceAll("%", "%25");
            urispec = urispec.replaceAll(" ", "%20");
            return new URI(urispec);
        }
    }
}
