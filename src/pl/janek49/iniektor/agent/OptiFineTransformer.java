package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import net.minecraft.client.Minecraft;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

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
                    "return pl.janek49.iniektor.agent.OptiFineTransformer.OFCT_Patch_Hook($1, $2, $3, ofBytes);" +
                    "}");

            byte[] bytecode = optiClass.toBytecode();

            Class clz = Class.forName(classname);
            ClassDefinition cd = new ClassDefinition(clz, bytecode);

            inst.redefineClasses(cd);
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }

    public static byte[] OFCT_Patch_Hook(String name, String transformedName, byte[] byteCode, byte[] optifineByteCode) {
        Logger.log("OptiFine Transformer Hook:", name, transformedName);
        return optifineByteCode == null ? byteCode : optifineByteCode;
    }
}
