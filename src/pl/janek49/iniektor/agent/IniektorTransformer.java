package pl.janek49.iniektor.agent;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.commons.RemappingClassAdapter;
import pl.janek49.iniektor.agent.asm.*;

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
                AsmReadWrite asm = new AsmReadWrite(byteCode);

                TransformerAnnotationAdapter.AcceptFor(asm.getClassReader(), asm.getClassWriter());

                return asm.getClassWriter().toByteArray();
            }

            return byteCode;
        } catch (Throwable t) {
            Logger.log("ERROR while transforming Iniektor class: " + className);
            t.printStackTrace();
            return byteCode;
        }
    }
}
