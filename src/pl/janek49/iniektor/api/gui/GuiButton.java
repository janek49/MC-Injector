package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/client/gui/components/Button")
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/client/gui/GuiButton")
public class GuiButton extends ClassImitator {

    @ResolveConstructor(version = Version.MC1_14_4, andAbove = true, params = {"I", "I", "I", "I", "java/lang/String", "net/minecraft/client/gui/components/Button$OnPress"})
    @ResolveConstructor(version = Version.DEFAULT, params = {"I", "I", "I", "java/lang/String"})
    public static ConstructorDefinition constructor;

    public static ClassInformation target;


    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/client/gui/components/AbstractWidget/playDownSound",
            descriptor = "(Lnet/minecraft/client/sounds/SoundManager;)V")
    @ResolveMethod(version = Version.MC1_8_8, andAbove = true, name = "playPressSound", descriptor = "(Lnet/minecraft/client/audio/SoundHandler;)V")
    @ResolveMethod(version = Version.MC1_7_10, name = "func_146113_a", descriptor = "(Lnet/minecraft/client/audio/SoundHandler;)V")
    public static MethodDefinition playPressSound;
}
