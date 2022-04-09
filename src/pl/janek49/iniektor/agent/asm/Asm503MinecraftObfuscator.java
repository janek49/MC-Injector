package pl.janek49.iniektor.agent.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.RemappingClassAdapter;
import pl.janek49.iniektor.agent.AgentMain;

public class Asm503MinecraftObfuscator {
    public static byte[] asm503remapNetMinecraftClasses(byte[] bytecode) {
        AsmReadWrite asm = new AsmReadWrite(bytecode);

        asm.getClassReader().accept(new RemappingClassAdapter(asm.getClassWriter(), AgentMain.IS_FORGE ? new ForgeClassRemapper() : new MinecraftClassRemapper()), ClassReader.EXPAND_FRAMES);
        asm.nextReadWrite();

        TransformerAnnotationAdapter.AcceptFor(asm.getClassReader(), asm.getClassWriter());

        return asm.getClassWriter().toByteArray();
    }
}
