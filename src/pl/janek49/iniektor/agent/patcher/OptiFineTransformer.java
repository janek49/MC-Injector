package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.minecraft.launchwrapper.LaunchClassLoader;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.asm.AsmUtil;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.List;

public class OptiFineTransformer {

    public static void ApplyPatchOptifine(Instrumentation inst) {
        try {
            String classname = "optifine.OptiFineClassTransformer";
            Logger.log("Patching:", classname);

            ClassPool pool = ClassPool.getDefault();
            CtClass optiClass = pool.getCtClass(classname);

            optiClass.stopPruning(true);
            if (optiClass.isFrozen())
                optiClass.defrost();

            CtMethod mdTransform = optiClass.getMethod("transform", "(Ljava/lang/String;Ljava/lang/String;[B)[B");
            Logger.log("Patching method body: " + mdTransform.getLongName());

            mdTransform.setBody("{" +
                    "String nameClass = String.valueOf($1) + \".class\";" +
                    "byte[] ofBytes = getOptiFineResource(nameClass);" +
                    "return pl.janek49.iniektor.agent.patcher.OptiFineTransformer.OFCT_Patch_Hook($1, $2, $3, ofBytes);" +
                    "}");

            byte[] bytecode = optiClass.toBytecode();

            Class clz = Class.forName(classname);
            ClassDefinition cd = new ClassDefinition(clz, bytecode);

            inst.redefineClasses(cd);
        } catch (Throwable t) {
            Logger.ex(t);
        }
    }


    public static void AddJarToOFClassLoader(String jarFile) {
        try {
            Object o = AsmUtil.getLaunchClassLoader();
            Field sourcesField = LaunchClassLoader.class.getDeclaredField("sources");
            sourcesField.setAccessible(true);
            List<URL> sources = (List<URL>) sourcesField.get(o);
            sources.add(new URL("file://" + jarFile.replace("\\", "/")));
        } catch (Exception e) {
            Logger.ex(e);
        }
    }

    public static byte[] OFCT_Patch_Hook(String name, String transformedName, byte[] byteCode, byte[] optifineByteCode) throws Exception {
        //naprawia problem ze classloader optifina nie widzi naszych klas w swoim classpathie, classpool zawiera definicje wszystkich classloaderów
        if (name.startsWith("pl.janek49.")) {
            Logger.log("OptiFine Transformer Hook:", name, transformedName);
            return ClassPool.getDefault().get(name).toBytecode();
        } else {
            return optifineByteCode == null ? byteCode : optifineByteCode;
        }
    }
}
