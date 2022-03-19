package pl.janek49.iniektor.agent.asm;

import javassist.ClassPool;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.Remapper;
import org.objectweb.asm.commons.RemappingClassAdapter;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.mapper.Mapper;

import java.util.ArrayList;
import java.util.List;

public class Asm503MinecraftObfuscator {

    public static byte[] remapNetMinecraftClasses(byte[] bytecode) {
        ClassReader reader = new ClassReader(bytecode);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        RemappingClassAdapter remapper = new RemappingClassAdapter(writer, AgentMain.IS_FORGE ?  new ForgeClassRemapper() :  new MinecraftClassRemapper());
        reader.accept(remapper, ClassReader.EXPAND_FRAMES);

        return writer.toByteArray();
    }


}

