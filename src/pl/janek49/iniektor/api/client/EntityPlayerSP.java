package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/client/player/LocalPlayer")
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/client/entity/EntityPlayerSP")
public class EntityPlayerSP extends Entity {

    public EntityPlayerSP(Object instance) {
        super(instance);
    }

    private EntityPlayerSP() {
        super(null);
    }

    @Override
    public Object getInstanceBehind() {
        return Minecraft.thePlayer.get();
    }

    public static ClassInformation target;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/world/entity/player/Player/abilities")
    @ResolveField(value = "net/minecraft/entity/player/EntityPlayer/capabilities")
    private static FieldDefinition capabilities;

    public PlayerCapabilities getCapabilities() {
        return new PlayerCapabilities(EntityPlayerSP.capabilities.get(this.getInstanceBehind()));
    }

    public void setCapabilities(PlayerCapabilities capabilities) {
        EntityPlayerSP.capabilities.set(this.getInstanceBehind(), capabilities.getInstanceBehind());
    }

    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/entity/EntityLivingBase/getItemInUseCount", descriptor = "()I")
    private static MethodDefinition getItemInUseCount;

    public int getItemInUseCount() {
        return EntityPlayerSP.getItemInUseCount.invokeType(getInstanceBehind());
    }

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "isHandsBusy", descriptor = "()Z")
    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "isHandActive", descriptor = "()Z")
    @ResolveMethod(name = "net/minecraft/entity/player/EntityPlayer/isUsingItem", descriptor = "()Z")
    private static MethodDefinition isUsingItem;

    public boolean isUsingItem() {
        if (Reflector.isOnOrAbvVersion(Version.MC1_9_4))
            return getItemInUseCount() > 0;
        else
            return EntityPlayerSP.isUsingItem.invokeType(this.getInstanceBehind());
    }

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/world/entity/LivingEntity/jumpFromGround", descriptor = "()V")
    @ResolveMethod(name = "net/minecraft/entity/EntityLivingBase/jump", descriptor = "()V")
    private static MethodDefinition jump;

    public void jump() {
        EntityPlayerSP.jump.invoke(this.getInstanceBehind());
    }

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/world/entity/LivingEntity/addEffect", descriptor = "(Lnet/minecraft/world/effect/MobEffectInstance;)Z")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/addPotionEffect", descriptor = "(Lnet/minecraft/potion/PotionEffect;)V")
    private static MethodDefinition _addPotionEffect;

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "net/minecraft/world/entity/LivingEntity/removeEffect", descriptor = "(Lnet/minecraft/world/effect/MobEffect;)Z")
    @ResolveMethod(version = Version.MC1_9_4, andAbove = true, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(Lnet/minecraft/potion/Potion;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(I)V")
    private static MethodDefinition _removePotionEffect;

    public void addPotionEffect(int id, int duration) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_9_4)) {
            _addPotionEffect.invoke(getInstanceBehind(), WrapperMisc.PotionEffect.newInstance(WrapperMisc.getPotionById.invokeSt(id), duration));
        } else {
            _addPotionEffect.invoke(getInstanceBehind(), WrapperMisc.PotionEffect.newInstance(id, duration));
        }
    }

    public void removePotionEffect(int id) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_9_4)) {
            _removePotionEffect.invoke(getInstanceBehind(), WrapperMisc.getPotionById.invokeSt(id));
        } else {
            _removePotionEffect.invoke(getInstanceBehind(), id);
        }
    }

    @ResolveField(version = Version.MC1_9_4, andAbove = true, value = "connection")
    @ResolveField(version = Version.MC1_8_8, value = "sendQueue")
    private static FieldDefinition _sendQueue;

    public Object getConnection() {
        return EntityPlayerSP._sendQueue.get(getInstanceBehind());
    }

    public boolean isMoving() {
        boolean isMoving = getMotionX() > 0 || getMotionY() > 0 || getMotionZ() > 0;
        return isMoving;
    }
}
