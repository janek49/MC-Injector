package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmReadWrite;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;

public class PatchIniektorGuiScreen extends IPatch {

    public PatchIniektorGuiScreen() {
        super("pl.janek49.iniektor.client.gui.IniektorGuiScreen");
        // doNotInit = true;
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        String newParent = "net/minecraft/client/gui/GuiScreen";

        boolean isMc114 = AgentMain.MCP_VERSION.ordinal() >= Version.MC1_14_4.ordinal();

        if (isMc114)
            newParent = "net/minecraft/client/gui/screens/Screen";

        String newParentClass = AgentMain.MAPPER.getObfClassName(newParent);

        AsmReadWrite arw = new AsmReadWrite(byteCode);

        arw.getClassReader().accept(new ClassVisitor(Opcodes.ASM5, arw.getClassWriter()) {

            @Override
            public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
                super.visit(version, access, name, signature, newParentClass, interfaces);
            }

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor parentMV = super.visitMethod(access, name, desc, signature, exceptions);

                if (!name.equals("<init>"))
                    return parentMV;

                if (isMc114) {
                    String textComponentClass = AgentMain.MAPPER.getObfClassName("net/minecraft/network/chat/TextComponent");
                    String componentClass = AgentMain.MAPPER.getObfClassName("net/minecraft/network/chat/Component");

                    parentMV.visitVarInsn(Opcodes.ALOAD, 0);
                    parentMV.visitTypeInsn(Opcodes.NEW, textComponentClass);
                    parentMV.visitInsn(Opcodes.DUP);
                    parentMV.visitLdcInsn("");
                    parentMV.visitMethodInsn(Opcodes.INVOKESPECIAL, textComponentClass, "<init>", "(Ljava/lang/String;)V", false);
                    parentMV.visitMethodInsn(Opcodes.INVOKESPECIAL, newParentClass, "<init>", "(L" + componentClass + ";)V", false);
                } else {
                    parentMV.visitVarInsn(Opcodes.ALOAD, 0);
                    parentMV.visitMethodInsn(Opcodes.INVOKESPECIAL, newParentClass, "<init>", "()V", false);
                }

                return new MethodVisitor(Opcodes.ASM5, parentMV) {
                    @Override
                    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                        if (opcode == Opcodes.INVOKESPECIAL && owner.equals("java/lang/Object") && name.equals("<init>")) {
                            super.visitInsn(Opcodes.POP);
                        } else {
                            super.visitMethodInsn(opcode, owner, name, desc, itf);
                        }
                    }
                };
            }
        }, ClassReader.EXPAND_FRAMES);


        byteCode = arw.getClassWriter().toByteArray();
        Path path = new File("IniektorGuiScreen.class").toPath();
        Files.write(path, byteCode);
        return byteCode;

    }
}
