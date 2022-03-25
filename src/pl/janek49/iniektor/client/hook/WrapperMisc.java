package pl.janek49.iniektor.client.hook;

import pl.janek49.iniektor.agent.Version;

public class WrapperMisc implements IWrapper {
    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/potion/Potion/getPotionById", descriptor = "(I)Lnet/minecraft/potion/Potion;")
    public static MethodDefinition getPotionById;

    @ResolveMethod(version = Version.MC1_8_8, andAbove = true, name = "net/minecraft/client/renderer/EntityRenderer/loadShader", descriptor = "(Lnet/minecraft/util/ResourceLocation;)V")
    public static MethodDefinition entityRenderer_LoadShader;

    @ResolveField(version =  Version.MC1_8_8, andAbove = true, name = "net/minecraft/client/renderer/EntityRenderer/theShaderGroup")
    public static FieldDefinition entityRenderer_TheShaderGroup;

    @ResolveConstructor(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/potion/PotionEffect", params = {"net/minecraft/potion/Potion", "I"})
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/potion/PotionEffect", params = {"I", "I"})
    public static ConstructorDefinition PotionEffect;

    @ResolveField(version = Version.MC1_12, andAbove = true, name= "net/minecraft/util/Timer/field_194149_e")
    @ResolveField(version = Version.DEFAULT, name= "net/minecraft/util/Timer/timerSpeed")
    public static FieldDefinition Timer_timerSpeed;

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getDefaultInstance() {
        return null;
    }
}
