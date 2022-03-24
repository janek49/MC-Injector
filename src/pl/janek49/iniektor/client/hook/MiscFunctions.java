package pl.janek49.iniektor.client.hook;

import pl.janek49.iniektor.agent.Version;

public class MiscFunctions implements IWrapper {
    @ResolveMethod(version = {Version.MC1_9_4, Version.MC1_10}, name = "net/minecraft/potion/Potion/getPotionById", descriptor = "(I)Lnet/minecraft/potion/Potion;")
    public static MethodDefinition getPotionById;

    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/client/renderer/EntityRenderer/loadShader", descriptor = "(Lnet/minecraft/util/ResourceLocation;)V")
    public static MethodDefinition entityRenderer_LoadShader;

    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/renderer/EntityRenderer/theShaderGroup")
    public static FieldDefinition entityRenderer_TheShaderGroup;

    @Override
    public void initWrapper() {

    }

    @Override
    public Object getDefaultInstance() {
        return null;
    }
}
