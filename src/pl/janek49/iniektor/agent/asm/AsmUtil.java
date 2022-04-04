package pl.janek49.iniektor.agent.asm;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import pl.janek49.iniektor.agent.AgentMain;

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

    public static byte[] RunClassByLaunchTransformers(byte[] input, String obfName, String deobfName) {
        try {
            LaunchClassLoader lcl = Launch.classLoader;

            Method md = lcl.getClass().getDeclaredMethod("runTransformers", String.class, String.class, byte[].class);
            md.setAccessible(true);

            byte[] output = (byte[]) md.invoke(lcl, deobfName, obfName, input);

            return output;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static CtClass getCtClassFromBytecode(String className, byte[] bytecode) {
        try {
            String dotclassName = className.replace("/", ".");

            ClassPool pool = ClassPool.getDefault();
            pool.insertClassPath(new ByteArrayClassPath(dotclassName, bytecode));

            CtClass ctClass = pool.get(dotclassName);
            return ctClass;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    public static String generateSignature(String[] params, String returnType) {
        String[] ctx = new String[params.length + 1];
        int i = 0;
        for (String str : params) {
            if (str.contains("/")) {
                ctx[i] = ("L" + str + ";");
            } else {
                ctx[i] = (str);
            }
            i++;
        }
        String sig = "(" + String.join("", ctx) + ")";
        return sig;
    }
}
