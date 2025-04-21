package l.s.common.vfs.protocol;

import l.s.common.vfs.VFSUtils;

import java.net.URLStreamHandler;
import java.net.URLStreamHandlerFactory;
import java.util.HashMap;
import java.util.Map;

@Deprecated
public class VfsUrlStreamHandlerFactory implements URLStreamHandlerFactory {

    private static Map<String, URLStreamHandler> handlerMap = new HashMap<String, URLStreamHandler>(1);

    static {
        handlerMap.put(VFSUtils.VFS_PROTOCOL, new VirtualFileURLStreamHandler());
    }

    @Override
    public URLStreamHandler createURLStreamHandler(final String protocol) {
        return handlerMap.get(protocol);
    }

}
