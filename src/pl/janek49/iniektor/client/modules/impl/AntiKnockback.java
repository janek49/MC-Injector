package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import net.minecraft.network.play.server.S12PacketEntityVelocity;
import org.lwjgl.input.Keyboard;
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
        if (epr.packet instanceof S12PacketEntityVelocity) {
            S12PacketEntityVelocity s12 = (S12PacketEntityVelocity) epr.packet;
            if (s12.getEntityID() == getPlayerObj().getEntityId()) {
                epr.cancel = true;
                try {
                    S12PacketEntityVelocity newpacket = new S12PacketEntityVelocity
                            (s12.getEntityID(),
                                    (s12.getMotionX() / 8000f) * valueH.getValue(),
                                    (s12.getMotionY() / 8000f) * valueV.getValue(),
                                    (s12.getMotionZ() / 8000f) * valueH.getValue());

                    Minecraft.getMinecraft().getNetHandler().handleEntityVelocity(newpacket);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }
}
