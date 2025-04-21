package l.s.common.vfs.util;

import l.s.common.vfs.VirtualFile;
import l.s.common.vfs.VirtualFileFilter;

import java.util.regex.Pattern;

public abstract class AbstractPatternVirtualFileFilter implements VirtualFileFilter {
    private Pattern pattern;

    public AbstractPatternVirtualFileFilter(String regexp) {
        if (regexp == null) {
            throw new NullPointerException("regexp");
        }

        pattern = Pattern.compile(regexp);
    }

    /**
     * Extract match string from file.
     *
     * @param file the file
     * @return extracted match string
     */
    protected abstract String getMatchString(VirtualFile file);

    /**
     * Should we match the pattern.
     *
     * @return the match flag
     */
    protected abstract boolean doMatch();

    public boolean accepts(VirtualFile file) {
        String string = getMatchString(file);
        return pattern.matcher(string).matches() == doMatch();
    }
}
