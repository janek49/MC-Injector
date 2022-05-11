package pl.janek49.iniektor.agent.asm;

import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.org.objectweb.asm.ClassVisitor;
import pl.janek49.org.objectweb.asm.MethodVisitor;

import java.util.HashMap;

public class MethodRenamerByAnnotation extends ClassVisitor {

    public HashMap<String, TransformerAnnotationAdapter.MappingEntry> mappings = new HashMap<>();

    public MethodRenamerByAnnotation(HashMap<String, TransformerAnnotationAdapter.MappingEntry> mappings, int i, ClassVisitor output) {
        super(i, output);
        this.mappings = mappings;
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        String key = name + " " + desc;
        if (mappings.containsKey(key)) {
            TransformerAnnotationAdapter.MappingEntry map = mappings.get(key);
            if (map.target.targetVersion == AgentMain.MCP_VERSION || map.target.targetVersion == Version.DEFAULT) {

                String[] obfName = AgentMain.MAPPER.getObfMethodNameWithoutClass(map.target.targetMethodName, map.target.targetMethodDesc);

                if (obfName != null) {
                    Logger.log("MethodRenamerByAnnotation: (" + map.target.targetVersion + ")", name, desc, "->", obfName[0], obfName[1]);
                    name = obfName[0];
                } else {
                    Logger.err("MethodRenamerByAnnotation: (" + map.target.targetVersion + ") No mapping for method:", name, desc);
                }
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
