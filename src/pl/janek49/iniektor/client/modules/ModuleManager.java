package pl.janek49.iniektor.client.modules;

import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.IniektorUtil;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.modules.impl.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModuleManager implements EventHandler {
    public List<Module> modules = new ArrayList<>();

    public ModuleManager() {
        modules.add(new VanillaFly());
        modules.add(new Jetpack());
        modules.add(new Speed());
        modules.add(new Cocaine());
        modules.add(new Fullbright());
        modules.add(new Jesus());
        modules.add(new LSD());

        modules.sort(new Comparator<Module>() {
            @Override
            public int compare(Module o1, Module o2) {
                return Integer.compare(o2.name.length(), o1.name.length());
            }
        });

        for (Module m : modules) {
            IniektorClient.INSTANCE.configManager.registerProperties(m);
        }
    }

    @Override
    public void onEvent(IEvent event) {
        if (event instanceof EventGameTick) {
            for (Module m : modules) {
                if (IniektorClient.INSTANCE.keyboardHandler.isKeyPressed(m.keyBind)) {
                    m.isEnabled = !m.isEnabled;
                    if (m.isEnabled)
                        m.onEnable();
                    else
                        m.onDisable();
                }
            }
        }
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
            IniektorUtil.showChatMessage("Module '" + command[0] + "' not found.");
            return;
        }

        if (command.length == 1) {
            for (Property pt : IniektorClient.INSTANCE.configManager.properties.get(m)) {
                IniektorUtil.showChatMessage(m.name + ": §e" + pt.propertyName + "§r - " + pt.description);
            }
            return;
        }

        IniektorClient.INSTANCE.configManager.processChatCommand(m, command);
    }


}
