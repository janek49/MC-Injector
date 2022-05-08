package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.api.reflection.Reflector;
import pl.janek49.iniektor.api.client.Entity;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class Step extends Module {
    public Step() {
        super("Step", Keys.KEY_NONE, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    public Property<Boolean> useTimer = new Property<>("useTimer", false, "Use Timer");
    public RangeProperty timerSpeed = new RangeProperty("timerSpeed", 2, 1, 10, "Timer Speed");
    public RangeProperty timerTicks = new RangeProperty("timerTicks", 8, 1, 20, "Timer disabel treshold");

    private int ticks = 0;

    @Override
    public void onEnable() {
        ticks = 0;
    }

    @Override
    public void onEvent(IEvent event) {
        Entity player = new Entity(Minecraft.thePlayer.get());

        if (player.isCollidedHorizontally()) {
            if (player.isOnGround()) {
                ticks = 0;
                if (useTimer.getValue())
                    Reflector.MINECRAFT.setTimerSpeed(timerSpeed.getValue());
                player.setMotionY(0.42);
            }
        }

        if (ticks > 6) {
            player.setMotionY(0);
        }

        if (ticks > timerTicks.getValue()) {

            if (useTimer.getValue())
                Reflector.MINECRAFT.setTimerSpeed(1);

            ticks = -1;
        }



        if (ticks >= 0) {
            ticks++;
        }
    }
}
