package pl.janek49.iniektor.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.ClassRemapper;
import sun.rmi.runtime.Log;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class IniektorTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] byteCode) {
        try {
            if (className == null)
                return byteCode;

            //transformacja klas klienta
            if (className.startsWith("pl/janek49/iniektor/")) {
                Logger.log("Transforming Iniektor class: " + className);
                return AsmMinecraftObfuscator.remapNetMinecraftClasses(byteCode);
            }

            return byteCode;
        } catch (Throwable t) {
            t.printStackTrace();
            return byteCode;
        }
    }
}
