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


            List<Field> fieldFields = new ArrayList<>();
            List<Field> methodFields = new ArrayList<>();

            for (Field fd : reflector.getDeclaredFields()) {
                if (fd.getName().startsWith("FIELD_")) {
                    fieldFields.add(fd);
                } else if (fd.getName().startsWith("METHOD_")) {
                    methodFields.add(fd);
                }
            }

            for (Field fieldField : fieldFields) {
                String deobfName = (String) fieldField.get(null);
                Logger.log("Reflector ResolveField", deobfName);
                String obfName = AgentMain.MAPPER.getObfFieldName(deobfName);
                fieldField.set(null, Util.getLastPartOfArray(obfName.split("/")));
            }

            for (Field methodField : methodFields) {
                String[] deobfName = ((String) methodField.get(null)).split(" ");
                Logger.log("Reflector ResolveMethod", deobfName);
                String[] obfName = AgentMain.MAPPER.getObfMethodName(deobfName[0], deobfName[1]);
                methodField.set(null, String.join(" ", obfName));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
