package pl.janek49.iniektor.client.modules;

import pl.janek49.iniektor.api.Keys;
import pl.janek49.iniektor.api.client.EntityPlayerSP;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.api.WrapperMinecraft;

public abstract class Module implements EventHandler {

    public enum Category{
        MOVEMENT, RENDER, COMBAT, WORLD, MISC
    }

    public String name;
    public Keys keyBind;
    public Category category;

    public boolean isEnabled;

    public Module(String name, Keys defKeyBind, Category category) {
        this.name = name;
        this.keyBind = defKeyBind;
        this.category = category;
    }

    public void onEnable() {
    }

    public void onDisable() {
    }

    protected void RegisterEvent(Class<? extends IEvent> eventClass) {
        IniektorClient.INSTANCE.eventManager.registerHandler(eventClass, this);
    }

    protected EntityPlayerSP getPlayer(){
        return new EntityPlayerSP(Minecraft.thePlayer.get());
    }

    protected WrapperMinecraft getMinecraft(){
        return Reflector.MINECRAFT;
    }

    @Override
    public void onEvent(IEvent event) {

    }
}
