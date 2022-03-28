package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class AutoSprint extends Module {
    public AutoSprint() {
        super("AutoSprint", Keyboard.KEY_NONE, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        getPlayerObj().setSprinting(true);
    }
}
