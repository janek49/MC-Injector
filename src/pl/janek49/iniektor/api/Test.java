package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.annotation.ImportMethod;

import static pl.janek49.iniektor.agent.Version.*;
import static pl.janek49.iniektor.agent.Version.Compare.*;

public class Test {

    public static Object getPlayer(Object theMinecraft) {
        return null;
    }


    @ImportMethod(name = "net/minecraft/entity/EntityLivingBase/addPotionEffect", descriptor = "(Lnet/minecraft/potion/PotionEffect;)V")
    public static void _addPotionEffect(Object player, Object potion) {
    }

    @ImportMethod(version = MC1_8_8, vcomp = OR_LOWER, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(I)V")
    public static void _removePotionEffect(Object player, int potionId) {
    }

    @ImportMethod(version = MC1_9_4, vcomp = OR_HIGHER, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(Lnet/minecraft/potion/Potion;)V")
    public static void _removePotionEffect(Object player, Object potion) {
    }

}
