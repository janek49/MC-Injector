package test;

import javassist.ClassPath;
import javassist.ClassPool;
import javassist.LoaderClassPath;
import pl.janek49.iniektor.Util;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.api.Reflector;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.regex.Pattern;

public class ReflectorTest {

    public static void main(String[] args) throws Exception {

        String clientVersionsDir = "C:\\Users\\Jan\\IdeaProjects\\MC-Injector\\versions";
        String mcVersionsDir = "C:\\Users\\Jan\\AppData\\Roaming\\.minecraft\\versions";

        File[] directories = new File(clientVersionsDir).listFiles(File::isDirectory);

        for (File dir : directories) {

            String ver = dir.getName();
            String mcVersionFolder = mcVersionsDir + "\\" + ver;
            String mcJar = mcVersionFolder + "\\" + ver + ".jar";

            if(!new File(mcJar).exists()){
                Logger.err("Missing game JAR for:", mcJar);
                continue;
            }

            TestClassLoader cl1 = new TestClassLoader(mcJar);

            Class testClass = cl1.loadClass("test.ReflectorTest");
            Object instance = testClass.getDeclaredConstructor(String.class, String.class).newInstance(dir.getAbsolutePath(), mcJar);
            testClass.getDeclaredMethod("start").invoke(instance);
        }
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

            } catch (Exception e) {
                e.printStackTrace();
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

        Logger.log("Testing Reflector - TargetJar:      " + targetJarFile);
        Logger.log("Testing Reflector - TargetVersion:  " + Reflector.MCP_VERSION_STRING);
        System.out.println();

        Logger.showOnlyErrors = true;
        Reflector reflector = new Reflector();
    }


}
