package pl.janek49.iniektor.agent.hotswap;

import javassist.ClassPool;
import javassist.CtClass;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.AsmUtil;
import pl.janek49.iniektor.agent.Logger;

import java.io.*;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.Instrumentation;
import java.net.URL;
import java.security.CodeSource;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

public class HotSwapper {
    public static void HotSwapIniektor(Instrumentation inst) throws Exception {
        Logger.log("HotSwapper INIT");

        ClassPool pool = ClassPool.getDefault();

        ZipFile zipFile = new ZipFile(AgentMain.JARFILE.getFile());
        ZipInputStream zip = new ZipInputStream(AgentMain.JARFILE.openStream());
        while (true) {
            ZipEntry e = zip.getNextEntry();
            if (e == null)
                break;
            String name = e.getName();
            if (name.startsWith("pl/janek49/iniektor/client/") && name.endsWith(".class")) {
                String className = name.substring(0, name.length() - 6).replace("/", ".");
                Logger.log("Reading class file:", name);
                byte[] bytes = ReadJarFileEntry(zipFile, e);
                Logger.log("Redefining class:", className);
                CtClass ctClass = pool.getCtClass(className);
                ClassDefinition def = new ClassDefinition(AsmUtil.findClass(className), bytes);
                inst.redefineClasses(def);
                Logger.log("Done:", className);
            }
        }
        zipFile.close();
    }

    public static byte[] ReadJarFileEntry(ZipFile file, ZipEntry entry) throws Exception {
        try (InputStream in = file.getInputStream(entry)) {
            return readFully(in);
        }
    }

    public static byte[] readFully(InputStream input) throws IOException {
        byte[] buffer = new byte[8192];
        int bytesRead;
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
        return output.toByteArray();
    }
}
