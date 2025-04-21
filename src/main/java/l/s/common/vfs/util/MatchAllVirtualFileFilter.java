package l.s.common.vfs.util;

import l.s.common.vfs.VirtualFile;
import l.s.common.vfs.VirtualFileFilter;

public class MatchAllVirtualFileFilter implements VirtualFileFilter {

    /**
     * The instance
     */
    public static final MatchAllVirtualFileFilter INSTANCE = new MatchAllVirtualFileFilter();

    /**
     * Singleton
     */
    private MatchAllVirtualFileFilter() {
    }

    public boolean accepts(VirtualFile file) {
        return true;
    }
}
