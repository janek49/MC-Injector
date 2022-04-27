package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;
import java.util.regex.Pattern;

public class MojangMapper extends Mapper {

    public MojangMapper(String mcpPath) {
        super(mcpPath);
    }


    @Override
    public void init() {
        SeargeMap = new HashMap<String, String>();
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
                    SeargeMap.put("CL:" + classDef.deobfName, classDef.obfName);
                    // Logger.log(classDef.obfName, "->", classDef.deobfName);

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

                        String deobfDesc = currentClass.deobfName + "/" + generateBytecodeMethodDesc(methodDef.returnType, methodDef.deobfName, methodDef.paramTypes, true);

                        String obfDesc = currentClass.obfName + "/" +
                                generateBytecodeMethodDesc(getObfClassNameIfExists(methodDef.returnType), methodDef.obfName, obfuscateList(methodDef.paramTypes), true);

                        // Logger.log("MD:" + deobfDesc, obfDesc);

                        //MD:net/minecraft/entity/item/EntityFireworkRocket/getBrightness:(F)F zz/e:(F)F
                        SeargeMap.put("MD:" + deobfDesc, obfDesc);

                    } else {
                        MojangFieldDef fieldDef = parseMojangFieldDef(line);

                        // Logger.log("FD:" + currentClass.deobfName + "/" + fieldDef.deobfName, currentClass.obfName + "/" + fieldDef.obfName);
                        SeargeMap.put("FD:" + currentClass.deobfName + "/" + fieldDef.deobfName, currentClass.obfName + "/" + fieldDef.obfName);
                    }

                }

            }
            fr.close();
            Logger.log("Read " + SeargeMap.size() + " name definitions.");
        } catch (IOException e) {
            e.printStackTrace();
        }


        File dump = new File(MCP_PATH + File.separatorChar + "dump.txt");
        if (!dump.exists())
            try {
                List<String> mLines = new ArrayList<String>();
                SeargeMap.forEach((key, value) -> mLines.add(key + " " + value));
                Collections.sort(mLines);
                Files.write(dump.toPath(), mLines, StandardCharsets.UTF_8);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
    }


    class MojangClassDef {
        String obfName, deobfName;
    }

    private MojangClassDef parseMojangClassDef(String input) {
        //PRZYKŁAD:
        //com.mojang.blaze3d.audio.Channel -> ctp:
        String[] split = input.split(" -> ");
        String deobfName = split[0].replace(".", "/");
        String obfName = split[1].substring(0, split[1].length() - 1).replace(".", "/");
        MojangClassDef d = new MojangClassDef();
        d.deobfName = deobfName;
        d.obfName = obfName;
        return d;
    }

    class MojangFieldDef {
        String rawType, deobfName, obfName;
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


    class MojangMethodDef {
        String returnType, deobfName, obfName, deobfBytecodeDesc;
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

    private String generateBytecodeMethodDesc(String returnType, String name, List<String> params, boolean insertColon) {
        StringBuilder sb = new StringBuilder();

        sb.append(name);
        if (insertColon)
            sb.append(":");
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
