package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.ClassImitator;
import pl.janek49.iniektor.api.reflection.ConstructorDefinition;
import pl.janek49.iniektor.api.reflection.ResolveConstructor;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/resources/ResourceLocation")
@ClassImitator.ResolveClass("net/minecraft/util/ResourceLocation")
public class ResourceLocation extends ClassImitator {
    public static ClassInformation target;

    @ResolveConstructor(params = "java/lang/String")
    public static ConstructorDefinition constructor;

    public ResourceLocation(Object instance){
        super(instance);
    }

    private ResourceLocation(){}
}
