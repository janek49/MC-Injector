package pl.janek49.iniektor.agent;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class IniektorTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] byteCode) {
        try {
            if (className != null && className.startsWith("pl/janek49/iniektor/")) {
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
