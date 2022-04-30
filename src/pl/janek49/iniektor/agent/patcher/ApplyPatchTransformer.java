package pl.janek49.iniektor.agent.patcher;

import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmUtil;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;
import java.util.HashMap;

public class ApplyPatchTransformer implements ClassFileTransformer {

    public HashMap<String, IPatch> patchList = new HashMap<>();

    public ApplyPatchTransformer() {
        AddPatch(new PatchMinecraft());


        if (AgentMain.MCP_VERSION.ordinal() > Version.MC1_7_10.ordinal()) {
            AddPatch(new PatchGuiScreen());
        } else if (AgentMain.MCP_VERSION == Version.MC1_7_10) {
            AddPatch(new PatchGuiChat());
        } else {
            AddPatch(new PatchEntityClientPlayerMP());
        }

        AddPatch(new PatchNetworkManager());
        AddPatch(new PatchIniektorGuiScreen());

        if (AgentMain.MCP_VERSION.ordinal() >= Version.MC1_14_4.ordinal()) {
            AddPatch(new PatchGuiIngame("net/minecraft/client/gui/Gui", "render"));
        } else {
            if (AgentMain.IS_FORGE) {
                AddPatch(new PatchGuiIngameForge());
            } else {
                AddPatch(new PatchGuiIngame("net/minecraft/client/gui/GuiIngame", "renderGameOverlay"));
            }
        }
    }

    public void AddPatch(IPatch patch) {
        patch.obfName = AgentMain.MAPPER.getObfClassNameIfExists(patch.deobfNameToPatch.replace(".", "/"));
        patchList.put(patch.obfName, patch);
    }

    public void ApplyPatches(Instrumentation inst) {
        for (IPatch patch : patchList.values()) {
            try {
                if (!patch.doNotInit)
                    inst.retransformClasses(AsmUtil.findClass(patch.obfName));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] byteCode) {
        if (className != null && patchList.containsKey(className)) {
            try {
                Logger.log("Applying patch for class:", className, "(" + patchList.get(className).deobfNameToPatch + ")");
                return patchList.get(className).TransformClass(className, byteCode);
            } catch (Exception ex) {
                Logger.log("ERROR: Applying patch failed:", className);
                ex.printStackTrace();
            }
        }
        return byteCode;
    }

}
