package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.agent.asm.AsmReadWrite;
import pl.janek49.iniektor.agent.asm.TransformerAnnotationAdapter;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class IniektorTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] byteCode) {
        try {
            if (className == null)
                return byteCode;

            //Logger.log("------Transforming class: " + className);

            //transformacja klas klienta
            if (className.startsWith("pl/janek49/iniektor/")) {
               Logger.log("Transforming Iniektor class: " + className);
                AsmReadWrite asm = new AsmReadWrite(byteCode);

                TransformerAnnotationAdapter.AcceptFor(asm.getClassReader(), asm.getClassWriter());

                return asm.getClassWriter().toByteArray();
            }

            return byteCode;
        } catch (Throwable t) {
            Logger.err("ERROR while transforming Iniektor class: " + className);
            Logger.ex(t);
            return byteCode;
        }
    }
}
