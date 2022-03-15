package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.LoaderClassPath;
import net.minecraft.client.Minecraft;

import java.lang.reflect.Method;

public class AsmUtil {
    public static Class<?> findClass(String className) {
        try {
            className = className.replace("/", ".");
            if (AgentMain.IS_LAUNCHWRAPPER) {
               return Class.forName(className, true, getLaunchClassLoader());
               // Method md = getLaunchClassLoader().getClass().getDeclaredMethod("findClass", String.class);
               // return (Class<?>) md.invoke(getLaunchClassLoader(), className);
            } else {
                return Class.forName(className);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void applyClassPath(ClassPool pool) {
        if (AgentMain.IS_LAUNCHWRAPPER)
            pool.appendClassPath(new LoaderClassPath(getLaunchClassLoader()));
        else
            pool.appendClassPath(new LoaderClassPath(Minecraft.getMinecraft().getClass().getClassLoader()));
    }

    public static ClassLoader getLaunchClassLoader() {
        try {
            return (ClassLoader) Class.forName("net.minecraft.launchwrapper.Launch").getDeclaredField("classLoader").get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
