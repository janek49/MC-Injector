package pl.janek49.iniektor.api.wrapper;


import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.gui.GuiButton;
import pl.janek49.iniektor.api.reflection.*;

public class WrapperMisc implements IWrapper {
    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/world/effect/MobEffect/byId", descriptor = "(I)Lnet/minecraft/world/effect/MobEffect;")
    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/potion/Potion/getPotionById", descriptor = "(I)Lnet/minecraft/potion/Potion;")
    public static MethodDefinition getPotionById;

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/client/renderer/GameRenderer/loadEffect", descriptor = "(Lnet/minecraft/resources/ResourceLocation;)V")
    @ResolveMethod(version = Version.MC1_8_8, andAbove = true, name = "net/minecraft/client/renderer/EntityRenderer/loadShader", descriptor = "(Lnet/minecraft/util/ResourceLocation;)V")
    public static MethodDefinition entityRenderer_LoadShader;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/client/renderer/GameRenderer/postEffect")
    @ResolveField(version = Version.MC1_8_8, andAbove = true, value = "net/minecraft/client/renderer/EntityRenderer/theShaderGroup")
    public static FieldDefinition entityRenderer_TheShaderGroup;

    @ResolveConstructor(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/world/effect/MobEffectInstance", params = {"net/minecraft/world/effect/MobEffect", "I"})
    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/potion/PotionEffect", params = {"net/minecraft/potion/Potion", "I"})
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/potion/PotionEffect", params = {"I", "I"})
    public static ConstructorDefinition PotionEffect;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/client/Timer/msPerTick")
    @ResolveField(version = Version.MC1_12, andAbove = true, value = "net/minecraft/util/Timer/field_194149_e")
    @ResolveField(version = Version.DEFAULT, value = "net/minecraft/util/Timer/timerSpeed")
    public static FieldDefinition Timer_timerSpeed;

    @ResolveConstructor(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/client/gui/screens/worldselection/SelectWorldScreen",
            params = "net/minecraft/client/gui/screens/Screen")
    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/gui/GuiWorldSelection", params = "net/minecraft/client/gui/GuiScreen")
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiSelectWorld", params = "net/minecraft/client/gui/GuiScreen")
    public static ConstructorDefinition GuiSinglePlayer;

    @ResolveConstructor(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/client/gui/screens/multiplayer/JoinMultiplayerScreen",
            params = "net/minecraft/client/gui/screens/Screen")
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiMultiplayer", params = "net/minecraft/client/gui/GuiScreen")
    public static ConstructorDefinition GuiMultiPlayer;

    @ResolveConstructor(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/client/gui/screens/OptionsScreen",
            params = {"net/minecraft/client/gui/screens/Screen", "net/minecraft/client/Options"})
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiOptions",
            params = {"net/minecraft/client/gui/GuiScreen", "net/minecraft/client/settings/GameSettings"})
    public static ConstructorDefinition GuiOptions;


    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/SoundManager/playSoundFX", descriptor = "(Ljava/lang/String;FF)V")
    public static MethodDefinition mc164playFx;


    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstanceBehind() {
        return null;
    }

    public static void playPressSound() {
        try {
            if (Reflector.isOnOrBlwVersion(Version.MC1_6_4)) {
                Invoker.fromObj(Reflector.MINECRAFT.mc164soundManager.get()).method(mc164playFx).exec("random.click", 1f, 1f);
            } else if (Reflector.isOnOrBlwVersion(Version.MC1_12_2)) {
                GuiButton.playPressSound.invoke(GuiButton.constructor.newInstance(0, 0, 0, ""), Minecraft.getSoundHandler());
            } else {
                GuiButton.playPressSound.invoke(GuiButton.constructor.newInstance(0, 0, 0, 0, "", null), Minecraft.getSoundHandler());
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
