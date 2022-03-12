package pl.janek49.iniektor.main;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("JVM Injector by janek49");

        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
        }

        new InjectorGui();

       /* Mapper m = new Mapper();
        m.init();

        m.getObfClassName("net/minecraft/item/ItemMinecart");
        m.getObfMethodName("net/minecraft/client/resources/data/AnimationFrame/hasNoTime", "()Z");
        m.getObfFieldName("net/minecraft/scoreboard/IScoreObjectiveCriteria/health");

        */
    }
}
