package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;

@ClassImitator.ResolveClass(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft")
public class Minecraft extends ClassImitator {

    public static ClassInformation target;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, name = "instance")
    @ResolveField(version = Version.DEFAULT, name = "theMinecraft")
    public static FieldDefinition instance;

    @ResolveField(version = Version.DEFAULT, name = "thePlayer")
    public static FieldDefinition thePlayer;

    @ResolveField(version = Version.DEFAULT, name = "timer")
    public static FieldDefinition timer;

    @ResolveField(version = Version.DEFAULT, name = "window")
    public static FieldDefinition window;

    @ResolveField(version = Version.DEFAULT, name = "displayWidth")
    public static FieldDefinition displayWidth;

    @ResolveField(version = Version.DEFAULT, name = "displayHeight")
    public static FieldDefinition displayHeight;

    @ResolveField(version = Version.DEFAULT, name = "gameSettings")
    public static FieldDefinition gameSettings;

    @ResolveField(version = Version.DEFAULT, name = "currentScreen")
    public static FieldDefinition currentScreen;

    @ResolveField(version = Version.MC1_8_8, andAbove = true, name = "fontRendererObj")
    @ResolveField(version = Version.DEFAULT, name = "fontRenderer")
    public static FieldDefinition fontRenderer;

    @ResolveMethod(version = Version.DEFAULT, name = "displayGuiScreen", descriptor = "(Lnet/minecraft/client/gui/GuiScreen;)V")
    public static MethodDefinition displayGuiScreen;

    @Override
    public Object getInstance() {
        return instance.get(null);
    }

    public static Object getMinecraft() {
        return instance.get(null);
    }

    public static void displayGuiScreen(Object gui){
        displayGuiScreen.call(gui);
    }
}
