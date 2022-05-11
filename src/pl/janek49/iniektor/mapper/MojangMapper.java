package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class MojangMapper extends SeargeMapper {

    public MojangMapper(String mcpPath) {
        super(mcpPath);
    }


    @Override
    public void init() {
        //populate class names
        readMojangMap(1);
        //translate field & method names
        readMojangMap(2);
    }

    public void readMojangMap(int stage) {
        try {
            File file = new File(MCP_PATH + File.separator + "client.txt");
            Logger.log("Reading Mojang mappings (stage " + stage + "): " + file.getAbsolutePath());
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            MojangClassDef currentClass = null;

            while ((line = br.readLine()) != null) {
                if (line.startsWith("#"))
                    continue;

                if (stage == 1) {
                    //członek klasy na tym etapie nas nie interesuje
                    if (line.startsWith(" "))
                        continue;

                    MojangClassDef classDef = parseMojangClassDef(line);
                   // SeargeMap.put("CL:" + classDef.deobfName, classDef.obfName);
                    // Logger.log(classDef.obfName, "->", classDef.deobfName);
                    classMatches.add(classDef);
                } else if (stage == 2) {

                    //ustawiamy aktualną klasę
                    if (!line.startsWith(" ")) {
                        currentClass = parseMojangClassDef(line);
                        // Logger.log();
                        //  Logger.log("CLASS:", currentClass.obfName, "->", currentClass.deobfName);
                        continue;
                    }

                    if (line.contains("(")) {//is method
                        //pomin konstruktory
                        if (line.contains("<init>") || line.contains("<clinit>"))
                            continue;

                        MojangMethodDef methodDef = parseMojangMethodDef(line);

                        methodDef.deobfOwner = currentClass.deobfName;
                        methodDef.obfOwner = currentClass.obfName;

                        methodDef.deobfDesc = generateBytecodeMethodDesc(methodDef.returnType, methodDef.deobfName, methodDef.paramTypes);
                        methodDef.obfDesc = generateBytecodeMethodDesc(getObfClassNameIfExists(methodDef.returnType), methodDef.obfName, obfuscateList(methodDef.paramTypes));

                        methodMatches.add(methodDef);
                       // Logger.err(methodDef);

                        //MD:net/minecraft/entity/item/EntityFireworkRocket/getBrightness:(F)F zz/e:(F)F



                    } else {
                        MojangFieldDef fieldDef = parseMojangFieldDef(line);

                        fieldDef.obfOwner = currentClass.obfName;
                        fieldDef.deobfOwner = currentClass.deobfName;

                        fieldMatches.add(fieldDef);
                    }

                }

            }
            fr.close();
            Logger.log("Read " + countEntries() + " name definitions.");
        } catch (IOException e) {
            Logger.ex(e);
        }
    }

    public static  class MojangClassDef extends SeargeMapper.ClassMatch{
        public MojangClassDef(String deobfName, String obfName) {
            super(deobfName, obfName);
        }
    }

    private MojangClassDef parseMojangClassDef(String input) {
        //PRZYKŁAD:
        //com.mojang.blaze3d.audio.Channel -> ctp:
        String[] split = input.split(" -> ");
        String deobfName = split[0].replace(".", "/");
        String obfName = split[1].substring(0, split[1].length() - 1).replace(".", "/");
        MojangClassDef d = new MojangClassDef(deobfName, obfName);
        return d;
    }

    public static class MojangFieldDef extends SeargeMapper.FieldMatch {
        String rawType;
    }

    private MojangFieldDef parseMojangFieldDef(String input) {
        //    net.minecraft.realms.RealmsEditBox nameEdit -> e
        String[] split = input.split(" -> ");
        String[] p0Split = split[0].trim().split(" ");
        MojangFieldDef d = new MojangFieldDef();
        d.rawType = p0Split[0].trim().replace(".", "/");
        d.deobfName = p0Split[1].trim();
        d.obfName = split[1].trim();
        return d;
    }


    public static  class MojangMethodDef extends SeargeMapper.MethodMatch{
        String returnType, deobfBytecodeDesc;
        List<String> paramTypes;
    }

    private MojangMethodDef parseMojangMethodDef(String input) {
        //    77:78:void drawIcon(int,int,net.minecraft.client.renderer.entity.ItemRenderer) -> a
        MojangMethodDef d = new MojangMethodDef();
        String[] split = input.trim().split(" -> ");
        d.obfName = split[1].trim();
        String[] left = split[0].trim().split(" ");
        d.returnType = left[0].substring(left[0].lastIndexOf(':') + 1).trim().replace(".", "/");

        String[] descriptor = left[1].split(Pattern.quote("("));
        d.deobfName = descriptor[0];
        d.paramTypes = new ArrayList<>(Arrays.asList(descriptor[1].replace(".", "/").substring(0, descriptor[1].length() - 1).split(Pattern.quote(","))));
        d.paramTypes.removeAll(Arrays.asList("", null));
        return d;
    }

    public List<String> obfuscateList(List<String> input) {
        ArrayList<String> list = new ArrayList<>();
        for (String className : input) {
            list.add(getObfClassNameIfExists(className));
        }
        return list;
    }

    private String generateBytecodeMethodDesc(String returnType, String name, List<String> params) {
        StringBuilder sb = new StringBuilder();

        sb.append("(");

        for (String param : params)
            sb.append(javaTypeToBytecode(param));

        sb.append(")");

        sb.append(javaTypeToBytecode(returnType));

        return sb.toString();
    }

    public String javaTypeToBytecode(String type) {
        StringBuilder sb = new StringBuilder();
        int arrDepth = Util.countStringinString(type, "[]");
        String cleanType = type.split(Pattern.quote("["))[0];

        sb.append(Util.repeatString("[", arrDepth));

        if (isPrimitive(cleanType))
            sb.append(primitiveToBytecode(cleanType));
        else {
            sb.append("L");
            sb.append(cleanType);
            sb.append(";");
        }

        return sb.toString();
    }

    public String primitiveToBytecode(String input) {
        StringBuilder sb = new StringBuilder();
        switch (input) {
            case "int":
                sb.append("I");
                break;
            case "float":
                sb.append("F");
                break;
            case "double":
                sb.append("D");
                break;
            case "long":
                sb.append("J");
                break;
            case "short":
                sb.append("H");
                break;
            case "byte":
                sb.append("B");
                break;
            case "boolean":
                sb.append("Z");
                break;
            case "char":
                sb.append("C");
                break;
            case "void":
                sb.append("V");
                break;
            default:
                throw new RuntimeException("Invalid primitive type:" + input);
        }
        return sb.toString();
    }

    public boolean isPrimitive(String className) {
        return ";int;float;double;long;short;byte;boolean;char;void;".contains(";" + className + ";");
    }
}
