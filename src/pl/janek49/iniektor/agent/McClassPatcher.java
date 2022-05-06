package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.CtClass;
import net.minecraft.launchwrapper.Launch;
import pl.janek49.iniektor.agent.asm.AsmUtil;

import java.io.File;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.nio.file.Files;

public abstract class McClassPatcher {
    public abstract byte[] patchClass(ClassPool pool, CtClass ctClass, String deobfName, String obfName) throws Exception;

    public McClassPatcher applyPatches(Instrumentation inst, String className) {
        return applyPatches(inst, className, true);
    }

    public McClassPatcher applyPatches(Instrumentation inst, String className, boolean obfuscateName) {
        try {
            Logger.log("Patching class:", className);

            String obfName = obfuscateName ? AgentMain.MAPPER.getObfClassName(className) : className;
            obfName = obfName.replace("/", ".");

            ClassPool classPool = ClassPool.getDefault();
            AsmUtil.applyClassPath(classPool);

            CtClass ctClass = classPool.get(obfName);
            ctClass.stopPruning(true);

            if (ctClass.isFrozen())
                ctClass.defrost();

            byte[] output = patchClass(classPool, ctClass, className, obfName);


            Class clz = AsmUtil.findClass(AgentMain.IS_FORGE ? className : obfName);
            ClassDefinition cd = new ClassDefinition(clz, output);

            Logger.log("Redefining class:", className);
            inst.redefineClasses(cd);
            Logger.log("Done");
        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            return this;
        }
    }
}
