package pl.janek49.iniektor.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.commons.ClassRemapper;
import org.objectweb.asm.commons.Remapper;

public class AsmMinecraftObfuscator {

    public static byte[] remapNetMinecraftClasses(byte[] bytecode) {
        ClassReader reader = new ClassReader(bytecode);
        ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);

        ClassRemapper remapper = new ClassRemapper(writer, new MinecraftClassRemapper());
        reader.accept(remapper, ClassReader.EXPAND_FRAMES);

        return writer.toByteArray();
    }

    private static class MinecraftClassRemapper extends Remapper {
        @Override
        public String map(String from) {
            if (from.startsWith("net/minecraft/")) {
                String classNameObf = AgentMain.MAPPER.getObfClassName(from);
                if (classNameObf != null)
                    return classNameObf;
            }
            return from;
        }

        @Override
        public String mapMethodName(String owner, String name, String descriptor) {
            if (owner.startsWith("net/minecraft/")) {
                String[] newName = AgentMain.MAPPER.getObfMethodName(owner + "/" + name, descriptor);
                if (newName != null) {
                    String[] obfNameParted = newName[0].split("/");
                    String[] newOwnerParted = new String[obfNameParted.length - 1];
                    System.arraycopy(obfNameParted, 0, newOwnerParted, 0, obfNameParted.length - 1);

                    owner = String.join("/", newOwnerParted);
                    name = obfNameParted[obfNameParted.length - 1];
                    descriptor = newName[1];
                }
            }
            return super.mapMethodName(owner, name, descriptor);
        }

        @Override
        public String mapFieldName(String owner, String name, String descriptor) {
            if (owner.startsWith("net/minecraft/")) {
                String newName = AgentMain.MAPPER.getObfFieldName(owner + "/" + name);
                if (newName != null) {
                    String[] obfNameParted = newName.split("/");
                    String[] newOwnerParted = new String[obfNameParted.length - 1];
                    System.arraycopy(obfNameParted, 0, newOwnerParted, 0, obfNameParted.length - 1);

                    owner = String.join("/", newOwnerParted);
                    name = obfNameParted[obfNameParted.length - 1];
                }
            }
            return super.mapFieldName(owner, name, descriptor);
        }
    }
}

