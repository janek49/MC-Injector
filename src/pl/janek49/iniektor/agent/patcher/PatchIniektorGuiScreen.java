package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import pl.janek49.iniektor.agent.AgentMain;

public class PatchIniektorGuiScreen extends IPatch{

    public PatchIniektorGuiScreen() {
        super("pl.janek49.iniektor.client.gui.IniektorGuiScreen");
        doNotInit = true;
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {

        String newParent = "net/minecraft/client/gui/GuiScreen";
        CtClass parent = pool.get(AgentMain.MAPPER.getObfClassName(newParent));

        ctClass.setSuperclass(parent);

        return ctClass.toBytecode();
    }
}
