package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import org.objectweb.asm.*;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmReadWrite;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.IniektorHooks;
import pl.janek49.iniektor.mapper.Mapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.objectweb.asm.Opcodes.*;

public class PatchNetworkManager extends IPatch {
    public PatchNetworkManager() {
        addFirst(new PatchTarget(Version.MC1_14_4, Version.Compare.OR_HIGHER,
                "net/minecraft/network/Connection", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/protocol/Packet;)V"));

        addFirst(new PatchTarget(Version.MC1_6_4, Version.Compare.EQUAL,
                "net/minecraft/src/MemoryConnection", "processReadPackets", "()V"));

        addFirst(new PatchTarget(Version.DEFAULT, Version.Compare.EQUAL,
                "net/minecraft/network/NetworkManager", "channelRead0", "(Lio/netty/channel/ChannelHandlerContext;Lnet/minecraft/network/Packet;)V"));
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {
        pool.importPackage(AsmUtil.getPackage(IniektorHooks.class));

        PatchTarget pt = getFirstPatchTarget();

        Logger.log("Patching method body:", pt);

        String hookClass = IniektorHooks.class.getName().replace(".", "/");
        String hookMethod = "HookCancelReceivedPacket";

        Mapper.MethodMatch obfTarget = AgentMain.MAPPER.findMethodMapping(pt);

        assert obfTarget != null;

        if (pt.version == Version.MC1_6_4) {
            return insert164PacketHook(obfTarget, byteCode, hookClass, hookMethod);
        } else {
            return insertModernPacketHook(obfTarget, byteCode, hookClass, hookMethod);
        }

    }


    private byte[] insertModernPacketHook(Mapper.MethodMatch obfTarget, byte[] in, String hookClass, String hookName) throws Exception {
        AsmReadWrite arw = new AsmReadWrite(in);

        arw.getClassReader().accept(new ClassVisitor(ASM5, arw.getClassWriter()) {
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                if (name.equals(obfTarget.obfName) && desc.equals(obfTarget.obfDesc)) {
                    Label label = new Label();

                    //load local var 2 (packet instance)
                    mv.visitVarInsn(ALOAD, 2);
                    //invoke our hook with given instance
                    mv.visitMethodInsn(INVOKESTATIC, hookClass, hookName, "(Ljava/lang/Object;)Z", false);
                    //store result of hook (boolean = int) into local var 3
                    mv.visitVarInsn(ISTORE, 3);
                    //load var 3 onto stack
                    mv.visitVarInsn(ILOAD, 3);
                    //ifeq jumps to label when return value is 0 (false = don't cancel), pointing to the original code skipping our return
                    mv.visitJumpInsn(IFEQ, label);

                    mv.visitInsn(RETURN);
                    //original code gets assigned to new label, so we can jump to it
                    mv.visitLabel(label);
                }

                return mv;
            }
        }, ClassReader.EXPAND_FRAMES);

        return arw.toByteCode();
    }


    private byte[] insert164PacketHook(Mapper.MethodMatch obfTarget, byte[] in, String hookClass, String hookName) throws IOException {

        AsmReadWrite arw = new AsmReadWrite(in);

        arw.getClassReader().accept(new ClassVisitor(Opcodes.ASM5, arw.getClassWriter()) {
            @Override
            public MethodVisitor visitMethod(int access, String mtdName, String mtdDesc, String mtdSig, String[] exceptions) {
                if (!mtdName.equals(obfTarget.obfName) || !mtdDesc.equals(obfTarget.obfDesc))
                    return super.visitMethod(access, mtdName, mtdDesc, mtdSig, exceptions);

                return new MethodVisitor(Opcodes.ASM5, super.visitMethod(access, mtdName, mtdDesc, mtdSig, exceptions)) {

                    int[] lastInsn = new int[2];
                    boolean found = false;

                    final List<Label> labels = new ArrayList<>();

                    @Override
                    public void visitLabel(Label label) {
                        labels.add(label);
                        super.visitLabel(label);
                    }

                    @Override
                    public void visitVarInsn(int opcode, int var) {
                        if (!found) {
                            if (opcode == Opcodes.ALOAD && var == 2
                                    && lastInsn[0] == Opcodes.ASTORE && lastInsn[1] == 2) {
                                found = true;
                                Logger.log("1.6.4 MemoryConnection ASM Patch");

                                //load local var 2 (packet instance)
                                super.visitVarInsn(Opcodes.ALOAD, 2);
                                //invoke our hook with given instance
                                super.visitMethodInsn(Opcodes.INVOKESTATIC, hookClass, hookName, "(Ljava/lang/Object;)Z", false);
                                //store result of hook (boolean = int) into local var 3
                                super.visitVarInsn(Opcodes.ISTORE, 3);
                                //load local var 3 onto stack (cancellation result)
                                super.visitVarInsn(Opcodes.ILOAD, 3);
                                //ifNE jumps to given label if the value on stack is other than 0 (true = cancel, so go back to the loop)
                                super.visitJumpInsn(Opcodes.IFNE, labels.get(1));

                            }
                        }

                        super.visitVarInsn(opcode, var);
                        lastInsn = new int[]{opcode, var};
                    }
                };

            }
        }, ClassReader.EXPAND_FRAMES);

        return arw.toByteCode();
    }
}
