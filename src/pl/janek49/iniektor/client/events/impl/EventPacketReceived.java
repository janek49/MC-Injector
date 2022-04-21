package pl.janek49.iniektor.client.events.impl;

import net.minecraft.network.Packet;
import pl.janek49.iniektor.client.events.IEvent;

public class EventPacketReceived extends IEvent {
    public Packet packet;
    public boolean cancel = false;

    public EventPacketReceived(Packet packet) {
        this.packet = packet;
    }
}
