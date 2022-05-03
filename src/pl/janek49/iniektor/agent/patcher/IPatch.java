package pl.janek49.iniektor.agent.patcher;

import javassist.ByteArrayClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.Reflector;

import java.util.ArrayList;
import java.util.List;

public abstract class IPatch {

    public boolean doNotInit = false;
    public String deobfNameToPatch;
    public String obfName;

    public IPatch(String target) {
        deobfNameToPatch = target;
    }

    public IPatch() {

    }

    //Adds the PatchTarget if list is empty, and target applies to current version
    //This allows for easy addition from latest to oldest MC version without complicated VersionRanged
    public void addFirst(PatchTarget target) {
        if(patchTargets.isEmpty() && target.appliesTo(AgentMain.MCP_VERSION))
            patchTargets.add(target);
    }

    public List<PatchTarget> patchTargets = new ArrayList<>();

    public List<PatchTarget> getApplicableTargets() {
        List<PatchTarget> targets = new ArrayList<>();
        for (PatchTarget pt : patchTargets) {
            if (pt.appliesTo(AgentMain.MCP_VERSION)) {
                targets.add(pt);
            }
        }
        return targets;
    }

    public PatchTarget getFirstPatchTarget(){
        return getApplicableTargets().get(0);
    }

    public abstract byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception;

    public byte[] TransformClass(String className, byte[] byteCode) throws Exception {
        String dotclassName = className.replace("/", ".");

        ClassPool pool = ClassPool.getDefault();
        ClassPath cp = new ByteArrayClassPath(dotclassName, byteCode);

        //AsmUtil.applyClassPath(pool);
        pool.insertClassPath(cp);

        CtClass ctClass = pool.get(dotclassName);
        //pool.removeClassPath(cp);

        ctClass.defrost();

        byte[] bytes = PatchClassImpl(className, pool, ctClass, byteCode);

        return bytes;
    }

}
