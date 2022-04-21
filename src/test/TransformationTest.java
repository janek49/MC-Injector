package test;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.Asm503MinecraftObfuscator;
import pl.janek49.iniektor.agent.asm.Asm92MinecraftObfuscator;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.IniektorUtil;
import pl.janek49.iniektor.client.modules.impl.Step;
import pl.janek49.iniektor.mapper.Mapper;
import pl.janek49.iniektor.mapper.Pre17Mapper;

import java.io.File;
import java.lang.reflect.Field;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class TransformationTest {

    public static void beginTest(String clientVersionsDir, String mcVersionsDir, File dir) throws Exception {
        String ver = dir.getName();

        String mcVersionFolder = mcVersionsDir + "\\" + ver;
        String mcJar = mcVersionFolder + "\\" + ver + ".jar";

        if (!new File(mcJar).exists()) {
            //  Logger.err("Missing game JAR for:", mcJar);System.out.println();System.out.println();
            return;
        }

        TestClassLoader cl1 = new TestClassLoader(mcJar);

        Class testClass = cl1.loadClass("test.TransformationTest");
        Object instance = testClass.getDeclaredConstructor(String.class, String.class).newInstance(dir.getAbsolutePath(), mcJar);
        testClass.getDeclaredMethod("start").invoke(instance);

        System.out.println();

    }

    static class TestClassLoader extends URLClassLoader {

        public TestClassLoader(String url) throws MalformedURLException {
            super(new URL[]{new File(url).toURL()});
        }

        @Override
        public Class<?> loadClass(String name) {
            try {
                if (name.contains(".") && !name.startsWith("pl.") && !name.startsWith("test."))
                    return super.loadClass(name);

                ClassPath cp = new LoaderClassPath(this);
                ClassPool pool = ClassPool.getDefault();
                pool.appendClassPath(cp);
                byte[] bytes = pool.get(name).toBytecode();
                pool.removeClassPath(cp);

                return defineClass(name, bytes, 0, bytes.length);

            } catch (Throwable e) {
                e.printStackTrace();
                return null;
            }
        }
    }

    public String versionFolder, targetJarFile;

    public TransformationTest(String versionFolder, String targetJarFile) {
        this.versionFolder = versionFolder;
        this.targetJarFile = targetJarFile;
    }

    public void start() {
        Logger.showOnlyErrors = false;
        System.out.println();

        String versionString = Util.getLastPartOfArray(versionFolder.contains("/") ? versionFolder.split("/") : versionFolder.split(Pattern.quote("\\")));
        String mcpVersion = "MC" + versionString.replace(".", "_");
        Version v = Version.valueOf(mcpVersion);

        Class mapperClass = Mapper.class;

        if (v.ordinal() < Version.MC1_7_10.ordinal())
            mapperClass = Pre17Mapper.class;


        Logger.log("Testing Transformation - TargetJar:      " + targetJarFile);
        Logger.log("Testing Transformation - TargetVersion:  " + mcpVersion);
        System.out.println();

        Logger.showOnlyErrors = true;
        AgentMain.REMAPPER_ERRORS = 0;
        int errors = 0;

        try {
            AgentMain.MAPPER = (Mapper) mapperClass.getDeclaredConstructor(String.class).newInstance(versionFolder);
            AgentMain.MAPPER.init();

            ClassPath cp = new LoaderClassPath(getClass().getClassLoader());
            ClassPool pool = ClassPool.getDefault();
            pool.appendClassPath(cp);

            ZipFile zipFile = new ZipFile("C:\\Users\\Jan\\IdeaProjects\\MC-Injector\\out\\artifacts\\MC_Injector_jar\\MC-Injector.jar");
            Enumeration<? extends ZipEntry> entries = zipFile.entries();

            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
                if (zipEntry.getName().startsWith("pl/janek49/iniektor/client/") && zipEntry.getName().endsWith(".class")) {
                    try {
                        String clName = zipEntry.getName().replace("/", ".").substring(0, zipEntry.getName().length() - 6);
                        AgentMain.REMAPPER_CURRENT_CLASS = clName;

                        byte[] bytes = pool.get(clName).toBytecode();

                        Asm503MinecraftObfuscator.asm503remapNetMinecraftClasses(bytes);

                    } catch (Throwable ex) {
                        ex.printStackTrace();
                        errors++;
                    }
                }
            }
            zipFile.close();

            pool.removeClassPath(cp);

            errors += AgentMain.REMAPPER_ERRORS;

            if (errors > 0) {
                System.out.println();
                System.out.println("❌️❌️❌️❌️ Unit Test Failed - " + errors + " errors occured");
            } else {
                System.out.println("✔️✔️✔️✔️ Test completed succesfully - No Errors Found");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

}
