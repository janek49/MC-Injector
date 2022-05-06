package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.patcher.PatchTarget;
import pl.janek49.iniektor.api.FieldDefinition;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Mapper {


    public static class MethodMatch {
        public String deobfOwner, obfOwner;
        public String deobfName, obfName;
        public String deobfDesc, obfDesc;

        @Override
        public String toString() {
            return "MethodMatch{" +
                    "deobfOwner='" + deobfOwner + '\'' +
                    ", obfOwner='" + obfOwner + '\'' +
                    ", deobfName='" + deobfName + '\'' +
                    ", obfName='" + obfName + '\'' +
                    ", deobfDesc='" + deobfDesc + '\'' +
                    ", obfDesc='" + obfDesc + '\'' +
                    '}';
        }

        public String getObfuscatedOwnerDotted() {
            return obfOwner.replace("/", ".");
        }
    }

    public static class FieldMatch {
        public String deobfOwner, obfOwner;
        public String deobfName, obfName;

        @Override
        public String toString() {
            return "FieldMatch{" +
                    "deobfOwner='" + deobfOwner + '\'' +
                    ", obfOwner='" + obfOwner + '\'' +
                    ", deobfName='" + deobfName + '\'' +
                    ", obfName='" + obfName + '\'' +
                    '}';
        }

        public String getObfuscatedOwnerDotted() {
            return obfOwner.replace("/", ".");
        }
    }


    public String MCP_PATH;

    public HashMap<String, String> SeargeMap;

    public HashMap<String, String> DeobfFieldNames = new HashMap<String, String>();
    public HashMap<String, String> DeobfMethodNames = new HashMap<String, String>();

    public Mapper(String mcpPath) {
        MCP_PATH = mcpPath;
    }

    public void init() {
        readMcpCsvFile("fields", DeobfFieldNames);
        readMcpCsvFile("methods", DeobfMethodNames);
        readSeargeDefinitions();
    }

    public void readSeargeDefinitions() {
        try {
            File file = new File(MCP_PATH + File.separator + "joined.srg");
            Logger.log("Reading Searge definitions from MCP: " + file.getAbsolutePath());
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            SeargeMap = new HashMap<String, String>();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals("CL:")) {
                    SeargeMap.put("CL:" + parts[2], parts[1]);
                } else if (parts[0].equals("FD:")) {

                    String[] fd = parts[2].split("/");
                    String fieldName = fd[fd.length - 1];

                    if (fieldName.startsWith("field_") && DeobfFieldNames.containsKey(fieldName)) {
                        fd[fd.length - 1] = DeobfFieldNames.get(fieldName);
                        String newFieldName = String.join("/", fd);
                        parts[2] = newFieldName;
                    }

                    SeargeMap.put("FD:" + parts[2], parts[1]);

                } else if (parts[0].equals("MD:")) {

                    String[] md = parts[3].split("/");
                    String funcName = md[md.length - 1];

                    if (funcName.startsWith("func_") && DeobfMethodNames.containsKey(funcName)) {
                        md[md.length - 1] = DeobfMethodNames.get(funcName);
                        String newFuncName = String.join("/", md);
                        parts[3] = newFuncName;
                    }
                    SeargeMap.put("MD:" + parts[3] + ":" + parts[4], parts[1] + ":" + parts[2]);
                    //  Logger.err("MD:" + parts[3] + ":" + parts[4], parts[1] + ":" + parts[2]);
                }
            }
            fr.close();
            Logger.log("Read " + SeargeMap.size() + " name definitions.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void readMcpCsvFile(String name, HashMap<String, String> target) {
        try {
            File file = new File(MCP_PATH + File.separator + name + ".csv");
            Logger.log("Reading CSV file: " + file.getAbsolutePath());
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;
            target.clear();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length > 1) {
                    target.put(parts[0], parts[1]);
                }
            }
            fr.close();
            Logger.log("Read " + target.size() + " CSV definitions.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getObfClassName(String deobfClassName) {
        deobfClassName = deobfClassName.replace(".", "/");
        String className = SeargeMap.get("CL:" + deobfClassName);
        //Logger.log("Mapping class name: " + deobfClassName + " -> " + className);
        return className;
    }

    public String getObfClassNameIfExists(String deobfClassName) {
        String obfName = getObfClassName(deobfClassName);
        return obfName == null ? deobfClassName : obfName;
    }

    public String getDeObfClassName(String obfClassName) {


        String className = null;
        for (String key : SeargeMap.keySet()) {
            if (key.startsWith("CL:") && SeargeMap.get(key).equals(obfClassName)) {
                className = key.split(":")[1];
            }
        }
        //  Logger.log("Reverse-mapping class name: " + obfClassName + " -> " + className);
        return className;
    }

    public String[] getObfMethodName(String deobfMethodName, String deobfMethodDescriptor) {
        String res = SeargeMap.get("MD:" + deobfMethodName + ":" + deobfMethodDescriptor);
        if (res == null)
            return null;
        String[] params = res.split(":");
        // Logger.log("Mapping method name: " + deobfMethodName + " " + deobfMethodDescriptor + " -> "
        //         + params[0] + " " + params[1]);
        return params;

    }

    public String[] getObfMethodNameWithoutClass(String deobfMethodName, String deobfMethodDescriptor) {
        String res = SeargeMap.get("MD:" + deobfMethodName + ":" + deobfMethodDescriptor);
        if (res == null)
            return null;
        String[] params = res.split(":");
        params[0] = Util.getLastPartOfArray(params[0].split("/"));

        //  Logger.log("Mapping simple method name: " + deobfMethodName + " " + deobfMethodDescriptor + " -> "
        //         + params[0] + " " + params[1]);
        return params;

    }

    public String getObfFieldName(String deobfFieldName) {
        String fieldName = SeargeMap.get("FD:" + deobfFieldName);
        //  Logger.log("Mapping field name: " + deobfFieldName + " -> " + fieldName);
        return fieldName;
    }

    public String getShortObfFieldName(String deobfFieldName) {
        return Util.getLastPartOfArray(getObfFieldName(deobfFieldName).split("/"));
    }

    public String[] findMethodMappingObfClassDeobfMethod(String obfOwner, String deobfName, String deobfDescriptor) {
        String deobfOwner = getDeObfClassName(obfOwner);
        return getObfMethodName(deobfOwner + "/" + deobfName, deobfDescriptor);
    }

    public static String GetClassNameFromFullMethod(String fullMd) {
        String[] obfNameParted = fullMd.split("/");
        String[] newOwnerParted = new String[obfNameParted.length - 1];
        System.arraycopy(obfNameParted, 0, newOwnerParted, 0, obfNameParted.length - 1);

        return String.join("/", newOwnerParted);
    }

    public static String[] GetOwnerAndField(String full) {
        String[] obfNameParted = full.split("/");
        String[] newOwnerParted = new String[obfNameParted.length - 1];
        System.arraycopy(obfNameParted, 0, newOwnerParted, 0, obfNameParted.length - 1);
        String owner = String.join("/", newOwnerParted);
        String field = Util.getLastPartOfArray(obfNameParted);
        return new String[]{owner, field};
    }

    public MethodMatch findMethodMapping(String deobfFullName, String deobfDesc) {
        String[] rawMD = getObfMethodName(deobfFullName, deobfDesc);
        if (rawMD == null)
            return null;

        MethodMatch mm = new MethodMatch();
        mm.deobfDesc = deobfDesc;

        String[] ownerSplit = GetOwnerAndField(deobfFullName);
        mm.deobfOwner = ownerSplit[0];
        mm.deobfName = ownerSplit[1];

        mm.obfDesc = rawMD[1];

        String[] obfOwnerSplit = GetOwnerAndField(rawMD[0]);
        mm.obfOwner = obfOwnerSplit[0];
        mm.obfName = obfOwnerSplit[1];

        return mm;
    }

    public MethodMatch findMethodMapping(PatchTarget pt) {
        return findMethodMapping(pt.owner + "/" + pt.methodName, pt.descriptor);
    }

    public FieldMatch findFieldMapping(String deobfFullName) {
        String rawFD = getObfFieldName(deobfFullName);
        if (rawFD == null)
            return null;

        FieldMatch fm = new FieldMatch();
        String[] deObf = GetOwnerAndField(deobfFullName);
        String[] obf = GetOwnerAndField(rawFD);
        fm.deobfOwner = deObf[0];
        fm.deobfName = deObf[1];
        fm.obfOwner = obf[0];
        fm.obfName = obf[1];
        return fm;
    }
}
