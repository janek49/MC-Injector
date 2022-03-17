package pl.janek49.iniektor.agent;

import pl.janek49.iniektor.Util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

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

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
