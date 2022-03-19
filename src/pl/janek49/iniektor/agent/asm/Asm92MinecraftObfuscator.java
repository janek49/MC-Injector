package pl.janek49.iniektor.agent.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import pl.janek49.iniektor.agent.AgentMain;

public class Asm92MinecraftObfuscator {


    public static byte[] remapNetMinecraftClasses(byte[] bytecode) {
        ClassReader reader = new ClassReader(bytecode);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassRemapper remapper = new ClassRemapper(writer, AgentMain.IS_FORGE ? new ForgeClassRemapper() : new MinecraftClassRemapper());
        reader.accept(remapper, ClassReader.EXPAND_FRAMES);

        return writer.toByteArray();
    }

}

