package pl.janek49.iniektor.client.events.impl;

import net.minecraft.client.gui.GuiIngame;
import pl.janek49.iniektor.client.events.IEvent;

public class EventRender2D extends IEvent {
    private GuiIngame gui;

    public EventRender2D(GuiIngame gui){
        this.gui = gui;
    }

    public GuiIngame getGui() {
        return gui;
    }
}
