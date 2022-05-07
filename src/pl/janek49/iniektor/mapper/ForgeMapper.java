package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.HashMap;

public class ForgeMapper extends SeargeMapper {

    private McpMapper mcpMapper;

    public ForgeMapper(String mcpPath) {
        super(mcpPath);
        mcpMapper = new McpMapper(mcpPath);
    }


    @Override
    public void init() {
        readSeargeDefinitions();

        mcpMapper.init();

        for (ClassMatch cm : classMatches) {
            cm.obfName = cm.deobfName;
        }

        for (MethodMatch mm : methodMatches) {
            MethodMatch mcpMatch = mcpMapper.findMethodMappingByObf(mm.obfOwner, mm.obfName, mm.obfDesc);
            mm.obfOwner = mm.deobfOwner;
            mm.obfName = mm.deobfName;
            mm.obfDesc = mcpMatch.deobfDesc;
            mm.deobfName = mcpMatch.deobfName;
        }

        for (FieldMatch fm : fieldMatches) {
            FieldMatch mcpMatch = mcpMapper.findFieldMappingByObf(fm.obfOwner, fm.obfName);
            fm.obfOwner = fm.deobfOwner;
            fm.obfName = fm.deobfName;
            fm.deobfName = mcpMatch.deobfName;
        }
    }


}
