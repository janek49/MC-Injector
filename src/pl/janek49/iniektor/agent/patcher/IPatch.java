package pl.janek49.iniektor.agent.patcher;

import javassist.ByteArrayClassPath;
import javassist.ClassPool;
import javassist.CtClass;

public abstract class IPatch {

    public String deobfNameToPatch;
    public String obfName;

    public IPatch(String target) {
        deobfNameToPatch = target;
    }

    public abstract byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception;

    public byte[] TransformClass(String className, byte[] byteCode) throws Exception {
        String dotclassName = className.replace("/", ".");

        ClassPool pool = ClassPool.getDefault();
        pool.insertClassPath(new ByteArrayClassPath(dotclassName, byteCode));

        CtClass ctClass = pool.get(dotclassName);
        ctClass.defrost();

        return PatchClassImpl(className, pool, ctClass, byteCode);
    }

}
