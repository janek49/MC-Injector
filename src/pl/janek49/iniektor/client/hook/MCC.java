package pl.janek49.iniektor.client.hook;

import pl.janek49.iniektor.agent.Version;

public class MCC implements IWrapper {
    @ResolveConstructor(version = Version.MC1_9_4, name = "net/minecraft/potion/PotionEffect", params = {"net/minecraft/potion/Potion", "I"})
    public static ConstructorDefinition PotionEffect;

    @Override
    public void initWrapper() {
    }

    @Override
    public Object getDefaultInstance() {
        return null;
    }
}
