package l.s.common.classloader.module;

import l.s.common.vfs.VirtualFile;
import org.jboss.modules.PathUtils;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LocalModuleParser {

    public static final Set<String> COMMON = new HashSet<>();

    public static void process(final VirtualFile root) throws IOException {
        root.mount().walk(vf -> {
            final String result = vf.getPathRelativeTo(root);
            String canonical = PathUtils.toGenericSeparators(result);

            // JBoss modules expect folders not to end with a slash, so we have to strip it.
            if (canonical.endsWith("/")) {
                canonical = canonical.substring(0, canonical.length() - 1);
            }
            COMMON.add(canonical);
        });
    }
}
