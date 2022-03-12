package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Mapper {
    public static String MCP_PATH = "C:\\Users\\Jan\\Desktop\\mcp918\\conf";

    public HashMap<String, String> SeargeMap;

    public HashMap<String, String> DeobfFieldNames = new HashMap<>();
    public HashMap<String, String> DeobfMethodNames = new HashMap<>();

    public Mapper() {

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
            StringBuffer sb = new StringBuffer();
            String line;

            SeargeMap = new HashMap<>();
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
            StringBuffer sb = new StringBuffer();
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
        String className = SeargeMap.get("CL:" + deobfClassName);
        Logger.log("Mapping class name: " + deobfClassName + " -> " + className);
        return className;
    }

    public String getDeObfClassName(String obfClassName) {
        String className = null;
        for (String key : SeargeMap.keySet()) {
            if (key.startsWith("CL:") && SeargeMap.get(key).equals(obfClassName)) {
                className = key.split(":")[1];
            }
        }
        Logger.log("Reverse-mapping class name: " + obfClassName + " -> " + className);
        return className;
    }

    public String[] getObfMethodName(String deobfMethodName, String deobfMethodDescriptor) {
        String res = SeargeMap.get("MD:" + deobfMethodName + ":" + deobfMethodDescriptor);
        if (res == null)
            return null;
        String[] params = res.split(":");
        Logger.log("Mapping method name: " + deobfMethodName + " " + deobfMethodDescriptor + " -> "
                + params[0] + " " + params[1]);
        return params;

    }

    public String[] getObfMethodNameWithoutClass(String deobfMethodName, String deobfMethodDescriptor) {
        String res = SeargeMap.get("MD:" + deobfMethodName + ":" + deobfMethodDescriptor);
        if (res == null)
            return null;
        String[] params = res.split(":");
        params[0] = Util.getLastPartOfArray(params[0].split("/"));

        Logger.log("Mapping simple method name: " + deobfMethodName + " " + deobfMethodDescriptor + " -> "
                + params[0] + " " + params[1]);
        return params;

    }

    public String getObfFieldName(String deobfFieldName) {
        String fieldName = SeargeMap.get("FD:" + deobfFieldName);
        Logger.log("Mapping field name: " + deobfFieldName + " -> " + fieldName);
        return fieldName;
    }

    public String[] findMethodMappingObfClassDeobfMethod(String obfOwner, String deobfName, String deobfDescriptor) {
        String deobfOwner = getDeObfClassName(obfOwner);
        return getObfMethodName(deobfOwner + "/" + deobfName, deobfDescriptor);
    }

    public static String GetClassNameFromFullMethod(String fullMd){
        String[] obfNameParted = fullMd.split("/");
        String[] newOwnerParted = new String[obfNameParted.length - 1];
        System.arraycopy(obfNameParted, 0, newOwnerParted, 0, obfNameParted.length - 1);

        return String.join("/", newOwnerParted);
    }
}