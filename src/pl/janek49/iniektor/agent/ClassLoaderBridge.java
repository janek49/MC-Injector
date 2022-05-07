package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.agent.asm.AsmUtil;
import pl.janek49.iniektor.api.Reflector;
import sun.management.Agent;

import java.lang.reflect.Field;

public class ClassLoaderBridge {
    public static void SetReflectorFields() {
        try {
            //znajdź klasę w classloaderze instancji gry
            Class reflector = AsmUtil.findClass(Reflector.class.getName());

            //ponieważ my jesteśmy na innym classloaderze niż instancja gry, Reflector != Reflector i potrzeba użyć refleksji
            Logger.log("Reflector SetField MCP_PATH");
            Field mapperField = reflector.getDeclaredField("MCP_PATH");
            mapperField.set(null, AgentMain.MAPPER.MCP_PATH);

            Logger.log("Reflector SetField MCP_VERSION");
            Field versionField = reflector.getDeclaredField("MCP_VERSION_STRING");
            versionField.set(null, AgentMain.MCP_VERSION.toString());

            Logger.log("Reflector SetField IS_FORGE");
            Field forgeField = reflector.getDeclaredField("IS_FORGE");
            forgeField.set(null, AgentMain.IS_FORGE);

            Class agentMain = AsmUtil.findClass(AgentMain.class.getName());
            Logger.log("AgentMain SetField guiWindow");
            Field guiWindowField = agentMain.getDeclaredField("guiWindow");
            guiWindowField.set(null, AgentMain.guiWindow);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
