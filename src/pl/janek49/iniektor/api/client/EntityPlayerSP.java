package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;

@ClassImitator.ResolveClass(version = Version.DEFAULT, name = "net/minecraft/client/entity/EntityPlayerSP")
public class EntityPlayerSP extends Entity {
    public EntityPlayerSP(Object instance) {
        super(instance);
    }

    @Override
    public Object getInstance() {
        return Minecraft.thePlayer.get();
    }

    public static ClassInformation target;

    @ResolveField(name = "capabilities")
    private static FieldDefinition capabilities;

    public PlayerCapabilities getCapabilities() {
        return new PlayerCapabilities(EntityPlayerSP.capabilities.get(getInstance()));
    }

    public void setCapabilities(PlayerCapabilities capabilities) {
        EntityPlayerSP.capabilities.set(getInstance(), capabilities.getInstance());
    }

    @ResolveMethod(name = "isUsingItem", descriptor = "()Z")
    private static MethodDefinition isUsingItem;

    public boolean isUsingItem() {
        return EntityPlayerSP.isUsingItem.invokeType(getInstance());
    }

    @ResolveMethod(name = "jump", descriptor = "()V")
    private static MethodDefinition jump;

    public void jump() {
        EntityPlayerSP.jump.invoke(getInstance());
    }
}
