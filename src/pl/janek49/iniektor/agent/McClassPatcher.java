package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.CtClass;
import pl.janek49.iniektor.agent.asm.AsmUtil;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public abstract class McClassPatcher {
    public abstract byte[] patchClass(ClassPool pool, CtClass ctClass, String deobfName, String obfName) throws Exception;

    public McClassPatcher applyPatches(Instrumentation inst, String className) {
        return applyPatches(inst, className, true);
    }

    public boolean throwEx = false;

    public McClassPatcher applyPatches(Instrumentation inst, String className, boolean obfuscateName) {
        try {
            applyPatchesUnsafe(inst, className, obfuscateName);
        } catch (Throwable t) {
            Logger.ex(t);
        } finally {
            return this;
        }
    }

    public McClassPatcher applyPatchesUnsafe(Instrumentation inst, String className, boolean obfuscateName) throws Exception {
        Logger.log("Patching class:", className);

        String obfName = obfuscateName ? AgentMain.MAPPER.getObfClassName(className) : className;
        obfName = obfName.replace("/", ".");

        ClassPool classPool = new ClassPool();
        classPool.appendSystemPath();
        AsmUtil.applyClassPath(classPool);

        CtClass ctClass = classPool.get(obfName);
        ctClass.stopPruning(true);

        if (ctClass.isFrozen())
            ctClass.defrost();

        byte[] output = patchClass(classPool, ctClass, className, obfName);

        //AsmUtil.removeCP(classPool);

        Class clz = AsmUtil.findClass(AgentMain.IS_FORGE ? className : obfName);
        ClassDefinition cd = new ClassDefinition(clz, output);

        Logger.log("Redefining class:", className);
        inst.redefineClasses(cd);
        Logger.log("Done");
        return this;
    }
}
