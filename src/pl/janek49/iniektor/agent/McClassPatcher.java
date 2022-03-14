package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.CtClass;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public abstract class McClassPatcher {
    public abstract byte[] patchClass(CtClass ctClass, String deobfName, String obfName) throws Exception;

    public McClassPatcher applyPatches(Instrumentation inst, String className) {
        return applyPatches(inst, className, true);
    }

    public McClassPatcher applyPatches(Instrumentation inst, String className, boolean obfuscateName) {
        try {
            Logger.log("Patching class:", className);
            String classname = className.replace(".", "/");
            String obfName = obfuscateName ? AgentMain.MAPPER.getObfClassName(classname) : className;
            obfName = obfName.replace("/", ".");

            ClassPool classPool = ClassPool.getDefault();
            AsmUtil.applyClassPath(classPool);

            CtClass ctClass = classPool.get(obfName);
            ctClass.stopPruning(true);

            if (ctClass.isFrozen())
                ctClass.defrost();

            byte[] output = patchClass(ctClass, className, obfName);

            Class clz = AsmUtil.findClass(obfName);
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
