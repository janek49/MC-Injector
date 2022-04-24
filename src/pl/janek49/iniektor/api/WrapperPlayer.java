package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Version;

public class WrapperPlayer implements IWrapper {

    @ResolveField(version = Version.MC1_11_2, andAbove = true, name = "net/minecraft/client/Minecraft/player")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/thePlayer")
    public FieldDefinition thePlayer;

    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/entity/player/EntityPlayer/capabilities")
    public FieldDefinition capabilities;

    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/addPotionEffect", descriptor = "(Lnet/minecraft/potion/PotionEffect;)V")
    public MethodDefinition _addPotionEffect;

    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(Lnet/minecraft/potion/Potion;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(I)V")
    public MethodDefinition _removePotionEffect;

    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/jump", descriptor = "()V")
    public MethodDefinition _jump;

    @ResolveField(version = Version.MC1_7_10, name = "net/minecraft/entity/Entity/field_145783_c")
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/entity/Entity/entityId")
    public FieldDefinition entityId;

    @ResolveMethod(version = Version.MC1_9_4,andAbove = true, name = "net/minecraft/client/entity/EntityPlayerSP/isHandActive", descriptor = "()Z")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/player/EntityPlayer/isUsingItem", descriptor = "()Z")
    public MethodDefinition isUsingItem;

    @Override
    public void initWrapper() {
    }

    @Override
    public Object getInstance() {
        return thePlayer.get(Reflector.MINECRAFT.getInstance());
    }

    public void addPotionEffect(int id, int duration) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_9_4)) {
            _addPotionEffect.call(WrapperMisc.PotionEffect.newInstance(WrapperMisc.getPotionById.invokeSt(id), duration));
        } else {
            _addPotionEffect.call(WrapperMisc.PotionEffect.newInstance(id, duration));
        }
    }

    public void removePotionEffect(int id) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_9_4)) {
            _removePotionEffect.call(WrapperMisc.getPotionById.invokeSt(id));
        } else {
            _removePotionEffect.call(id);
        }
    }

    public void jump() {
        _jump.call();
    }
}