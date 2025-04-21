package l.s.common.vfs;

import l.s.common.vfs.util.PathTokenizer;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VirtualFile {

    static TempFileProvider tempFileProvider;

    VirtualRoot root;

    VirtualFile parent;

    String name;

    String path;

    Path mountPath;

    static {
        try {
            tempFileProvider = TempFileProvider.create("temp", true);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private VirtualFile(VirtualRoot root, VirtualFile parent, String name, String path, Path mountPath) {
        this.root = root;
        this.parent = parent;
        this.path = path;
        this.name = name;
        this.mountPath = mountPath;
    }

    static VirtualRoot getRoot(Path root) throws IOException {
        VirtualFile virtualFile = new VirtualFile(null, null, "/", "/", root);
        VirtualRoot virtualRoot = new VirtualRoot(virtualFile, root);
        virtualFile.root = virtualRoot;
        return virtualRoot;
    }

    public void linkTo(Path path) throws IOException {
        try{
            VFS.addSoftLink(this, path);
            this.mount().reMount();
        } catch (Exception e){
            unlink();
            throw e;
        }

    }

    public void unlink() throws IOException {
        VFS.removeSoftLink(this);
        this.mount().reMount();
    }

    public Mount mount() throws IOException {
        return makeMount(this);
    }

    @SuppressWarnings("unchecked")
    public <T> T mount(Class<T> tClass) throws IOException {
        return (T)mount();
    }

    private static Mount makeMount(Path path, List<String> entryList, VirtualFile vf, VirtualFile mountPoint) throws IOException {
        VirtualFileType type;
        Mount ret;
        Path softLink = VFS.getSoftLink(vf);
        // softLink not null and softlink is not same path and not in zip.
        if(softLink != null && !softLink.equals(path) && entryList.size() <= 1){
            ret = makeMount(softLink, entryList, vf, mountPoint);
        }
        else if(!Files.exists(path)){
            type = VirtualFileType.NOT_EXISTS;
            ret = new RealPathMount(tempFileProvider, vf, type, path);
        }
        else if(Files.isDirectory(path)) {
            type = VirtualFileType.DIRECTORY;
            ret = new RealPathMount(tempFileProvider, vf, type, path);
        }
        else if(Files.isRegularFile(path)) {
            File file = path.toFile();
            if(isJavaZipFile(file)){
                type = VirtualFileType.JAVA_ZIP;
                ret = new JavaZipMount(tempFileProvider, vf, type, mountPoint, path);
            }else {
                type = VirtualFileType.FILE;
                ret = new RealPathMount(tempFileProvider, vf, type, path);
            }
        }
        else if(Files.isSymbolicLink(path)) {
            Path linked = Files.readSymbolicLink(path);
            Mount realMount = makeMount(linked, entryList, vf, mountPoint);
            if(realMount instanceof SymbolicLinkJavaZipMount){
                SymbolicLinkJavaZipMount realMount0 = (SymbolicLinkJavaZipMount) realMount;
                realMount0.symbolicLinkPath = vf.mountPath;
            }
            else if(realMount instanceof SymbolicLinkRealPathMount){
                SymbolicLinkRealPathMount realMount0 = (SymbolicLinkRealPathMount) realMount;
                realMount0.symbolicLinkPath = vf.mountPath;
            }
            else if(realMount instanceof JavaZipMount){
                JavaZipMount realMount0 = (JavaZipMount) realMount;
                SymbolicLinkJavaZipMount realRealMount = new SymbolicLinkJavaZipMount(tempFileProvider, vf, realMount0.type, realMount0.mountPoint, realMount0.getZipFile(), path);
                realRealMount.symbolicLinkPath = vf.mountPath;
                realMount = realRealMount;
            }
            else if(realMount instanceof RealPathMount){
                RealPathMount realMount0 = (RealPathMount) realMount;
                SymbolicLinkRealPathMount realRealMount = new SymbolicLinkRealPathMount(tempFileProvider, vf, realMount0.type, realMount0.realPath, path);
                realRealMount.symbolicLinkPath = vf.mountPath;
                realMount = realRealMount;
            }
            else {
                throw new RuntimeException("Unknown Mount type : " + realMount.getClass());
            }
            ret = realMount;
        }
        else {
            type = VirtualFileType.FILE;
            ret = new RealPathMount(tempFileProvider, vf, type, path);
        }
        return ret;
    }

    private static Mount makeMount(VirtualFile virtualFile) throws IOException {
        List<VirtualFile> list = new ArrayList<>();
        VirtualFile current = virtualFile;
        for (;;){
            list.add(current);
            if(current.parent == null){
                break;
            }else{
                current = current.parent;
            }
        }
        Collections.reverse(list);

        List<String> entryList = new ArrayList<>();
        entryList.add("/");
        Mount mount = null;
        for(VirtualFile vf : list){
            if(mount == null){
                mount = makeMount(vf.mountPath, entryList, vf, vf);
                continue;
            }
            if(mount.type == VirtualFileType.FILE){
                throw new RuntimeException("Not allow - Parent is a File: " + mount.virtualFile.path);
            }
            else if(mount.type == VirtualFileType.DIRECTORY){
                RealPathMount mount0 = (RealPathMount) mount;
                mount = makeMount(vf.mountPath, entryList, vf, vf);
            }
            else if(mount.type == VirtualFileType.JAVA_ZIP){
                JavaZipMount mount0 = (JavaZipMount) mount;
                entryList.add(vf.name);
                mount = makeMount(mount0.getZipFile(), entryList, vf, mount0.mountPoint);
            }
            else if(mount.type == VirtualFileType.NOT_EXISTS){
                RealPathMount mount0 = (RealPathMount) mount;
                mount = makeMount(vf.mountPath, entryList, vf, vf);
            }
            else{
                throw new RuntimeException("type not support : " + mount.type);
            }
        }
        return mount;
    }

    static boolean isJavaZipFile(File f) {
        // TODO: only for .jar and .war files, but we need to check if has any other file types should be checked?
        return f.getName().endsWith(".jar") || f.getName().endsWith(".war") && isArchive(f);
    }

    private static boolean isArchive(File f) {
        if(!f.isFile()){
            return false;
        }
        int fileSignature = 0;
        try (RandomAccessFile raf = new RandomAccessFile(f, "r")) {
            fileSignature = raf.readInt();
        } catch (IOException ignored) {

        }
        return fileSignature == 0x504B0304 || fileSignature == 0x504B0506 || fileSignature == 0x504B0708;
    }

    public VirtualFile get(String path) {
        if (path == null) {
            throw new NullPointerException("path");
        }
        path = path.replace('\\', '/');
        if(path.equals(this.path)){
            return this;
        }
        if(path.startsWith(this.path)){
            path = path.substring(this.path.length() + 1);
        }
        final List<String> pathParts = PathTokenizer.getTokens(path);
        VirtualFile current = this;
        for (String part : pathParts) {
            if (PathTokenizer.isReverseToken(part)) {
                final VirtualFile parent = current.parent;
                current = parent == null ? current : parent;
            } else if (!PathTokenizer.isCurrentToken(part)) {
                current = new VirtualFile(root, current, part, (current.path.endsWith("/")? "" : current.path) + "/" + part, Paths.get(current.mountPath.toString(), part));
            }
        }
        return current;
    }

    public String getPath() {
        return path;
    }

    public VirtualRoot getRoot() {
        return root;
    }

    public VirtualFile getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return path;
    }

    @Override
    public int hashCode() {
        return (root + "" + parent + name + path + mountPath).hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof VirtualFile)){
            return false;
        }
        VirtualFile vf = (VirtualFile) obj;
        return hashCode() == vf.hashCode()
                && root == vf.root
                && name.equals(vf.name)
                && path.equals(vf.path)
                && mountPath.equals(vf.mountPath);
    }

    public String getPathRelativeTo(VirtualFile root) {
        String rootPath = root.path;
        if(rootPath.equals(path)){
            return "";
        }
        if(!rootPath.equals("/")){
            rootPath += "/";
        }
        if(path.startsWith(rootPath)){
            return path.substring(rootPath.length());
        }else{
            throw new RuntimeException("Not a child of " + root);
        }
    }

    public boolean isRoot() {
        return this.root.getVirtualFile() == this;
    }

    public Path getMountPath() {
        return mountPath;
    }
    public URL toURL() throws MalformedURLException {
        return VFSUtils.getVirtualURL(this);
    }

    public URI toURI() throws URISyntaxException {
        return VFSUtils.getVirtualURI(this);
    }
}
