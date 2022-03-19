package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class ForgeMapper extends Mapper {

    public ForgeMapper(String mcpPath) {
        super(mcpPath);
    }

    @Override
    public void init() {
        readMcpCsvFile("fields", DeobfFieldNames);
        readMcpCsvFile("methods", DeobfMethodNames);

        readSeargeDefinitions();
    }

    @Override
    public void readSeargeDefinitions() {
        try {
            File file = new File(MCP_PATH + File.separator + "joined.srg");
            Logger.log("Reading Searge definitions from MCP: " + file.getAbsolutePath());
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            StringBuffer sb = new StringBuffer();
            String line;

            SeargeMap = new HashMap<String, String>();
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");

                if (parts[0].equals("FD:")) {

                    String[] fd = parts[2].split("/");
                    String fieldName = fd[fd.length - 1];

                    if (fieldName.startsWith("field_") && DeobfFieldNames.containsKey(fieldName)) {
                        fd[fd.length - 1] = DeobfFieldNames.get(fieldName);
                        String newFieldName = String.join("/", fd);
                        SeargeMap.put("FD:" + newFieldName, parts[2]);
                    } else {
                        SeargeMap.put("FD:" + parts[2], parts[2]);
                    }


                } else if (parts[0].equals("MD:")) {

                    String[] md = parts[3].split("/");
                    String funcName = md[md.length - 1];

                    if (funcName.startsWith("func_") && DeobfMethodNames.containsKey(funcName)) {
                        md[md.length - 1] = DeobfMethodNames.get(funcName);
                        String newFuncName = String.join("/", md);

                        SeargeMap.put("MD:" + newFuncName + ":" + parts[4], parts[3] + ":" + parts[4]);
                    } else {
                        SeargeMap.put("MD:" + parts[3] + ":" + parts[4], parts[3] + ":" + parts[4]);
                    }


                }
            }
            fr.close();
            Logger.log("Read " + SeargeMap.size() + " name definitions.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String getObfClassName(String deobfClassName) {
        return deobfClassName;
    }

    public String getDeObfClassName(String obfClassName) {
        return obfClassName;
    }

    public String[] getObfMethodName(String deobfMethodName, String deobfMethodDescriptor) {
        String res = SeargeMap.get("MD:" + deobfMethodName + ":" + deobfMethodDescriptor);
        if (res == null)
            return null;
        String[] params = res.split(":");
        return params;

    }

    public String[] getObfMethodNameWithoutClass(String deobfMethodName, String deobfMethodDescriptor) {
        String res = SeargeMap.get("MD:" + deobfMethodName + ":" + deobfMethodDescriptor);
        if (res == null)
            return null;
        String[] params = res.split(":");
        params[0] = Util.getLastPartOfArray(params[0].split("/"));

        return params;

    }

    public String getObfFieldName(String deobfFieldName) {
        String fieldName = SeargeMap.get("FD:" + deobfFieldName);
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
}
