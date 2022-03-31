package pl.janek49.iniektor.agent.asm;

import org.objectweb.asm.*;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;

import java.util.HashMap;

public class TransformerAnnotationAdapter extends ClassVisitor {

    public static void AcceptFor(ClassReader reader, ClassVisitor writer) {
        TransformerAnnotationAdapter trAnot = new TransformerAnnotationAdapter(Opcodes.ASM5);
        reader.accept(trAnot, ClassReader.EXPAND_FRAMES);
        reader.accept(new MethodRenamerByAnnotation(trAnot.discoveredMethods, Opcodes.ASM5, writer), ClassReader.EXPAND_FRAMES);
    }

    public HashMap<String, MappingEntry> discoveredMethods = new HashMap<>();

    public TransformerAnnotationAdapter(int i) {
        super(i);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new TransfomerMethodVisitor(this, name, desc, this.api);
    }


    class MappingEntry {
        public MappingEntry(String srcName, String srcDescriptor, TransformerMethodAnnotationVisitor target) {
            this.srcName = srcName;
            this.srcDescriptor = srcDescriptor;
            this.target = target;
        }

        public String srcName, srcDescriptor;
        public TransformerMethodAnnotationVisitor target;
    }


    class TransfomerMethodVisitor extends MethodVisitor {

        public String methodName, methodDesc;

        public TransformerMethodAnnotationVisitor annotationVisitor;

        public TransformerAnnotationAdapter parent;

        public TransfomerMethodVisitor(TransformerAnnotationAdapter parent, String name, String desc, int i) {
            super(i);
            this.parent = parent;
            methodName = name;
            methodDesc = desc;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String s, boolean b) {
            if (s.equals("L" + RenameMethod.class.getName().replace(".", "/") + ";")) {
                return annotationVisitor = new TransformerMethodAnnotationVisitor(this.api);
            }
            return super.visitAnnotation(s, b);
        }

        @Override
        public void visitEnd() {
            if (annotationVisitor != null) {
                Logger.log(annotationVisitor.targetVersion, annotationVisitor.targetMethodName, annotationVisitor.targetMethodDesc);
                parent.discoveredMethods.put(methodName + " " + methodDesc, new MappingEntry(methodName, methodDesc, annotationVisitor));
            }
            super.visitEnd();
        }
    }

    class TransformerMethodAnnotationVisitor extends AnnotationVisitor {
        public TransformerMethodAnnotationVisitor(int i) {
            super(i);
        }

        public Version targetVersion;
        public String targetMethodName;
        public String targetMethodDesc;

        @Override
        public void visitEnum(String s, String s1, String s2) {
            if (s.equals("version"))
                targetVersion = Version.valueOf(s2);
            super.visitEnum(s, s1, s2);
        }

        @Override
        public void visit(String s, Object o) {
            if (s.equals("name"))
                targetMethodName = (String) o;
            else if (s.equals("descriptor"))
                targetMethodDesc = (String) o;

            super.visit(s, o);
        }
    }
}