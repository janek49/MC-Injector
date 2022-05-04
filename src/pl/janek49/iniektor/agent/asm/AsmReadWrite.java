package pl.janek49.iniektor.agent.asm;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import pl.janek49.iniektor.agent.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class AsmReadWrite {

    private final byte[] initialBytecode;
    private ClassReader classReader;
    private ClassWriter classWriter;


    public AsmReadWrite(byte[] initialBytecode) {
        this.initialBytecode = initialBytecode;
        this.classReader = new ClassReader(initialBytecode);
        this.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
    }

    public void nextReadWrite() {
        this.classReader = new ClassReader(classWriter.toByteArray());
        this.classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES + ClassWriter.COMPUTE_MAXS);
    }

    public ClassReader getClassReader() {
        return classReader;
    }

    public ClassWriter getClassWriter() {
        return classWriter;
    }

    public byte[] getInitialBytecode() {
        return initialBytecode;
    }

    public void dumpClass(String s) throws IOException {
        Path path = new File(s).toPath();
        Logger.log("AsmReadWrite->dumpClass:", path.toAbsolutePath().toString());
        Files.write(path, classWriter.toByteArray());
    }
}
