package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.FieldDefinition;
import pl.janek49.iniektor.api.ResolveField;

@ClassImitator.ResolveClass(version = Version.DEFAULT, name = "net/minecraft/entity/player/PlayerCapabilities")
public class PlayerCapabilities extends ClassImitator {
    public static ClassInformation target;

    public PlayerCapabilities(Object instance) {
        super(instance);
    }

    private PlayerCapabilities() {
    }

    @ResolveField(name = "isFlying")
    private static FieldDefinition isFlying;

    @ResolveField(name = "allowFlying")
    private static FieldDefinition allowFlying;

    public boolean isFlying() {
        return PlayerCapabilities.isFlying.getBoolean(getInstance());
    }

    public boolean getAllowFlying() {
        return PlayerCapabilities.allowFlying.getBoolean(getInstance());
    }

    public void setIsFlying(boolean isFlying) {
        PlayerCapabilities.isFlying.set(getInstance(), isFlying);
    }

    public void setAllowFlying(boolean allowFlying) {
        PlayerCapabilities.allowFlying.set(getInstance(), allowFlying);
    }
}
