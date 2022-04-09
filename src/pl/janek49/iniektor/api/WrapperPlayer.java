package pl.janek49.iniektor.api;

import net.minecraft.client.Minecraft;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.annotation.ImportMethod;
import pl.janek49.iniektor.agent.annotation.ImportMethodContainer;

import static pl.janek49.iniektor.agent.Version.*;
import static pl.janek49.iniektor.agent.Version.Compare.OR_HIGHER;
import static pl.janek49.iniektor.agent.Version.Compare.OR_LOWER;

@ImportMethodContainer
public class WrapperPlayer implements IWrapper {

    @ResolveField(version = Version.MC1_11_2, andAbove = true, name = "net/minecraft/client/Minecraft/player")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/thePlayer")
    public FieldDefinition thePlayer;

    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/entity/player/EntityPlayer/capabilities")
    public FieldDefinition capabilities;

    @ResolveMethod(version = DEFAULT, name = "net/minecraft/entity/EntityLivingBase/jump", descriptor = "()V")
    public MethodDefinition _jump;

    @ImportMethod(name = "net/minecraft/entity/EntityLivingBase/addPotionEffect", descriptor = "(Lnet/minecraft/potion/PotionEffect;)V")
    public static void _addPotionEffect(Object player, Object potion) {
    }

    @ImportMethod(version = MC1_8_8, vcomp = OR_LOWER, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(I)V")
    public static void _removePotionEffect(Object player, int potionId) {
    }

    @ImportMethod(version = MC1_9_4, vcomp = OR_HIGHER, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(Lnet/minecraft/potion/Potion;)V")
    public static void _removePotionEffect(Object player, Object potion) {
    }

    @Override
    public void initWrapper() {
    }

    @Override
    public Object getDefaultInstance() {
        return thePlayer.get(Minecraft.getMinecraft());
    }

    public void addPotionEffect(int id, int duration) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_9_4)) {
            _addPotionEffect(getDefaultInstance(), WrapperMisc.PotionEffect.newInstance(WrapperMisc.getPotionById.invokeSt(id), duration));
        } else {
            _addPotionEffect(getDefaultInstance(), WrapperMisc.PotionEffect.newInstance(id, duration));
        }
    }

    public void removePotionEffect(int id) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_9_4)) {
            _removePotionEffect(getDefaultInstance(), WrapperMisc.getPotionById.invokeSt(id));
        } else {
            _removePotionEffect(getDefaultInstance(), id);
        }
    }

    public void jump() {
        _jump.call();
    }
}
