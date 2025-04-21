package l.s.common.vfs.util;

public abstract class IncludePatternVirtualFileFilter extends AbstractPatternVirtualFileFilter {
    public IncludePatternVirtualFileFilter(String regexp) {
        super(regexp);
    }

    protected boolean doMatch() {
        return true;
    }
}