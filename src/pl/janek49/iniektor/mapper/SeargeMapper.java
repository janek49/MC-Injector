package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.patcher.PatchTarget;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class SeargeMapper {


    public static class MethodMatch {
        public String deobfOwner, obfOwner;
        public String deobfName, obfName;
        public String deobfDesc, obfDesc;

//        @Override
//        public String toString() {
//            return deobfOwner + "/" + deobfName + deobfDesc + " -> " + obfOwner + "/" + obfName + obfDesc;
//        }

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

//        @Override
//        public String toString() {
//            return deobfOwner + "/" + deobfName + " -> " + obfOwner + "/" + obfName;
//        }


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

    public static class ClassMatch {

        public ClassMatch(String deobfName, String obfName) {
            this.deobfName = deobfName;
            this.obfName = obfName;
        }

        public String deobfName, obfName;

        @Override
        public String toString() {
            return "ClassMatch{" +
                    "deobfName='" + deobfName + '\'' +
                    ", obfName='" + obfName + '\'' +
                    '}';
        }

        public String getObfuscatedNameDotted() {
            return obfName.replace("/", ".");
        }
    }

    public List<ClassMatch> classMatches = new ArrayList<>();
    public List<FieldMatch> fieldMatches = new ArrayList<>();
    public List<MethodMatch> methodMatches = new ArrayList<>();

    public String MCP_PATH;

    public SeargeMapper(String mcpPath) {
        MCP_PATH = mcpPath;
    }

    public void init() {
        readSeargeDefinitions();
    }

    public void readSeargeDefinitions() {
        readSeargeDefinitions("joined.srg");
    }

    public void readSeargeDefinitions(String filename) {
        try {
            File file = new File(MCP_PATH + File.separator + filename);
            Logger.log("Reading Searge definitions from MCP: " + file.getAbsolutePath());
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);
            String line;

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(" ");
                if (parts[0].equals("CL:")) {

                    classMatches.add(new ClassMatch(parts[2], parts[1]));

                } else if (parts[0].equals("FD:")) {
                    FieldMatch fm = new FieldMatch();

                    //split part 2, the intermediate name
                    String[] deobfOaf = GetOwnerAndField(parts[2]);

                    fm.deobfOwner = deobfOaf[0];
                    fm.deobfName = deobfOaf[1];

                    //split part 1, the obfuscated owner/name
                    String[] obfOaf = GetOwnerAndField(parts[1]);
                    fm.obfOwner = obfOaf[0];
                    fm.obfName = obfOaf[1];

                    fieldMatches.add(fm);

                } else if (parts[0].equals("MD:")) {
                    MethodMatch mm = new MethodMatch();

                    //split part 1, the obfuscated owner/name
                    String[] obfOaf = GetOwnerAndField(parts[1]);
                    mm.obfOwner = obfOaf[0];
                    mm.obfName = obfOaf[1];

                    mm.obfDesc = parts[2];
                    mm.deobfDesc = parts[4];

                    //split part 3, the deobfuscated owner/name
                    String[] deobfOaf = GetOwnerAndField(parts[3]);
                    mm.deobfOwner = deobfOaf[0];
                    mm.deobfName = deobfOaf[1];

                    methodMatches.add(mm);
                }
            }
            fr.close();
            Logger.log("Read " + countEntries() + " name definitions.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int countEntries() {
        return classMatches.size() + methodMatches.size() + fieldMatches.size();
    }


    public String getObfClassName(String deobfClassName) {
        deobfClassName = deobfClassName.replace(".", "/");

        for (ClassMatch cm : classMatches) {
            if (Objects.equals(cm.deobfName, deobfClassName))
                return cm.obfName;
        }
        return null;
    }

    public String getObfClassNameIfExists(String deobfClassName) {
        String obfName = getObfClassName(deobfClassName);
        return obfName == null ? deobfClassName : obfName;
    }

    public String getDeObfClassName(String obfClassName) {
        obfClassName = obfClassName.replace(".", "/");

        for (ClassMatch cm : classMatches) {
            if (cm.obfName.equals(obfClassName))
                return cm.deobfName;
        }
        return null;
    }

    @Deprecated
    public String[] getObfMethodName(String deobfMethodName, String deobfMethodDescriptor) {
        MethodMatch mm = findMethodMappingByDeobf(deobfMethodName, deobfMethodDescriptor);

        return new String[]{mm.obfOwner + "/" + mm.obfName, mm.obfDesc};
    }

    public String[] getObfMethodNameWithoutClass(String deobfMethodName, String deobfMethodDescriptor) {
        MethodMatch mm = findMethodMappingByDeobf(deobfMethodName, deobfMethodDescriptor);
        return mm != null ? new String[]{mm.obfName, mm.obfDesc} : null;
    }

    @Deprecated
    public String getObfFieldName(String deobfFieldName) {
        FieldMatch fm = findFieldByDeobf(deobfFieldName);
        return fm.obfOwner + "/" + fm.obfName;
    }

    public FieldMatch findFieldByDeobf(String fullDeobfName) {
        String[] split = GetOwnerAndField(fullDeobfName);
        return findFieldByDeobf(split[0], split[1]);
    }

    public FieldMatch findFieldByDeobf(String deobfOwner, String deobfName) {
        for (FieldMatch fm : fieldMatches) {
            if (fm.deobfOwner.equals(deobfOwner) && fm.deobfName.equals(deobfName))
                return fm;
        }
        return null;
    }

    @Deprecated
    public static String GetClassNameFromFullMethod(String fullMd) {
        String[] obfNameParted = fullMd.split("/");
        String[] newOwnerParted = new String[obfNameParted.length - 1];
        System.arraycopy(obfNameParted, 0, newOwnerParted, 0, obfNameParted.length - 1);

        return String.join("/", newOwnerParted);
    }

    protected static String[] GetOwnerAndField(String full) {
        String[] obfNameParted = full.split("/");
        String[] newOwnerParted = new String[obfNameParted.length - 1];
        System.arraycopy(obfNameParted, 0, newOwnerParted, 0, obfNameParted.length - 1);
        String owner = String.join("/", newOwnerParted);
        String field = Util.getLastPartOfArray(obfNameParted);
        return new String[]{owner, field};
    }

    public MethodMatch findMethodMappingByDeobf(String deobfOwner, String deobfName, String deobfDesc) {
        for (MethodMatch mm : methodMatches) {
            if (mm.deobfOwner.equals(deobfOwner) && mm.deobfName.equals(deobfName) && mm.deobfDesc.equals(deobfDesc))
                return mm;
        }
        return null;
    }

    public MethodMatch findMethodMappingByDeobf(String deobfFullName, String deobfDesc) {
        String[] split = GetOwnerAndField(deobfFullName);
        return findMethodMappingByDeobf(split[0], split[1], deobfDesc);
    }

    public MethodMatch findMethodMapping(PatchTarget pt) {
        return findMethodMappingByDeobf(pt.owner, pt.methodName, pt.descriptor);
    }

    public MethodMatch findMethodMappingByObf(String obfOwner, String obfName, String obfDesc) {
        for (MethodMatch mm : methodMatches) {
            if (mm.obfOwner.equals(obfOwner) && mm.obfName.equals(obfName) && mm.obfDesc.equals(obfDesc))
                return mm;
        }
        return null;
    }

    public FieldMatch findFieldMappingByObf(String obfOwner, String obfName) {
        for (FieldMatch fm : fieldMatches) {
            if (fm.obfOwner.equals(obfOwner) && fm.obfName.equals(obfName))
                return fm;
        }
        return null;
    }
}
