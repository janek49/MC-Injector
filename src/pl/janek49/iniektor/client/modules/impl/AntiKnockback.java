package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.api.Keys;
import pl.janek49.iniektor.api.network.PacketHelper;
import pl.janek49.iniektor.api.network.SPacketEntityVelocity;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventPacketReceived;
import pl.janek49.iniektor.client.modules.Module;

public class AntiKnockback extends Module {
    public AntiKnockback() {
        super("AntiKnockback", Keys.KEY_NONE, Category.COMBAT);
        RegisterEvent(EventPacketReceived.class);
    }

    public RangeProperty valueH = new RangeProperty("horizontal", 1, 0, 2, "Horizontal % of knockback");
    public RangeProperty valueV = new RangeProperty("vertical", 1, 0, 2, "Vertical % of knockback");

    @Override
    public void onEvent(IEvent event) {
        EventPacketReceived epr = (EventPacketReceived) event;
        if (epr.packet.getClass() == SPacketEntityVelocity.target.javaClass) {
            if(getPlayer()==null)return;

            int myId = getPlayer().getEntityID();

            if (SPacketEntityVelocity.entityID.getInt(epr.packet) == myId) {
                epr.cancel = true;

                double motionX = (double) SPacketEntityVelocity.motionX.getInt(epr.packet) / 8000d;
                double motionY = (double) SPacketEntityVelocity.motionY.getInt(epr.packet) / 8000d;
                double motionZ = (double) SPacketEntityVelocity.motionZ.getInt(epr.packet) / 8000d;

                motionX *= valueH.getValue();
                motionY *= valueV.getValue();
                motionZ *= valueH.getValue();

                getPlayer().setDeltaMovement(motionX, motionY, motionZ);
            }

        }

    }
}
