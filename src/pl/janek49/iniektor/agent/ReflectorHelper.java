package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.agent.asm.AsmUtil;

import java.lang.reflect.Field;

public class ReflectorHelper {
    public static void TransformNames() {
        try {
            Class reflector = AsmUtil.findClass("pl.janek49.iniektor.client.hook.Reflector");

            Logger.log("Reflector SetField MCP_PATH");
            Field mapperField = reflector.getDeclaredField("MCP_PATH");
            mapperField.set(null, AgentMain.MAPPER.MCP_PATH);

            Logger.log("Reflector SetField MCP_VERSION");
            Field versionField = reflector.getDeclaredField("MCP_VERSION_STRING");
            versionField.set(null, AgentMain.MCP_VERSION.toString());

            Logger.log("Reflector SetField IS_FORGE");
            Field forgeField = reflector.getDeclaredField("IS_FORGE");
            forgeField.set(null, AgentMain.IS_FORGE);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
