package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.api.WrapperPacket;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", Keyboard.KEY_NONE, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (getPlayerObj().fallDistance > 1f) {
            WrapperPacket.sendPacket(WrapperPacket.CPacketPlayer.newInstance(true));
            getPlayerObj().fallDistance = 0;
        }
    }
}
