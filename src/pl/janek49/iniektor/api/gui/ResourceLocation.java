package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.ConstructorDefinition;
import pl.janek49.iniektor.api.ResolveConstructor;

@ClassImitator.ResolveClass("net/minecraft/util/ResourceLocation")
public class ResourceLocation extends ClassImitator {
    public static ClassInformation target;

    @ResolveConstructor(params = "java/lang/String")
    public static ConstructorDefinition constructor;
}
