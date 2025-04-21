package l.s.common.vfs.spi;

import l.s.common.vfs.VirtualFile;
import l.s.common.vfs.WalkFunction;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.CodeSigner;
import java.util.List;

public interface FileSystem {

    /**
     * Get a real {@code File} for the given path within this filesystem.  Some filesystem types will need to make a copy
     * in order to return this file; such copies should be cached and retained until the filesystem is closed.  Depending
     * on the file type, the real path of the returned {@code File} may or may not bear a relationship to the virtual
     * path provided; if such a relationship is required, it must be negotiated at the time the filesystem is mounted.
     *
     * @param target     the virtual file to act upon
     * @return the file instance
     * @throws IOException if an I/O error occurs
     */
    File getFile(VirtualFile target) throws IOException;

    /**
     * Open an input stream for the file at the given relative path.
     *
     * @param target     the virtual file to act upon
     * @return the input stream
     * @throws IOException if an I/O error occurs
     */
    InputStream openInputStream(VirtualFile target) throws IOException;

    /**
     * Determine whether this filesystem is read-only.  A read-only filesystem prohibits file modification or deletion.
     * It is not an error to mount a read-write filesystem within a read-only filesystem however (this operation does not
     * take place within the {@code FileSystem} implementation).
     *
     * @return {@code true} if the filesystem is read-only
     */
    boolean isReadOnly();

    /**
     * Attempt to delete a virtual file within this filesystem.
     *
     * @param target     the virtual file to act upon
     * @return {@code true} if the file was deleted, {@code false} if it failed for any reason
     */
    boolean delete(VirtualFile target);

    /**
     * Get the size of a virtual file within this filesystem.
     *
     * @param target     the virtual file to act upon
     * @return the size, in bytes, or 0L if the file does not exist or is a directory
     */
    long getSize(VirtualFile target);

    /**
     * Get the last modification time of a virtual file within this filesystem.
     *
     * @param target     the virtual file to act upon
     * @return the modification time in milliseconds, or 0L if the file does not exist or if an error occurs
     */
    long getLastModified(VirtualFile target);

    /**
     * Ascertain the existance of a virtual file within this filesystem.
     *
     * @param target     the virtual file to act upon
     * @return {@code true} if the file exists, {@code false} otherwise
     * @throws IOException if an I/O error occurs
     */
    boolean exists(VirtualFile target);

    /**
     * Ascertain whether a virtual file within this filesystem is a plain file.
     *
     * @param target     the virtual file to act upon
     * @return {@code true} if the file exists and is a plain file, {@code false} otherwise
     */
    boolean isFile(VirtualFile target);

    /**
     * Ascertain whether a virtual file within this filesystem is a directory.
     *
     * @param target     the virtual file to act upon
     * @return {@code true} if the file exists and is a directory, {@code false} otherwise
     */
    boolean isDirectory(VirtualFile target);

    /**
     * Read a directory.  Returns all the simple path names (excluding "." and "..").  The returned list will be empty if
     * the node is not a directory.
     *
     * @param target     the virtual file to act upon
     * @return the collection of children names
     */
    List<String> getDirectoryEntries(VirtualFile target);

    void walk(VirtualFile virtualFile, WalkFunction function);

    List<VirtualFile> walkToList(VirtualFile target) throws IOException;

    /**
     * Get the {@link CodeSigner}s for a the virtual file.
     *
     * @param target     the virtual file to act upon
     * @return {@link CodeSigner} for the virtual file or null if not signed.
     */
    CodeSigner[] getCodeSigners(VirtualFile target);

    /**
     * Get the {@link File} source provided at mount time.
     *
     * @return the source used for mounting
     */
    File getMountSource();

    /**
     * Get the root URI for this file system, or {@code null} if there is no valid root URI.
     *
     * @return the root URI
     * @throws URISyntaxException if the URI isn't valid
     */
    URI getRootURI() throws URISyntaxException;
}
