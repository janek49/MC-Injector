package pl.janek49.iniektor.mapper;

public class Pre17Mapper extends McpMapper {

    public Pre17Mapper(String mcpPath) {
        super(mcpPath);
    }

    public static String redirectClassName(String in) {
        if(in == null)
            return null;

        if (in.startsWith("net/minecraft/") && !in.startsWith("net/minecraft/src/")) {
            String[] split = in.split("/");
            String className = split[split.length - 1];
            return "net/minecraft/src/" + className;
        }
        return in;
    }

    public static String redirectDescriptor(String in) {
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
                output.append("L");
                output.append(redirectClassName(literal));
                output.append(";");
            } else {
                output.append(chr);
            }
        }
        return output.toString();
    }


    @Override
    public String getObfClassName(String deobfClassName) {
        return super.getObfClassName(redirectClassName(deobfClassName));
    }

    @Override
    public FieldMatch findFieldByDeobf(String deobfOwner, String deobfName) {
        return super.findFieldByDeobf(redirectClassName(deobfOwner), deobfName);
    }

    @Override
    public MethodMatch findMethodMappingByDeobf(String deobfOwner, String deobfName, String deobfDesc) {
        return super.findMethodMappingByDeobf(redirectClassName(deobfOwner), deobfName, redirectDescriptor(deobfDesc));
    }
}
