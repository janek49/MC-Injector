package pl.janek49.iniektor.agent.asm;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.LoaderClassPath;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;
import pl.janek49.org.objectweb.asm.Label;
import pl.janek49.org.objectweb.asm.MethodVisitor;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import static pl.janek49.org.objectweb.asm.Opcodes.ASM5;

public class AsmUtil {
    public static Class<?> findClass(String className) {
        try {
            className = className.replace("/", ".");
            if (AgentMain.IS_LAUNCHWRAPPER) {
                return Class.forName(className, true, getLaunchClassLoader());
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

    public static String getPackage(Class clazz) {
        String name = clazz.getName();
        return name.substring(0, name.lastIndexOf('.'));
    }

    public MethodVisitor getLocalVarDeobfuscator(MethodVisitor parent) {
        return new MethodVisitor(ASM5, parent) {
            @Override
            public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
                String newName = (name + desc);
                super.visitLocalVariable(("var" + newName.hashCode()).replace("-", "_"), desc, signature, start, end, index);
            }
        };
    }

    public static Unsafe getUnsafe() {
        try {
            Field singleoneInstanceField = Unsafe.class.getDeclaredField("theUnsafe");
            singleoneInstanceField.setAccessible(true);
            return (Unsafe) singleoneInstanceField.get(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean doesClassExist(String className){
        try {
            Class.forName(className);
            return true;
        } catch (ClassNotFoundException | NoClassDefFoundError ex) {
            return false;
        }
    }
}
