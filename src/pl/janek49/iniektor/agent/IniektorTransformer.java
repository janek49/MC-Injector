package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.agent.asm.Asm503MinecraftObfuscator;
import pl.janek49.iniektor.agent.asm.Asm92MinecraftObfuscator;
import pl.janek49.iniektor.agent.patcher.PatchMinecraft;

import java.io.File;
import java.lang.instrument.ClassFileTransformer;
import java.nio.file.Files;
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

                if (AgentMain.USE_ASM_503) {
                    return Asm503MinecraftObfuscator.remapNetMinecraftClasses(byteCode);
                } else {
                    return Asm92MinecraftObfuscator.remapNetMinecraftClasses(byteCode);
                }
            }

            return byteCode;
        } catch (Throwable t) {
            t.printStackTrace();
            return byteCode;
        }
    }
}
