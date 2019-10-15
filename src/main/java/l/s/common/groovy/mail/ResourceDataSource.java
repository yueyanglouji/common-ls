package l.s.common.groovy.mail;

import org.springframework.core.io.Resource;

import javax.activation.DataSource;
import javax.activation.FileTypeMap;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ResourceDataSource implements DataSource {
    private Resource _resource;
    private FileTypeMap typeMap;

    public ResourceDataSource(Resource file) {
        this._resource = null;
        this.typeMap = null;
        this._resource = file;
    }

    public InputStream getInputStream() throws IOException {
        return _resource.getInputStream();
    }

    public OutputStream getOutputStream() throws IOException {
        return new FileOutputStream(this._resource.getFile());
    }

    public String getContentType(){
        return this.typeMap == null ? FileTypeMap.getDefaultFileTypeMap().getContentType(this._resource.getFilename()) : this.typeMap.getContentType(this._resource.getFilename());
    }

    public String getName() {
        return this._resource.getFilename();
    }

}

