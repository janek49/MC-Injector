package pl.janek49.iniektor.client.events.impl;

import pl.janek49.iniektor.client.events.IEvent;

public class EventPacketReceived extends IEvent {
    public Object packet;
    public boolean cancel = false;

    public EventPacketReceived(Object packet) {
        this.packet = packet;
    }
}
