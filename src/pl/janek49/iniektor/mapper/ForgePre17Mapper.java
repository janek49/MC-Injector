package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.agent.Logger;

public class ForgePre17Mapper extends SeargeMapper {
    public ForgePre17Mapper(String mcpPath) {
        super(mcpPath);
    }

    private Pre17Mapper srcMapper;

    @Override
    public void init() {
        readSeargeDefinitions("forge.srg");

        srcMapper = new Pre17Mapper(MCP_PATH);
        srcMapper.init();

        for (ClassMatch cm : classMatches) {
            String srcName = srcMapper.getDeObfClassName(cm.obfName);
            cm.obfName = cm.deobfName;
            cm.deobfName = srcName;
        }

        for (MethodMatch mm : methodMatches) {
            MethodMatch srcMatch = srcMapper.findMethodMappingByObf(mm.obfOwner, mm.obfName, mm.obfDesc);

            if (srcMatch == null) {
                Logger.warn("[ForgePre17Mapper] No reverse mapping for:", mm);
                continue;
            }

            mm.obfOwner = mm.deobfOwner;
            mm.deobfOwner = srcMatch.deobfOwner;

            mm.obfName = mm.deobfName;
            mm.deobfName = srcMatch.deobfName;

            mm.obfDesc = mm.deobfDesc;
            mm.deobfDesc = srcMatch.deobfDesc;
        }

        for (FieldMatch fm : fieldMatches) {
            FieldMatch srcMatch = srcMapper.findFieldMappingByObf(fm.obfOwner, fm.obfName);

            if (srcMatch == null) {
                Logger.warn("[ForgePre17Mapper] No reverse mapping for:", fm);
                continue;
            }

            fm.obfOwner = fm.deobfOwner;
            fm.deobfOwner = srcMatch.deobfOwner;

            fm.obfName = fm.deobfName;
            fm.deobfName = srcMatch.deobfName;
        }
    }


    @Override
    public String getObfClassName(String deobfClassName) {
        return super.getObfClassName(Pre17Mapper.redirectClassName(deobfClassName));
    }

    @Override
    public FieldMatch findFieldByDeobf(String deobfOwner, String deobfName) {
        return super.findFieldByDeobf(Pre17Mapper.redirectClassName(deobfOwner), deobfName);
    }

    @Override
    public MethodMatch findMethodMappingByDeobf(String deobfOwner, String deobfName, String deobfDesc) {
        return super.findMethodMappingByDeobf(Pre17Mapper.redirectClassName(deobfOwner), deobfName, Pre17Mapper.redirectDescriptor(deobfDesc));
    }
}
