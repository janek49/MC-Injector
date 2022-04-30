package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.ConstructorDefinition;
import pl.janek49.iniektor.api.ResolveConstructor;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/network/chat/TextComponent")
@ClassImitator.ResolveClass(version = Version.MC1_9_4, andAbove = true, value = "net/minecraft/util/text/TextComponentString")
@ClassImitator.ResolveClass(version = Version.MC1_7_10, andAbove = true, value = "net/minecraft/util/ChatComponentText")
public class TextComponent extends ClassImitator {
    public static ClassInformation target;

    @ResolveConstructor(params = "java/lang/String")
    public static ConstructorDefinition constructor;
}
