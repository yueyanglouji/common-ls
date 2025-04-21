package l.s.common.vfs;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

public final class TempFileProvider implements Closeable {

    private static final String LS_TMP_DIR_PROPERTY = "ls.temp.dir";
    private static final String JVM_TMP_DIR_PROPERTY = "java.io.tmpdir";
    private static final File TMP_ROOT;
    private static final int RETRIES = 10;
    private final AtomicBoolean open = new AtomicBoolean(true);

    private volatile Map<String, TempDir> map;

    static {
        String configTmpDir = System.getProperty(LS_TMP_DIR_PROPERTY);
        if (configTmpDir == null) { configTmpDir = System.getProperty(JVM_TMP_DIR_PROPERTY); }
        try {
            TMP_ROOT = new File(configTmpDir, VFSUtils.VFS_PROTOCOL);
            TMP_ROOT.mkdirs();
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Create a temporary file provider for a given type.
     * <p/>
     *
     * @param providerType the provider type string (used as a prefix in the temp file dir name)
     * @return the new provider
     * @throws IOException if an I/O error occurs
     */
    public static TempFileProvider create(String providerType) throws IOException {
        return create(providerType, true);
    }

    /**
     * Create a temporary file provider for a given type.
     *
     * @param providerType The provider type string (used as a prefix in the temp file dir name)
     * @param cleanExisting If this is true, then this method will *try* to delete the existing temp content (if any) for the <code>providerType</code>. The attempt to delete the existing content (if any)
     *                      will be done in the background and this method will not wait for the deletion to complete. The method will immediately return back with a usable {@link TempFileProvider}. Note that the
     *                      <code>cleanExisting</code> will just act as a hint for this method to trigger the deletion of existing content. The method may not always be able to delete the existing contents.
     * @return The new provider
     * @throws IOException if an I/O error occurs
     */
    public static TempFileProvider create(final String providerType, final boolean cleanExisting) throws IOException {
        if (cleanExisting) {
            try {
                // The "clean existing" logic is as follows:
                // 1) Rename the root directory "foo" corresponding to the provider type to "bar"
                // 2) Submit a task to delete "bar" and its contents, in a background thread, to the the passed executor.
                // 3) Create a "foo" root directory for the provider type and return that TempFileProvider (while at the same time the background task is in progress)
                // This ensures that the "foo" root directory for the providerType is empty and the older content is being cleaned up in the background (without affecting the current processing),
                // thus simulating a "cleanup existing content"
                final File possiblyExistingProviderRoot = new File(TMP_ROOT, providerType);
                if (possiblyExistingProviderRoot.exists()) {
                    // rename it so that it can be deleted as a separate (background) task
                    final File toBeDeletedProviderRoot = new File(TMP_ROOT, createTempName(providerType + "-to-be-deleted-", ""));
                    if(toBeDeletedProviderRoot.exists()){
                        // delete in the background
                        new DeleteTask(toBeDeletedProviderRoot).run();
                    }
                    final boolean renamed = possiblyExistingProviderRoot.renameTo(toBeDeletedProviderRoot);
                    if (!renamed) {
                        throw new IOException("Failed to rename " + possiblyExistingProviderRoot.getAbsolutePath() + " to " + toBeDeletedProviderRoot.getAbsolutePath());
                    } else {
                        // delete in the background
                        new Thread(new DeleteTask(toBeDeletedProviderRoot)).start();
                    }
                }
            } catch (Throwable t) {
                // just log a message if existing contents couldn't be deleted
                // log the cause of the failure
            }
        }
        // now create and return the TempFileProvider for the providerType
        final File providerRoot = new File(TMP_ROOT, providerType);
        return new TempFileProvider(createTempDir(providerType, "", providerRoot));
    }

    private final File providerRoot;

    File getProviderRoot() {
        return providerRoot;
    }

    private TempFileProvider(File providerRoot) {
        this.providerRoot = providerRoot;
    }

    public TempDir createOrGetTempDir(Path path) throws IOException {
        if(map == null){
            synchronized (TempFileProvider.class) {
                if(map == null){
                    map = new ConcurrentHashMap<>();
                }
            }
        }
        TempDir tempDir = map.get(path.toString());
        if(tempDir == null){
            synchronized (TempFileProvider.class) {
                tempDir = map.get(path.toString());
                if(tempDir == null){
                    tempDir = createTempDir(path.getFileName().toString());
                    map.put(path.toString(), tempDir);
                }
            }
        }
        return tempDir;
    }

    /**
     * Create a temp directory, into which temporary files may be placed.
     *
     * @param originalName the original file name
     * @return the temp directory
     * @throws IOException for any error
     */
    private TempDir createTempDir(String originalName) throws IOException {
        if (!open.get()) {
            throw new IOException("tempFileProviderClosed");
        }
        final String name = createTempName(originalName + "-", "");
        final File f = new File(providerRoot, name);
        for (int i = 0; i < RETRIES; i++) {
            if (f.mkdirs()) {
                return new TempDir(this, f);
            }
        }
        throw new IOException("couldNotCreateDirectory " + originalName);
    }

    private static final Random rng = new Random();

    private static File createTempDir(String prefix, String suffix, File root) throws IOException {
        for (int i = 0; i < RETRIES; i++) {
            final File f = new File(root, createTempName(prefix, suffix));
            if (f.mkdirs()) {
                if (f.isDirectory()&&f.getParent()!=null){
                    f.delete();
                }
                return f;
            }
        }
        throw new IOException("couldNotCreateDirectoryForRoot " + root);
    }

    private static String createTempName(String prefix, String suffix) {
        return prefix + Long.toHexString(rng.nextLong()) + suffix;
    }

    /**
     * Close this provider and delete any temp files associated with it.
     */
    public void close() throws IOException {
        if (open.getAndSet(false)) {
            delete(this.providerRoot);
        }
    }

    protected void finalize() {
        VFSUtils.safeClose(this);
    }

    /**
     * Deletes any temp files associated with this provider
     *
     * @throws IOException IOException
     */
    void delete(final File root) throws IOException {
        new DeleteTask(root).run();
    }

    static final class DeleteTask implements Runnable {

        private final File root;

        DeleteTask(final File root) {
            this.root = root;
        }

        public void run() {
            if(!VFSUtils.recursiveDelete(root)){
                try {
                    TimeUnit.SECONDS.sleep(30);
                } catch (InterruptedException e) {
                    return;
                }
                run();
            }
        }
    }
}
