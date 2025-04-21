package l.s.common.vfs.util;

import java.io.IOException;
import java.io.InputStream;

public class PaddedManifestStream extends InputStream {

    private final InputStream realStream;
    private int previousChar = -1;

    public PaddedManifestStream(InputStream realStream) {
        this.realStream = realStream;
    }

    @Override
    public int read() throws IOException {
        int value = this.realStream.read();
        while(value == '\0') {
            value = this.realStream.read();
        }
        if (value == -1 && previousChar != '\n' && previousChar != -1) {
            previousChar = '\n';
            return '\n';
        }
        previousChar = value;
        return value;
    }

    @Override
    public void close() throws IOException {
        super.close();
        realStream.close();
    }
}
