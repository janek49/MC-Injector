package pl.janek49.iniektor.client.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.api.WrapperMinecraft;
import pl.janek49.iniektor.api.WrapperPlayer;

public abstract class Module implements EventHandler {

    public enum Category{
        MOVEMENT, RENDER, COMBAT, WORLD, MISC
    }

    public String name;
    public int keyBind;
    public Category category;

    public boolean isEnabled;

    public Module(String name, int defKeyBind, Category category) {
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

    protected Entity getPlayerObj() {
        return Reflector.PLAYER.thePlayer.get(Minecraft.getMinecraft());
    }

    protected WrapperPlayer getPlayer(){
        return Reflector.PLAYER;
    }

    protected WrapperMinecraft getMinecraft(){
        return Reflector.MC;
    }

    @Override
    public void onEvent(IEvent event) {

    }
}
