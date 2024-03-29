package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmReadWrite;
import pl.janek49.org.objectweb.asm.ClassReader;
import pl.janek49.org.objectweb.asm.ClassVisitor;
import pl.janek49.org.objectweb.asm.MethodVisitor;
import pl.janek49.org.objectweb.asm.Opcodes;

public class PatchIniektorGuiScreen extends IPatch {

    public PatchIniektorGuiScreen() {
        super("pl/janek49/iniektor/client/gui/IniektorGuiScreen");
        doNotInit = true;
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {

        String newParent = "net/minecraft/client/gui/GuiScreen";

        boolean isMc114 = AgentMain.MCP_VERSION.ordinal() >= Version.MC1_14_4.ordinal();

        if (isMc114)
            newParent = "net/minecraft/client/gui/screens/Screen";

        String newParentClass = AgentMain.MAPPER.getObfClassName(newParent);

        Logger.log("Setting IniektorGuiScreen superclass ->", newParentClass);

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


        return arw.toByteCode();

    }
}
