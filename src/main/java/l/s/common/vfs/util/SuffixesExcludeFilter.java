package l.s.common.vfs.util;

import l.s.common.vfs.VirtualFile;
import l.s.common.vfs.VirtualFileFilter;

import java.util.Collection;

public class SuffixesExcludeFilter implements VirtualFileFilter {

    /**
     * The suffixes
     */
    private Collection<String> suffixes;

    /**
     * Create a new SuffixMatchFilter,
     *
     * @param suffixes the suffixes
     * @throws IllegalArgumentException for null suffixes
     */
    public SuffixesExcludeFilter(Collection<String> suffixes) {
        if (suffixes == null) {
            throw new NullPointerException("suffixes");
        }
        for (String suffix : suffixes) {
            if (suffix == null) {
                throw new IllegalArgumentException("Null suffix in " + suffixes);
            }
        }
        this.suffixes = suffixes;
    }

    public boolean accepts(VirtualFile file) {
        String name = file.getName();
        for (String suffix : suffixes) {
            if (name.endsWith(suffix)) { return false; }
        }
        return true;
    }
}
