package l.s.common.vfs.util;

import l.s.common.vfs.VirtualFile;

public class IncludeFileNameVirtualFileFilter extends IncludePatternVirtualFileFilter {
    public IncludeFileNameVirtualFileFilter(String regexp) {
        super(regexp);
    }

    protected String getMatchString(VirtualFile file) {
        return file.getName();
    }
}