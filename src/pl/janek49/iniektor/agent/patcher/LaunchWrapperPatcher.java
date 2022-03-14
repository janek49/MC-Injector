package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.minecraft.launchwrapper.LaunchClassLoader;
import pl.janek49.iniektor.agent.AsmUtil;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.McClassPatcher;

import java.lang.instrument.Instrumentation;

public class LaunchWrapperPatcher {

    public static void ApplyPatch(Instrumentation inst) {
        new McClassPatcher() {
            @Override
            public byte[] patchClass(CtClass ctClass, String deobfName, String obfName) throws Exception {
                CtMethod ctm = ctClass.getMethod("findClass", "(Ljava/lang/String;)Ljava/lang/Class;");
                Logger.log("Patching method body:", ctm.getLongName());
                ctm.insertBefore(
                        "{ if(pl.janek49.iniektor.agent.patcher.LaunchWrapperPatcher.HOOK_IsIniektorClass($1)){" +
                                "byte[] byteCode = pl.janek49.iniektor.agent.patcher.LaunchWrapperPatcher.HOOK_GetByteCode($1);" +
                                    "return defineClass($1, byteCode, 0, byteCode.length);" +
                                "} }");
                return ctClass.toBytecode();
            }
        }.applyPatches(inst, "net/minecraft/launchwrapper/LaunchClassLoader", false);
    }

    public static final boolean HOOK_IsIniektorClass(String className) {
        if (className.startsWith("pl.janek49.iniektor.")) {
            Logger.log("LaunchClassLoader HOOK_IsIniektorClass: " + className);
            return true;
        }
        return false;
    }

    public static final byte[] HOOK_GetByteCode(String className) throws Exception {
        Logger.log("LaunchClassLoader HOOK_GetByteCode:", className);
        return ClassPool.getDefault().get(className).toBytecode();
    }
}
