package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.ConstructorDefinition;
import pl.janek49.iniektor.api.ResolveConstructor;

@ClassImitator.ResolveClass("net/minecraft/client/renderer/texture/DynamicTexture")
public class DynamicTexture extends ClassImitator {
    public static ClassInformation target;

    @ResolveConstructor(params = "java/awt/image/BufferedImage")
    public static ConstructorDefinition constructor;
}
