package l.s.common.vfs.spi;

import l.s.common.util.IoUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

public class JavaZipEntryInputStream extends InputStream {

    private JarFile zipFile;
    InputStream entryInputStream;

    public JavaZipEntryInputStream(File archiveFile, String entryName) throws IOException {
        try{
            zipFile = new JarFile(archiveFile);
            ZipEntry entry = zipFile.getEntry(entryName);
            if(entry == null){
                throw new IOException("Entry not found: " + entryName);
            }
            entryInputStream = zipFile.getInputStream(entry);
        }finally {
            IoUtil.close(zipFile);
        }
    }

    @Override
    public int read() throws IOException {
        return entryInputStream.read();
    }

    @Override
    public void close() {
        IoUtil.close(entryInputStream);
        IoUtil.close(zipFile);
    }
}
