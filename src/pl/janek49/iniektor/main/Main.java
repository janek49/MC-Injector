package pl.janek49.iniektor.main;

import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("JVM Injector by janek49");

        FlatLightLaf.setup();

        new InjectorGui();
    }
}
