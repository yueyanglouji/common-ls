package l.s.common.vfs.protocol;

import l.s.common.vfs.VFSUtils;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public abstract class AbstractURLConnection extends URLConnection {

    private String contentType;

    protected AbstractURLConnection(final URL url) {
        super(url);
    }

    public String getHeaderField(String name) {
        String headerField = null;
        if (name.equals("content-type")) {
            headerField = getContentType();
        } else if (name.equals("content-length")) {
            headerField = String.valueOf(getContentLength());
        } else if (name.equals("last-modified")) {
            long lastModified = getLastModified();
            if (lastModified != 0) {
                // return the last modified date formatted according to RFC 1123
                Date modifiedDate = new Date(lastModified);
                SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.US);
                sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
                headerField = sdf.format(modifiedDate);
            }
        } else {
            headerField = super.getHeaderField(name);
        }
        return headerField;
    }

    public String getContentType() {
        if (contentType != null) { return contentType; }
        contentType = getFileNameMap().getContentTypeFor(getName());
        if (contentType == null) {
            try {
                InputStream is = getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                contentType = URLConnection.guessContentTypeFromStream(bis);
                bis.close();
            } catch (IOException e) { /* ignore */ }
        }
        return contentType;
    }

    protected static URI toURI(URL url) throws IOException {
        try {
            return VFSUtils.toURI(url);
        } catch (URISyntaxException e) {
            IOException ioe = new IOException();
            ioe.initCause(e);
            throw ioe;
        }
    }

    protected abstract String getName();
}
