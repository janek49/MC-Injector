package pl.janek49.iniektor.client.hook;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.player.PlayerCapabilities;
import pl.janek49.iniektor.agent.Version;

public class WrapperPlayer implements IWrapper {
    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/client/Minecraft/thePlayer")
    public FieldDefinition thePlayer;

    @ResolveField(version = Version.DEFAULT, name = "net/minecraft/entity/player/EntityPlayer/capabilities")
    public FieldDefinition capabilities;

    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/addPotionEffect", descriptor = "(Lnet/minecraft/potion/PotionEffect;)V")
    public MethodDefinition addPotionEffect;

    @ResolveMethod(version = {Version.MC1_9_4, Version.MC1_10}, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(Lnet/minecraft/potion/Potion;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/removePotionEffect", descriptor = "(I)V")
    public MethodDefinition removePotionEffect;

    @ResolveMethod(version = {Version.MC1_9_4, Version.MC1_10}, name = "net/minecraft/potion/Potion/getPotionById", descriptor = "(I)Lnet/minecraft/potion/Potion;")
    public MethodDefinition getPotionById;

    @ResolveMethod(version = Version.MC1_10, name = "net/minecraft/client/entity/EntityPlayerSP/addChatMessage", descriptor = "(Lnet/minecraft/util/text/ITextComponent;)V")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityPlayerSP/addChatMessage", descriptor = "(Lnet/minecraft/util/IChatComponent;)V")
    public MethodDefinition addChatMessage;

    @Override
    public void initWrapper() {
    }

    @Override
    public Object getDefaultInstance() {
        return thePlayer.get(Minecraft.getMinecraft());
    }
}
