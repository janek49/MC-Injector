package pl.janek49.iniektor.client.hook;

import net.minecraft.client.Minecraft;
import pl.janek49.iniektor.agent.Version;

public class WrapperPlayer implements IWrapper {
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/thePlayer")
    public FieldDefinition thePlayer;

    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/entity/player/EntityPlayer/capabilities")
    public FieldDefinition capabilities;

    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/addPotionEffect", descriptor = "(Lnet/minecraft/potion/PotionEffect;)V")
    public MethodDefinition _addPotionEffect;

    @ResolveMethod(version = {Version.MC1_9_4, Version.MC1_10}, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(Lnet/minecraft/potion/Potion;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(I)V")
    public MethodDefinition _removePotionEffect;


    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/jump", descriptor = "()V")
    public MethodDefinition _jump;

    @Override
    public void initWrapper() {
        //  Minecraft.getMinecraft().thePlayer.jump();
    }

    @Override
    public Object getDefaultInstance() {
        return thePlayer.get(Minecraft.getMinecraft());
    }

    public void addPotionEffect(int id, int duration) {
        if (Reflector.MCP_VERSION.ordinal() >= Version.MC1_9_4.ordinal()) {
            _addPotionEffect.call(WrapperMisc.PotionEffect.newInstance(WrapperMisc.getPotionById.invokeSt(id), duration));
        } else {
            _addPotionEffect.call(WrapperMisc.PotionEffect.newInstance(id, duration));
        }
    }

    public void removePotionEffect(int id) {
        if (Reflector.MCP_VERSION.ordinal() >= Version.MC1_9_4.ordinal()) {
            _removePotionEffect.call(WrapperMisc.getPotionById.invokeSt(id));
        } else {
            _removePotionEffect.call(id);
        }
    }

    public void jump() {
        _jump.call();
    }
}
