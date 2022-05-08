package pl.janek49.iniektor.api.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.ClassImitator;
import pl.janek49.iniektor.api.reflection.FieldDefinition;
import pl.janek49.iniektor.api.reflection.ResolveField;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/client/Options")
@ClassImitator.ResolveClass("net/minecraft/client/settings/GameSettings")
public class GameSettings extends ClassImitator {
    public GameSettings(Object instance) {
        super(instance);
    }

    private GameSettings() {
        super(null);
    }

    public static ClassInformation target;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "fov")
    @ResolveField("fovSetting")
    public static FieldDefinition fovSetting;

    public float getFOV() {
        Object o = fovSetting.get(getInstanceBehind());
        return o instanceof Float ? (float)o : ((Double)o).floatValue();
    }

    public void setFOV(float fov) {
        fovSetting.set(getInstanceBehind(), fov);
    }


}
