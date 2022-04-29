package pl.janek49.iniektor.api;

import pl.janek49.iniektor.agent.Version;

public class WrapperPlayer implements IWrapper {

    @ResolveField(version = Version.MC1_11_2, andAbove = true, value = "net/minecraft/client/Minecraft/player")
    @ResolveField(version = Version.DEFAULT, value = "net/minecraft/client/Minecraft/thePlayer")
    public FieldDefinition thePlayer;

    @ResolveField(version = Version.DEFAULT, value = "net/minecraft/entity/player/EntityPlayer/capabilities")
    public FieldDefinition capabilities;



    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/EntityLivingBase/jump", descriptor = "()V")
    public MethodDefinition _jump;

    @ResolveField(version = Version.MC1_7_10, value = "net/minecraft/entity/Entity/field_145783_c")
    @ResolveField(version = Version.DEFAULT, value = "net/minecraft/entity/Entity/entityId")
    public FieldDefinition entityId;

    @ResolveMethod(version = Version.MC1_9_4,andAbove = true, name = "net/minecraft/client/entity/EntityPlayerSP/isHandActive", descriptor = "()Z")
    @ResolveMethod(version = Version.DEFAULT, name = "net/minecraft/entity/player/EntityPlayer/isUsingItem", descriptor = "()Z")
    public MethodDefinition isUsingItem;

    @Override
    public void initWrapper() {
    }

    @Override
    public Object getInstanceBehind() {
        return thePlayer.get(Reflector.MINECRAFT.getInstanceBehind());
    }



    public void jump() {
        _jump.call();
    }
}