package pl.janek49.iniektor.agent.patcher;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Version;

public class PatchIniektorGuiScreen extends IPatch{

    public PatchIniektorGuiScreen() {
        super("pl.janek49.iniektor.client.gui.IniektorGuiScreen");
        doNotInit = true;
    }

    @Override
    public byte[] PatchClassImpl(String obfClassName, ClassPool pool, CtClass ctClass, byte[] byteCode) throws Exception {

        String newParent = "net/minecraft/client/gui/GuiScreen";

        boolean isMc114 = AgentMain.MCP_VERSION.ordinal()>= Version.MC1_14_4.ordinal();

        if(isMc114)
            newParent = "net/minecraft/client/gui/screens/Screen";

        CtClass parent = pool.get(AgentMain.MAPPER.getObfClassName(newParent));

        ctClass.setSuperclass(parent);

        if(isMc114){
            String textComponentClass = AgentMain.MAPPER.getObfClassName("net/minecraft/network/chat/TextComponent").replace("/", ".");

            CtConstructor ctConstructor = ctClass.getConstructor("()V");
            ctConstructor.setBody(String.format("{ super( new %s(\"\") ); initClass(); }", textComponentClass));
        }


        return ctClass.toBytecode();
    }
}
