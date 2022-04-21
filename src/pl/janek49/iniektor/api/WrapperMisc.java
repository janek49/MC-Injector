package pl.janek49.iniektor.api;


import net.minecraft.client.gui.GuiButton;
import pl.janek49.iniektor.agent.Version;

public class WrapperMisc implements IWrapper {
    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/potion/Potion/getPotionById", descriptor = "(I)Lnet/minecraft/potion/Potion;")
    public static MethodDefinition getPotionById;

    @ResolveMethod(version = Version.MC1_8_8, andAbove = true, name = "net/minecraft/client/renderer/EntityRenderer/loadShader", descriptor = "(Lnet/minecraft/util/ResourceLocation;)V")
    public static MethodDefinition entityRenderer_LoadShader;

    @ResolveField(version = Version.MC1_8_8, andAbove = true, name = "net/minecraft/client/renderer/EntityRenderer/theShaderGroup")
    public static FieldDefinition entityRenderer_TheShaderGroup;

    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/potion/PotionEffect", params = {"net/minecraft/potion/Potion", "I"})
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/potion/PotionEffect", params = {"I", "I"})
    public static ConstructorDefinition PotionEffect;

    @ResolveField(version = Version.MC1_12, andAbove = true, name = "net/minecraft/util/Timer/field_194149_e")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/util/Timer/timerSpeed")
    public static FieldDefinition Timer_timerSpeed;

    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/client/gui/GuiWorldSelection", params = "net/minecraft/client/gui/GuiScreen")
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiSelectWorld", params = "net/minecraft/client/gui/GuiScreen")
    public static ConstructorDefinition GuiSinglePlayer;

    @ResolveMethod(version = Version.MC1_8_8, andAbove = true, name = "net/minecraft/client/gui/GuiButton/playPressSound", descriptor = "(Lnet/minecraft/client/audio/SoundHandler;)V")
    @ResolveMethod(version = Version.MC1_7_10, name = "net/minecraft/client/gui/GuiButton/func_146113_a", descriptor = "(Lnet/minecraft/client/audio/SoundHandler;)V")
    public static MethodDefinition GuiButton_playPressSound;

    @ResolveMethod(version = Version.MC1_6_4, name = "net/minecraft/src/SoundManager/playSoundFX", descriptor = "(Ljava/lang/String;FF)V")
    public static MethodDefinition mc164playFx;


    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstance() {
        return null;
    }

    public static void playPressSound() {
        try {
            if (Reflector.isOnOrBlwVersion(Version.MC1_6_4)) {
                Invoker.fromObj(Reflector.MINECRAFT.mc164soundManager.get()).method(mc164playFx).exec("random.click", 1f, 1f);
            } else {
                WrapperMisc.GuiButton_playPressSound.invoke(new GuiButton(0, 0, 0, ""), Reflector.MINECRAFT.getSoundHandler.call());
            }
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }
}
