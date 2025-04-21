package l.s.common.vfs.util;

import l.s.common.vfs.VirtualFile;
import l.s.common.vfs.VirtualFileFilter;

import java.util.Map;
import java.util.Set;

public class FileNameVirtualFileFilter implements VirtualFileFilter {
    private Map<String, Set<String>> excludes;

    public FileNameVirtualFileFilter(Map<String, Set<String>> excludes) {
        if (excludes == null || excludes.isEmpty()) {
            throw new RuntimeException("excludes is null or empty.");
        }

        this.excludes = excludes;
    }

    /**
     * Do we accept file.
     * <p/>
     * If pathName contains any of the keys,
     * * if the value is null - then do exclude
     * * if value is not null - only exclude if it value contains simple name
     *
     * @param file the virtual file
     * @return false if file is excluded by excludes map, true other wise
     */
    public boolean accepts(VirtualFile file) {
        String pathName = getPathName(file);
        for (Map.Entry<String, Set<String>> entry : excludes.entrySet()) {
            String key = entry.getKey();
            if (pathName.contains(key)) {
                String simpleName = file.getName();
                Set<String> value = entry.getValue();
                if (value == null || value.contains(simpleName)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Get the path name for the VirtualFile.
     *
     * @param file the virtual file
     * @return the path name
     */
    protected String getPathName(VirtualFile file) {
        try {
            // prefer the URI, as the pathName might
            // return an empty string for temp virtual files
            return file.mount().toURI().toString();
        } catch (Exception e) {
            return file.getPath();
        }
    }
}