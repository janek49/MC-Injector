package test;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.AgentMain;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.patcher.ApplyPatchTransformer;
import pl.janek49.iniektor.api.reflection.Reflector;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

public class ReflectorTest {

    public static void beginTest(String clientVersionsDir, String mcVersionsDir, File dir) throws Exception {
        String ver = dir.getName();
        String mcVersionFolder = mcVersionsDir + "\\" + ver;
        String mcJar = mcVersionFolder + "\\" + ver + ".jar";

        if (!new File(mcJar).exists()) {
            // Logger.err("Missing game JAR for:", mcJar);
            return;
        }

        TestClassLoader cl1 = new TestClassLoader(mcJar);

        Class testClass = cl1.loadClass("test.ReflectorTest");
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
                ClassPool pool = new ClassPool();
                pool.appendClassPath(cp);
                byte[] bytes = pool.get(name).toBytecode();

                return defineClass(name, bytes, 0, bytes.length);

            } catch (Throwable e) {
                Logger.err(name);
                Logger.ex(e);
                return null;
            }
        }
    }

    public String versionFolder, targetJarFile;

    public TestClassLoader tcl;

    public ReflectorTest(String versionFolder, String targetJarFile) {
        this.versionFolder = versionFolder;
        this.targetJarFile = targetJarFile;
    }

    public void start() {
        Logger.showOnlyErrors = false;
        System.out.println();

        Reflector.TEST_MODE = true;
        Reflector.IS_FORGE = false;
        Reflector.MCP_PATH = versionFolder;
        String versionString = Util.getLastPartOfArray(versionFolder.contains("/") ? versionFolder.split("/") : versionFolder.split(Pattern.quote("\\")));
        Reflector.MCP_VERSION_STRING = "MC" + versionString.replace(".", "_");
        AgentMain.MCP_VERSION = Version.valueOf(Reflector.MCP_VERSION_STRING);

        Logger.log("TargetVersion:  " + Reflector.MCP_VERSION_STRING, "     TargetJar:  " + targetJarFile);
        System.out.println();
        System.out.println("Testing: Reflector");

        Logger.showOnlyErrors = true;
        Reflector reflector = new Reflector();
        Logger.showOnlyErrors = false;

        printResult(reflector.errors);
        System.out.println();



        System.out.println("Testing: ApplyPatchTransformer");

        Logger.showOnlyErrors = true;
       try {
           AgentMain.MAPPER = Reflector.MAPPER;
           ApplyPatchTransformer apt = new ApplyPatchTransformer();
           FakeInstrumentation fi = new FakeInstrumentation();
           fi.transformer = apt;
           apt.ApplyPatches(fi);
           Logger.showOnlyErrors = false;
           printResult(apt.errors);
       } catch (Throwable ex) {
           Logger.showOnlyErrors = false;
           Logger.ex(ex);
           printResult(1);
       }



    }

    public void printResult(int err){
        if (err>0) {
            System.out.println();
            System.out.println("❌️❌️❌️❌️ Unit Test Failed - " + err + " errors occured");
        } else {
            System.out.println("✔️✔️✔️✔️ Test completed succesfully - No Errors Found");
        }
    }


}
