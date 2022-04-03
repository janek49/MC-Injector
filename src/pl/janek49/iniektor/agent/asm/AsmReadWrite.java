package pl.janek49.iniektor.agent.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

public class AsmReadWrite {

    private byte[] initialBytecode;
    private ClassReader classReader;
    private ClassWriter classWriter;


    public AsmReadWrite(byte[] initialBytecode) {
        this.initialBytecode = initialBytecode;
        this.classReader = new ClassReader(initialBytecode);
        this.classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    }

    public void nextReadWrite() {
        this.classReader = new ClassReader(classWriter.toByteArray());
        this.classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
    }

    public ClassReader getClassReader() {
        return classReader;
    }

    public ClassWriter getClassWriter() {
        return classWriter;
    }
}
