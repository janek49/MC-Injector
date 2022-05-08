package pl.janek49.iniektor.api.wrapper;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.reflection.*;

public class WrapperMinecraft implements IWrapper {



    @ResolveField(version = Version.MC1_6_4, value = "net/minecraft/src/Minecraft/sndManager")
    public FieldDefinition mc164soundManager;


    @Override
    public void initWrapper() {

    }

    @Override
    public Object getInstanceBehind() {
        return Minecraft.getInstanceObj();
    }


    public Object getTimer() {
        return Minecraft.timer.get();
    }

    public float getTimerSpeed() {
        return Invoker.fromObj(getTimer()).field(WrapperMisc.Timer_timerSpeed).getType();
    }

    public void setTimerSpeed(float speed) {
        if (Reflector.isOnOrAbvVersion(Version.MC1_12))
            speed = 50f / speed;

        Invoker.fromObj(getTimer()).field(WrapperMisc.Timer_timerSpeed).set(speed);
    }
}
