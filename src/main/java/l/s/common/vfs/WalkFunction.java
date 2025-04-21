package l.s.common.vfs;

@FunctionalInterface
public interface WalkFunction {
    void apply(VirtualFile file);
}
