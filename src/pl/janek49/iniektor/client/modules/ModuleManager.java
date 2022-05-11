package pl.janek49.iniektor.client.modules;

import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.api.gui.FontRenderer;
import pl.janek49.iniektor.api.reflection.MinimumVersion;
import pl.janek49.iniektor.api.reflection.Reflector;
import pl.janek49.iniektor.api.wrapper.WrapperChat;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.impl.*;

import java.util.ArrayList;
import java.util.List;

public class ModuleManager implements EventHandler {
    public List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        IniektorClient.INSTANCE.eventManager.registerHandler(EventGameTick.class, this);

        registerModule(new VanillaFly());
        registerModule(new Jetpack());
        registerModule(new Speed());
        registerModule(new Cocaine());
        registerModule(new Fullbright());
        registerModule(new Jesus());
        registerModule(new LSD());
        registerModule(new Glide());
        registerModule(new ClickGui());
        registerModule(new Hud());
        registerModule(new AutoSprint());
        registerModule(new Zoom());
        registerModule(new NoFall());
        registerModule(new Step());
        registerModule(new AntiKnockback());
        registerModule(new FastUse());
        registerModule(new AntiPotion());

        modules.sort((o1, o2) -> Integer.compare(FontRenderer.getStringWidth(o2.name), FontRenderer.getStringWidth(o1.name)));

        for (Module m : modules) {
            IniektorClient.INSTANCE.configManager.registerProperties(m);
        }
    }

    private void registerModule(Module m) {
        MinimumVersion mv = m.getClass().getAnnotation(MinimumVersion.class);
        if (mv != null && !Reflector.isOnOrAbvVersion(mv.version())) {
            Logger.warn("Module '" + m.name + "' requires MCP version: " + mv.version() + ", but client running on: " + Reflector.MCP_VERSION);
            return;
        }
        modules.add(m);
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            for (Module m : modules) {
                if (IniektorClient.INSTANCE.keyboardHandler.isKeyPressed(m.keyBind)) {
                    toggle(m);
                }
            }
        }
    }

    public void toggle(Module m) {
        m.isEnabled = !m.isEnabled;
        if (m.isEnabled)
            m.onEnable();
        else
            m.onDisable();
    }

    public void setEnabled(Module m, boolean state) {
        if (state)
            m.onEnable();
        else
            m.onDisable();
        m.isEnabled = state;
    }

    public Module getModuleByName(String s) {
        for (Module m : modules) {
            if (m.name.equalsIgnoreCase(s))
                return m;
        }
        return null;
    }

    public void processChatCommand(String text) {
        if (!text.startsWith(".")) {
            return;
        }

        String[] command = text.substring(1).split(" ");
        Module m = getModuleByName(command[0]);

        if (m == null) {
            WrapperChat.showChatMessage("Module '" + command[0] + "' not found.");
            return;
        }

        if (command.length == 1) {
            if (IniektorClient.INSTANCE.configManager.properties.get(m) == null) {
                WrapperChat.showChatMessage("Module '" + m.name + "' has no configurable options.");
                return;
            }

            for (Property pt : IniektorClient.INSTANCE.configManager.properties.get(m)) {
                WrapperChat.showChatMessage(m.name + ": §e" + pt.propertyName + "§r - " + pt.description);
            }
            return;
        }

        IniektorClient.INSTANCE.configManager.processChatCommand(m, command);
    }


}
