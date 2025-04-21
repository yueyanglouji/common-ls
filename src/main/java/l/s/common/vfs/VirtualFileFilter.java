package l.s.common.vfs;

public interface VirtualFileFilter {

    /**
     * Match the virtual file
     *
     * @param file the virtual file
     * @return true when it matches
     */
    boolean accepts(VirtualFile file);
}
