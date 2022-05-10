package pl.janek49.iniektor.addon;

import javassist.ClassPool;
import javassist.CtClass;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.McClassPatcher;
import pl.janek49.iniektor.agent.asm.AsmUtil;

import javax.swing.*;
import java.awt.*;

public class CraftingDeadTimerPatch {
    public static void patchEntry(Container window, JComponent sender) {
        try {
            String timerDetectorClass = "com.craftingdead.client.j";

            if (!AsmUtil.doesClassExist(timerDetectorClass)) {
                JOptionPane.showMessageDialog(window, "Could not find CDC modpack - missing class: " + timerDetectorClass, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            McClassPatcher mc = new McClassPatcher() {
                @Override
                public byte[] patchClass(ClassPool pool, CtClass ctClass, String deobfName, String obfName) throws Exception {
                    ctClass.getDeclaredMethods()[0].setBody("{}");
                    return AsmUtil.runClassByLaunchTransformers(ctClass.toBytecode(), obfName, deobfName);
                }
            };

            mc.applyPatchesUnsafe(AgentMain.INSTRUMENTATION, timerDetectorClass, false);

            JOptionPane.showMessageDialog(window, "Success!", "Info", JOptionPane.INFORMATION_MESSAGE);
            sender.setEnabled(false);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(window, Util.printException(ex), "Error", JOptionPane.ERROR_MESSAGE);
            sender.setEnabled(true);
        }
    }
}
