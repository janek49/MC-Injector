package pl.janek49.iniektor.mapper;

import pl.janek49.iniektor.agent.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class McpMapper extends SeargeMapper {


    public HashMap<String, String> DeobfFieldNames = new HashMap<String, String>();
    public HashMap<String, String> DeobfMethodNames = new HashMap<String, String>();

    public McpMapper(String mcpPath) {
        super(mcpPath);
    }

    @Override
    public void init() {
        super.init();

        readMcpCsvFile("fields", DeobfFieldNames);
        readMcpCsvFile("methods", DeobfMethodNames);

        for (MethodMatch mm : methodMatches) {
            //if searge name is in mcp map
            if (mm.deobfName.startsWith("func_") && DeobfMethodNames.containsKey(mm.deobfName)) {
                //replace searge name with mcp name
                mm.deobfName = DeobfMethodNames.get(mm.deobfName);
            }
        }

        for (FieldMatch fm : fieldMatches) {
            //if searge name is in mcp map
            if (fm.deobfName.startsWith("field_") && DeobfFieldNames.containsKey(fm.deobfName)) {
                //replace searge name with mcp name
                fm.deobfName = DeobfFieldNames.get(fm.deobfName);
            }
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
}
