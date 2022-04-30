package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.api.Keys;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Speed extends Module implements EventHandler {

    public RangeProperty value = new RangeProperty("value", 1.2f, 1, 2, "Walking speed");

    public Speed() {
        super("Speed", Keys.KEY_H, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            if (Keyboard.isKeyDown(Keyboard.KEY_W) || Keyboard.isKeyDown(Keyboard.KEY_A) || Keyboard.isKeyDown(Keyboard.KEY_S) || Keyboard.isKeyDown(Keyboard.KEY_D)) {
                if (getPlayer().isOnGround() && !getPlayer().isInWater()) {
                    double mX = getPlayer().getMotionX();
                    double mZ = getPlayer().getMotionZ();

                    if((mX <= 1 && mZ <=1 && mX >= -1&& mZ >= -1)){
                        getPlayer().setMotionX(mX * value.getValue());
                        getPlayer().setMotionZ(mZ * value.getValue());
                    }else{
                        getPlayer().setMotionX(0);
                        getPlayer().setMotionZ(0);
                    }


                }
            }
        }
    }
}
