package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.ClassImitator;

@ClassImitator.ResolveClass(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiMainMenu")
public class GuiMainMenu extends ClassImitator {
    public static ClassInformation target;
}
