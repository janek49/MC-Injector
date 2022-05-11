package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.api.network.CPacketPlayer;
import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.api.wrapper.WrapperPacket;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class NoFall extends Module {
    public NoFall() {
        super("NoFall", Keys.KEY_NONE, Category.MOVEMENT);
        RegisterEvent(EventGameTick.class);
    }

    @Override
    public void onEvent(IEvent event) {
        if (getPlayer().getFallDistance() > 1f) {
            WrapperPacket.sendPacket(new CPacketPlayer(true));
        }
    }
}
