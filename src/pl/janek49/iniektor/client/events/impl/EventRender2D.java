package pl.janek49.iniektor.client.events.impl;

import pl.janek49.iniektor.client.events.IEvent;

public class EventRender2D extends IEvent {
    private Object gui;

    public EventRender2D(Object gui){
        this.gui = gui;
    }

    public Object getGui() {
        return gui;
    }
}
