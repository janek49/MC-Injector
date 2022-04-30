package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.MethodDefinition;
import pl.janek49.iniektor.api.ResolveMethod;

@ClassImitator.ResolveClass("net/minecraft/client/renderer/texture/TextureManager")
public class TextureManager extends ClassImitator {

    public TextureManager(Object instance) {
        super(instance);
    }

    private TextureManager() {
    }

    public static ClassInformation target;

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "bind", descriptor = "(Lnet/minecraft/resources/ResourceLocation;)V")
    @ResolveMethod(name = "bindTexture", descriptor = "(Lnet/minecraft/util/ResourceLocation;)V")
    private static MethodDefinition bindTexture;

    public void bindTexture(Object resLocationObj) {
        bindTexture.invoke(getInstanceBehind(), resLocationObj);
    }

    public void bindTexture(ResourceLocation resLocation) {
        bindTexture(resLocation.getInstanceBehind());
    }

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true,
            name = "register", descriptor = "(Ljava/lang/String;Lnet/minecraft/client/renderer/texture/DynamicTexture;)Lnet/minecraft/resources/ResourceLocation;")
    @ResolveMethod(name = "getDynamicTextureLocation", descriptor = "(Ljava/lang/String;Lnet/minecraft/client/renderer/texture/DynamicTexture;)Lnet/minecraft/util/ResourceLocation;")
    private static MethodDefinition getDynamicTextureLocation;

    public ResourceLocation getDynamicTextureLocation(String name, DynamicTexture texture) {
        return new ResourceLocation(getDynamicTextureLocation.invoke(getInstanceBehind(), name, texture.getInstanceBehind()));
    }

    public ResourceLocation getDynamicTextureLocation(String name, Object texture) {
        return new ResourceLocation(getDynamicTextureLocation.invoke(getInstanceBehind(), name, texture));
    }
}
