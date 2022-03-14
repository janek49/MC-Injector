package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.lang.reflect.Method;

public class AsmUtil {
    public static Class<?> findClass(String className) {
        try {
            if (AgentMain.IS_LAUNCHWRAPPER) {
                return getLaunchClassLoader().findClass(className);
            } else {
                return Class.forName(className);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void applyClassPath(ClassPool pool) {
        if (AgentMain.IS_LAUNCHWRAPPER)
            pool.appendClassPath(new LoaderClassPath(getLaunchClassLoader()));
    }

    public static LaunchClassLoader getLaunchClassLoader() {
        return Launch.classLoader;
    }

}
