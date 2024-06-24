package org.jboss.modules;

import l.s.common.vfs.VFS;
import l.s.common.vfs.VirtualFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class LocalModuleParser {

    static final Set<String> COMMON = new HashSet<>();

    public static void processJars(VirtualFile virtualFile) {
        try {
            Files.list(virtualFile.getPath()).forEach(it -> {
                try {
                    processJar(COMMON, it.toFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    static void processDirectory(final Set<String> pathSet, final File file) {
        for (File entry : file.listFiles()) {
            if (entry.isDirectory()) {
                processDirectory1(pathSet, entry, file.getPath());
            } else {
                final String parent = entry.getParent();
                if (parent != null) pathSet.add(parent);
            }
        }
    }
    private static void processDirectory1(final Set<String> pathSet, final File file, final String pathBase) {
        for (File entry : file.listFiles()) {
            if (entry.isDirectory()) {
                processDirectory1(pathSet, entry, pathBase);
            } else {
                String packagePath = entry.getParent();
                if (packagePath != null) {
                    packagePath = packagePath.substring(pathBase.length()).replace('\\', '/');
                    if(packagePath.startsWith("/")) {
                        packagePath = packagePath.substring(1);
                    }
                    pathSet.add(packagePath);
                }
            }
        }
    }
    static void processJar(final Set<String> pathSet, final File file) throws IOException {
        try (final ZipFile zipFile = new ZipFile(file)) {
            final Enumeration<? extends ZipEntry> entries = zipFile.entries();
            while (entries.hasMoreElements()) {
                final ZipEntry entry = entries.nextElement();
                final String name = entry.getName();
                final int lastSlash = name.lastIndexOf('/');
                if (lastSlash != -1) {
                    pathSet.add(name.substring(0, lastSlash));
                }
            }
        }
    }
}
