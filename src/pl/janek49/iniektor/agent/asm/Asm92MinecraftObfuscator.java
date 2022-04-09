package pl.janek49.iniektor.agent.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.ClassRemapper;
import pl.janek49.iniektor.agent.AgentMain;

import java.io.IOException;

public class Asm92MinecraftObfuscator {

    public static byte[] asm92remapNetMinecraftClasses(byte[] bytecode) throws IOException {
        AsmReadWrite asm = new AsmReadWrite(bytecode);

        asm.getClassReader().accept(new ClassRemapper(asm.getClassWriter(), AgentMain.IS_FORGE ? new ForgeClassRemapper() : new MinecraftClassRemapper()), ClassReader.EXPAND_FRAMES);
        asm.nextReadWrite();

        TransformerAnnotationAdapter.AcceptFor(asm.getClassReader(), asm.getClassWriter());

        return asm.getClassWriter().toByteArray();
    }





}

