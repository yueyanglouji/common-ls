package l.s.common.groovy.mail;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.AbstractResource;
import org.springframework.util.Assert;

public class InMemoryResource extends AbstractResource {
    private final byte[] source;
    private final String description;
    private String _charset = "UTF-8";

    public InMemoryResource(String source) {
        this(source, "UTF-8");
    }

    public InMemoryResource(String source, String charset) {
        this(getBytes(source, charset));
        this._charset = charset;
    }

    private static byte[] getBytes(String source, String charset){
        try{
            return source.getBytes(charset);
        }catch (Exception e){
            return source.getBytes();
        }
    }

    public InMemoryResource(byte[] source) {
        this(source, (String)null);
    }

    public InMemoryResource(byte[] source, String description) {
        Assert.notNull(source, "source cannot be null");
        this.source = source;
        this.description = description;
    }

    public String getDescription() {
        return this.description;
    }

    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(this.source);
    }

    public String getCharset(){
        return _charset;
    }

}
