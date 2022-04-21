package test;

import pl.janek49.iniektor.Util;

import java.io.File;

public class TestMain {
    public static void main(String[] args) throws Exception {

        String clientVersionsDir = "C:\\Users\\Jan\\IdeaProjects\\MC-Injector\\versions";
        String mcVersionsDir = "C:\\Users\\Jan\\AppData\\Roaming\\.minecraft\\versions";

        File[] directories = new File(clientVersionsDir).listFiles(File::isDirectory);

        for (File dir : directories) {
            String ver = dir.getName();
            String mcVersionFolder = mcVersionsDir + "\\" + ver;
            String mcJar = mcVersionFolder + "\\" + ver + ".jar";

            if (!new File(mcJar).exists()) {
                continue;
            }

            ReflectorTest.beginTest(clientVersionsDir, mcVersionsDir, dir);
            TransformationTest.beginTest(clientVersionsDir, mcVersionsDir, dir);


            System.out.println();
            System.out.println(Util.repeatString("*", 150));
            System.out.println(Util.repeatString("*", 150));
            System.out.println();

        }
    }
}
