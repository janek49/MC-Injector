package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class Pre17Mapper extends Mapper {

    public Pre17Mapper(String mcpPath) {
        super(mcpPath);
    }

    public String redirectClassName(String in) {
        if (in.startsWith("net/minecraft/") && !in.startsWith("net/minecraft/src/")) {
            String[] split = in.split("/");
            String className = split[split.length - 1];
            return "net/minecraft/src/" + className;
        }
        return in;
    }

    public String redirectPropertyName(String in) {
        if (in.startsWith("net/minecraft/") && !in.startsWith("net/minecraft/src/")) {
            String[] split = in.split("/");
            String className = split[split.length - 2];
            String memberName = split[split.length - 1];
            return "net/minecraft/src/" + className + "/" + memberName;
        }
        return in;
    }

    public String redirectDescriptor(String in) {
        StringBuilder output = new StringBuilder();
        char[] chars = in.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char chr = chars[i];
            if (chr == 'L') {
                int skip = 0;
                String literal = "";
                for (int pos = ++i; ; pos++) {
                    char chAt = chars[pos];
                    if (chAt == ';')
                        break;
                    literal += chAt;
                    skip++;
                }
                i += skip;
              //  Logger.log(literal);
                output.append("L");
                output.append(redirectClassName(literal));
                output.append(";");
            } else {
                output.append(chr);
            }
        }
       // Logger.log(output.toString());
        return output.toString();
    }


    public String getObfClassName(String deobfClassName) {
        deobfClassName = redirectClassName(deobfClassName);
        String className = SeargeMap.get("CL:" + deobfClassName);
      //  Logger.log("Mapping class name: " + deobfClassName + " -> " + className);
        return className;
    }


    public String[] getObfMethodName(String deobfMethodName, String deobfMethodDescriptor) {
        deobfMethodName = redirectPropertyName(deobfMethodName);
        deobfMethodDescriptor = redirectDescriptor(deobfMethodDescriptor);

        String res = SeargeMap.get("MD:" + deobfMethodName + ":" + deobfMethodDescriptor);
        if (res == null)
            return null;
        String[] params = res.split(":");

      //  Logger.log("Mapping method name: " + deobfMethodName + " " + deobfMethodDescriptor + " -> "
       //         + params[0] + " " + params[1]);

        return params;
    }

    public String[] getObfMethodNameWithoutClass(String deobfMethodName, String deobfMethodDescriptor) {
        deobfMethodName = redirectPropertyName(deobfMethodName);
        deobfMethodDescriptor = redirectDescriptor(deobfMethodDescriptor);

        String res = SeargeMap.get("MD:" + deobfMethodName + ":" + deobfMethodDescriptor);
        if (res == null)
            return null;
        String[] params = res.split(":");
        params[0] = Util.getLastPartOfArray(params[0].split("/"));

       // Logger.log("Mapping simple method name: " + deobfMethodName + " " + deobfMethodDescriptor + " -> "
       //         + params[0] + " " + params[1]);

        return params;

    }

    public String getObfFieldName(String deobfFieldName) {
        deobfFieldName = redirectPropertyName(deobfFieldName);

        String fieldName = SeargeMap.get("FD:" + deobfFieldName);

     //   Logger.log("Mapping field name: " + deobfFieldName + " -> " + fieldName);
        return fieldName;
    }


}
