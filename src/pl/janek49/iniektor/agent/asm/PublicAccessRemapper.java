package pl.janek49.iniektor.agent.asm;

import org.objectweb.asm.*;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class PublicAccessRemapper extends ClassVisitor implements ClassFileTransformer {

    public PublicAccessRemapper(){
        super(Opcodes.ASM5);
    }

    public PublicAccessRemapper(ClassVisitor classVisitor) {
        super(Opcodes.ASM5, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return super.visitMethod(Opcodes.ACC_PUBLIC, name, desc, signature, exceptions);
    }

    @Override
    public FieldVisitor visitField(int i, String s, String s1, String s2, Object o) {
        return super.visitField(Opcodes.ACC_PUBLIC, s, s1, s2, o);
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        if (className != null && (!className.contains("/") || className.startsWith("net/minecraft/"))) {
            AsmReadWrite asm = new AsmReadWrite(classfileBuffer);
            asm.getClassReader().accept(new PublicAccessRemapper(asm.getClassWriter()), ClassReader.EXPAND_FRAMES);
            return asm.getClassWriter().toByteArray();
        }

        return classfileBuffer;
    }
}
