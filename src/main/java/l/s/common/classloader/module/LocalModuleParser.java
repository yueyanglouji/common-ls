package l.s.common.classloader.module;

import l.s.common.vfs.VirtualFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LocalModuleParser {

    public static final Set<String> COMMON = new HashSet<>();

    public static void processJars(VirtualFile virtualFile) {
        try {
            List<VirtualFile> list = virtualFile.mount().listFiles();
            list.forEach(it -> {
                try {
                    if(it.getName().endsWith(".jar")) {
                        processJar(it);
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    static void processDirectory(final Set<String> pathSet, final VirtualFile file) throws IOException {
        processDirectory1(pathSet, file, file);
    }

    private static void processDirectory1(Set<String> pathSet, VirtualFile file, VirtualFile pathBase) throws IOException {
        for (VirtualFile entry : file.mount().listFiles()) {
            if (entry.mount().isDirectory()) {
                processDirectory1(pathSet, entry, pathBase);
            } else {
                final VirtualFile parent = entry.getParent();
                if (parent != null) {
                    pathSet.add(parent.getPathRelativeTo(pathBase));
                }
            }
        }
    }

    static void processJar(final VirtualFile file) throws IOException {
        List<String> directoryEntries = file.mount().getFileSystem().getDirectoryEntries(file);
        for (String directoryEntry : directoryEntries) {
            final int lastSlash = directoryEntry.lastIndexOf('/');
            if (lastSlash != -1) {
                LocalModuleParser.COMMON.add(directoryEntry.substring(0, lastSlash));
            }
        }
    }
}
