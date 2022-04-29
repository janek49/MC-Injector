package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.ConstructorDefinition;
import pl.janek49.iniektor.api.ResolveConstructor;

@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/client/gui/GuiButton")
public class GuiButton extends ClassImitator {

    @ResolveConstructor(params = {"I", "I", "I", "java/lang/String"})
    public static ConstructorDefinition constructor;

    public static ClassInformation target;
}
