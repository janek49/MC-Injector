package pl.janek49.iniektor.agent.asm;

import jdk.internal.org.objectweb.asm.Opcodes;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class Asm92MinecraftObfuscator {

    public static byte[] remapNetMinecraftClasses(byte[] bytecode) throws IOException {
        ClassReader reader = new ClassReader(bytecode);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassRemapper remapper = new ClassRemapper(writer, AgentMain.IS_FORGE ? new ForgeClassRemapper() : new MinecraftClassRemapper());

        TransformerAnnotationAdapter.AcceptFor(reader, remapper);

        return writer.toByteArray();
    }

}

