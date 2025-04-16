package l.s.common.classloader.module;

import l.s.common.util.ReflectUtil;
import org.jboss.modules.LocalLoader;
import org.jboss.modules.Module;
import org.jboss.modules.ModuleClassLoader;
import org.jboss.modules.ModuleLoader;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

public class JbossAccessor {

    static final String MODULES_DIR;

    static final LocalLoader SYSTEM;

    static final Set<String> JDK;

    static final String[] NATIVE_SEARCH_PATHS;

    static {
        MODULES_DIR = (String) ReflectUtil.getStaticValueByClassName("org.jboss.modules.Utils", "MODULES_DIR");
        SYSTEM = (LocalLoader) ReflectUtil.getStaticValueByClassName("org.jboss.modules.ClassLoaderLocalLoader", "SYSTEM");
        JDK = (Set<String> ) ReflectUtil.getStaticValueByClassName("org.jboss.modules.JDKPaths", "JDK");
        NATIVE_SEARCH_PATHS = (String[]) ReflectUtil.getStaticValueByClassName("org.jboss.modules.NativeLibraryResourceLoader$Identification", "NATIVE_SEARCH_PATHS");
    }

    public static boolean unloadModule(ModuleLoader loader, final String moduleId, final Module module ) {
        Method unloadModule = ReflectUtil.getDeclaredMethod(loader.getClass(), "unloadModule", String.class, Module.class);
        assert unloadModule != null;
        unloadModule.setAccessible(true);
        try {
            return (Boolean) unloadModule.invoke(loader, moduleId, module);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    public static Module getModule(ModuleClassLoader.Configuration configuration) {
        Method getModule = ReflectUtil.getDeclaredMethod(configuration.getClass(), "getModule");
        assert getModule != null;
        getModule.setAccessible(true);
        try {
            return (Module) getModule.invoke(configuration);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

}
