package pl.janek49.iniektor.main;

import com.formdev.flatlaf.FlatLightLaf;
import pl.janek49.iniektor.mapper.ForgeMapper;
import pl.janek49.iniektor.mapper.Pre17Mapper;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("JVM Injector by janek49");

        FlatLightLaf.setup();

        new InjectorGui();
    }
}
