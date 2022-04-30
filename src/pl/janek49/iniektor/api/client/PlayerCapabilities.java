package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.FieldDefinition;
import pl.janek49.iniektor.api.ResolveField;

@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/entity/player/PlayerCapabilities")
public class PlayerCapabilities extends ClassImitator {
    public static ClassInformation target;

    public PlayerCapabilities(Object instance) {
        super(instance);
    }

    private PlayerCapabilities() {
    }

    @ResolveField(value = "isFlying")
    private static FieldDefinition isFlying;

    @ResolveField(value = "allowFlying")
    private static FieldDefinition allowFlying;

    public boolean isFlying() {
        return PlayerCapabilities.isFlying.getBoolean(getInstanceBehind());
    }

    public boolean getAllowFlying() {
        return PlayerCapabilities.allowFlying.getBoolean(getInstanceBehind());
    }

    public void setIsFlying(boolean isFlying) {
        PlayerCapabilities.isFlying.set(getInstanceBehind(), isFlying);
    }

    public void setAllowFlying(boolean allowFlying) {
        PlayerCapabilities.allowFlying.set(getInstanceBehind(), allowFlying);
    }

    @ResolveField("walkSpeed")
    private static FieldDefinition walkSpeed;

    public void setWalkSpeed(float walkSpeed){
        PlayerCapabilities.walkSpeed.set(getInstanceBehind(), walkSpeed);
    }

    public float getWalkSpeed(){
        return PlayerCapabilities.walkSpeed.getFloat(getInstanceBehind());
    }
}
