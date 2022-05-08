package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.ClassImitator;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/world/entity/LivingEntity")
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/entity/EntityLivingBase")

public class EntityLiving extends Entity {
    public EntityLiving(Object instance) {
        super(instance);
    }

    public static ClassInformation target;
}
