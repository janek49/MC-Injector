package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.api.network.WrapperPacket;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class FastUse extends Module {
    public RangeProperty waitTicks = new RangeProperty("ticks", 5, 0, 20, "Ticks between packets");

    public FastUse() {
        super("FastUse", Keyboard.KEY_NONE, Category.MISC);
        RegisterEvent(EventGameTick.class);
    }

    int ticks = 0;

    @Override
    public void onEvent(IEvent event) {
        if (getPlayer().isUsingItem()) {
            if (ticks >= waitTicks.getValue()) {
                WrapperPacket.sendPacket(WrapperPacket.CPacketPlayer.newInstance(getPlayer().isOnGround()));
                ticks = 0;
            } else {
                ticks++;
            }
        } else {
            ticks = 0;
        }
    }
}
