package pl.janek49.iniektor.agent;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMember;
import javassist.CtMethod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiIngame;

import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;

public class ClientInit {


    public static void init(Instrumentation inst) {
        ApplyPatchGuiIngame();
    }

    private static void ApplyPatchGuiIngame() {
        try {
            ClassPool classPool = ClassPool.getDefault();
            CtClass guiIngame = classPool.get(GuiIngame.class.getName());
            guiIngame.stopPruning(true);

            if (guiIngame.isFrozen())
                guiIngame.defrost();

            String renderGameOverlayMethodName = "renderGameOverlay";
            String[] rgoMethodObf = AgentMain.MAPPER.getObfMethodNameWithoutClass(GuiIngame.class.getName() + "/" + renderGameOverlayMethodName, "(F)V");
            CtMethod renderGameOverlay = guiIngame.getMethod(rgoMethodObf[0], rgoMethodObf[1]);


            renderGameOverlay.insertAt(1, "{  }");

        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
