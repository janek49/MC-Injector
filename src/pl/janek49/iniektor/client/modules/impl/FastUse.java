package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.patcher.PatchTarget;
import pl.janek49.iniektor.api.Keys;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.api.client.EntityPlayerSP;
import pl.janek49.iniektor.api.network.CPacketPlayer;
import pl.janek49.iniektor.api.network.PacketHelper;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.Module;

public class FastUse extends Module {
    public RangeProperty waitTicks = new RangeProperty("ticks", 5, 0, 20, "Ticks between packets");

    public FastUse() {
        super("FastUse", Keys.KEY_NONE, Category.MISC);
        RegisterEvent(EventGameTick.class);
    }

    int ticks = 0;

    @Override
    public void onEvent(IEvent event) {
        EntityPlayerSP pl = getPlayer();

        if (pl.isUsingItem()) {
            if (ticks >= waitTicks.getValue()) {

                if (Reflector.isOnOrAbvVersion(Version.MC1_9_4)) {
                    for (int i = 0; i < (pl.isMoving() ? 1 : 3); i++) {
                        PacketHelper.sendPacket(new CPacketPlayer(pl.isOnGround()));
                    }
                }

                PacketHelper.sendPacket(new CPacketPlayer(pl.isOnGround()));
                ticks = 0;
            } else {
                ticks++;
            }
        } else {
            ticks = 0;
        }
    }
}
