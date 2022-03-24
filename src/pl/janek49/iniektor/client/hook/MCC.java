package pl.janek49.iniektor.client.hook;

import pl.janek49.iniektor.agent.Version;

public class MCC implements IWrapper {
    @ResolveConstructor(version = {Version.MC1_9_4,Version.MC1_10}, name = "net/minecraft/potion/PotionEffect", params = {"net/minecraft/potion/Potion", "I"})
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/potion/PotionEffect", params = {"I", "I"})
    public static ConstructorDefinition PotionEffect;

    @ResolveConstructor(version = Version.MC1_10, name = "net/minecraft/util/text/TextComponentString", params = {"java/lang/String"})
    @ResolveConstructor(version = Version.DEFAULT, name = "net/minecraft/util/ChatComponentText", params = {"java/lang/String"})
    public static ConstructorDefinition TextComponentString;

    @Override
    public void initWrapper() {
    }

    @Override
    public Object getDefaultInstance() {
        return null;
    }
}
