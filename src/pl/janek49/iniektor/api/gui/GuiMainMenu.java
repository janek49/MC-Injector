package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.ClassImitator;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/client/gui/screens/TitleScreen")
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/client/gui/GuiMainMenu")
public class GuiMainMenu extends ClassImitator {
    public static ClassInformation target;
}
