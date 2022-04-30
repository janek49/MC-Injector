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
        //main mc class
        AddPatch(new PatchMinecraft());

        //hooks for sending chat message
        AddPatch(new Patch_LegacySendChatMessageHook());
        AddPatch(new PatchGuiScreen());

        AddPatch(new PatchNetworkManager());

        //change superclass to guiscreen
        AddPatch(new PatchIniektorGuiScreen());

        //Hook for ingame ui overlay
        if (AgentMain.IS_FORGE) {
            AddPatch(new PatchGuiIngameForge());
        } else {
            AddPatch(new PatchGuiIngame());
        }
    }

    public void AddPatch(IPatch patch) {
        if (patch.deobfNameToPatch != null) {
            patch.obfName = AgentMain.MAPPER.getObfClassNameIfExists(patch.deobfNameToPatch.replace(".", "/"));
            patchList.put(patch.obfName, patch);
        } else {
            for (PatchTarget pt : patch.getApplicableTargets()) {
                String obfOwner = AgentMain.MAPPER.getObfClassNameIfExists(pt.owner.replace(".", "/"));
                patchList.put(obfOwner, patch);
            }
        }
    }

    public void ApplyPatches(Instrumentation inst) {
        for (IPatch patch : patchList.values()) {
            try {
                if (!patch.doNotInit) {
                    if (patch.deobfNameToPatch != null) {
                        inst.retransformClasses(AsmUtil.findClass(patch.obfName));
                    } else {
                        for (PatchTarget pt : patch.getApplicableTargets()) {
                            String obfOwner = AgentMain.MAPPER.getObfClassNameIfExists(pt.owner.replace(".", "/"));
                            inst.retransformClasses(AsmUtil.findClass(obfOwner));
                        }
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain pd, byte[] byteCode) {
        if (className != null && patchList.containsKey(className)) {
            try {
                Logger.log("Applying patch for class:", className, "(" + AgentMain.MAPPER.getDeObfClassName(className) + ")");
                return patchList.get(className).TransformClass(className, byteCode);
            } catch (Exception ex) {
                Logger.log("ERROR: Applying patch failed:", className);
                ex.printStackTrace();
            }
        }
        return byteCode;
    }

}
