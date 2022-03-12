package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.minecraft.launchwrapper.Launch;
import net.minecraft.launchwrapper.LaunchClassLoader;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.util.Map;

public class LaunchWrapperPatcher {

    public static void ClearCacheInLaunchClassLoader() {
        try {
            Field cachedField = LaunchClassLoader.class.getDeclaredField("cachedClasses");
            cachedField.setAccessible(true);
            Map<String, Class<?>> cacheMap = (Map<String, Class<?>>) cachedField.get(Launch.classLoader);
            cacheMap.clear();
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static Instrumentation INSTR;

    public static void ApplyPatchLaunchClassLoader(Instrumentation inst) {
        try {
            INSTR = inst;
            String classname = "net.minecraft.launchwrapper.LaunchClassLoader";
            Logger.log("Patching:", classname);

            ClassPool pool = ClassPool.getDefault();
            CtClass llClass = pool.getCtClass(classname);

            llClass.stopPruning(true);
            if (llClass.isFrozen())
                llClass.defrost();

            CtMethod mdTransform = llClass.getMethod("findClass", "(Ljava/lang/String;)Ljava/lang/Class;");
            Logger.log("Patching method body: " + mdTransform.getLongName());
            mdTransform.insertBefore("{ pl.janek49.iniektor.agent.LaunchWrapperPatcher.LCL_Patch_Hook(this, $1); }");

            byte[] bytecode = llClass.toBytecode();

            Class clz = Class.forName(classname);
            ClassDefinition cd = new ClassDefinition(clz, bytecode);
            inst.redefineClasses(cd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static void insertInto(CtClass llClass, String name, String desc, String insert) throws Exception {
        CtMethod mdTransform = llClass.getMethod(name, desc);
        Logger.log("Patching method body: " + mdTransform.getLongName());
        mdTransform.insertBefore("{" + insert + " new Throwable().printStackTrace();}");

    }


    public static void LCL_Patch_Hook(LaunchClassLoader lcl, String name) {
        Logger.log("LaunchClassLoader Hook:", name);

    }
}
