package pl.janek49.iniektor.client.modules;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.hook.Reflector;

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

    protected Entity getPlayer() {
        return Reflector.PLAYER.getPlayerObj();
    }

    @Override
    public void onEvent(IEvent event) {

    }
}
