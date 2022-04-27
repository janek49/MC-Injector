package pl.janek49.iniektor.client.modules.impl;

import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.api.network.WrapperPacket;
import pl.janek49.iniektor.api.network.WrapperSPacketVelocity;
import pl.janek49.iniektor.client.config.RangeProperty;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventPacketReceived;
import pl.janek49.iniektor.client.modules.Module;

public class AntiKnockback extends Module {
    public AntiKnockback() {
        super("AntiKnockback", Keyboard.KEY_NONE, Category.COMBAT);
        RegisterEvent(EventPacketReceived.class);
    }

    public RangeProperty valueH = new RangeProperty("horizontal", 1, 0, 2, "Horizontal % of knockback");
    public RangeProperty valueV = new RangeProperty("vertical", 1, 0, 2, "Vertical % of knockback");

    @Override
    public void onEvent(IEvent event) {
        EventPacketReceived epr = (EventPacketReceived) event;
        if (epr.packet.getClass() == WrapperSPacketVelocity.target.javaClass) {
            int myId = getPlayer().getEntityID();

            if (WrapperSPacketVelocity.entityID.getInt(epr.packet) == myId) {
                epr.cancel = true;

                double motionX = (double) WrapperSPacketVelocity.motionX.getInt(epr.packet) / 8000d;
                double motionY = (double) WrapperSPacketVelocity.motionY.getInt(epr.packet) / 8000d;
                double motionZ = (double) WrapperSPacketVelocity.motionZ.getInt(epr.packet) / 8000d;

                motionX *= valueH.getValue();
                motionY *= valueV.getValue();
                motionZ *= valueH.getValue();

                Object newPacket = WrapperSPacketVelocity.defaultConstructor.newInstance(myId, motionX, motionY, motionZ);
                WrapperPacket.fakeReceivePacket(newPacket);
            }

        }

    }
}
