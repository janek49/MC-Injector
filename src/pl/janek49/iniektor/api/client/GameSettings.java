package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.api.ClassImitator;
import pl.janek49.iniektor.api.FieldDefinition;
import pl.janek49.iniektor.api.ResolveField;

@ClassImitator.ResolveClass("net/minecraft/client/settings/GameSettings")
public class GameSettings extends ClassImitator {
    public GameSettings(Object instance) {
        super(instance);
    }

    private GameSettings(){
        super(null);
    }

    public static ClassInformation target;

    @ResolveField("fovSetting")
    public static    FieldDefinition fovSetting;

    public float getFOV() {
        return fovSetting.getFloat(getInstanceBehind());
    }

    public void setFOV(float fov) {
        fovSetting.set(getInstanceBehind(), fov);
    }


}
