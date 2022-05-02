package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;
import pl.janek49.iniektor.api.gui.TextureManager;

@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/client/Minecraft")
public class Minecraft extends ClassImitator {

    public static ClassInformation target;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "instance")
    @ResolveField(version = Version.DEFAULT, value = "theMinecraft")
    public static FieldDefinition instance;

    @ResolveField(version = Version.MC1_11_2, andAbove = true, value = "player")
    @ResolveField(version = Version.DEFAULT, value = "thePlayer")
    public static FieldDefinition thePlayer;

    @ResolveField(version = Version.DEFAULT, value = "timer")
    public static FieldDefinition timer;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "window")
    public static FieldDefinition window;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = Reflector.SKIP_MEMBER)
    @ResolveField(version = Version.DEFAULT, value = "displayWidth")
    public static FieldDefinition displayWidth;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = Reflector.SKIP_MEMBER)
    @ResolveField(version = Version.DEFAULT, value = "displayHeight")
    public static FieldDefinition displayHeight;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "options")
    @ResolveField(version = Version.DEFAULT, value = "gameSettings")
    public static FieldDefinition gameSettings;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "screen")
    @ResolveField(version = Version.DEFAULT, value = "currentScreen")
    public static FieldDefinition currentScreen;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "font")
    @ResolveField(version = Version.MC1_8_8, andAbove = true, value = "fontRendererObj")
    @ResolveField(version = Version.DEFAULT, value = "fontRenderer")
    public static FieldDefinition fontRenderer;

    @ResolveMethod(version = Version.MC1_14_4,andAbove = true, name = "setScreen", descriptor = "(Lnet/minecraft/client/gui/screens/Screen;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "displayGuiScreen", descriptor = "(Lnet/minecraft/client/gui/GuiScreen;)V")
    public static MethodDefinition displayGuiScreen;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "gameRenderer")
    @ResolveField("entityRenderer")
    public static FieldDefinition gameRenderer;

    @ResolveField(version = Version.MC1_14_4, value =  "gui")
    @ResolveField("ingameGUI")
    public static FieldDefinition ingameGUI;

    public static Object getInstanceObj() {
        return instance.get(null);
    }

    @Override
    public Object getInstanceBehind() {
        return instance.get(null);
    }

    public static void displayGuiScreen(Object gui) {
        displayGuiScreen.call(gui);
    }

    public static GameSettings getGameSettings() {
        return new GameSettings(gameSettings.get());
    }

    @ResolveMethod(name = "getTextureManager", descriptor = "()Lnet/minecraft/client/renderer/texture/TextureManager;")
    private static MethodDefinition getTextureManager;

    public static TextureManager getTextureManager() {
        return new TextureManager(getTextureManager.call());
    }

    public static EntityPlayerSP getPlayer(){
        return new EntityPlayerSP(thePlayer.get());
    }

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/client/Minecraft/getSoundManager", descriptor = "()Lnet/minecraft/client/sounds/SoundManager;")
    @ResolveMethod(version = Version.MC1_7_10, andAbove = true, name = "net/minecraft/client/Minecraft/getSoundHandler", descriptor = "()Lnet/minecraft/client/audio/SoundHandler;")
    public static MethodDefinition getSoundHandler;

    public static Object getSoundHandler(){
        return getSoundHandler.call();
    }
}
