package pl.janek49.iniektor.client;

import pl.janek49.iniektor.client.events.EventManager;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.gui.GuiManager;
import pl.janek49.iniektor.client.gui.KeyboardHandler;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.modules.ModuleManager;

public class IniektorClient {
    public static IniektorClient INSTANCE;

    public EventManager eventManager;
    public GuiManager guiManager;
    public KeyboardHandler keyboardHandler;
    public Reflector reflector;
    public ModuleManager moduleManager;


    public IniektorClient() {
        INSTANCE = this;
        eventManager = new EventManager();
        guiManager = new GuiManager();
        keyboardHandler = new KeyboardHandler();
        reflector = new Reflector();
        moduleManager = new ModuleManager();

        eventManager.registerHandler(EventRender2D.class, guiManager);
        eventManager.registerHandler(EventGameTick.class, moduleManager);
    }
}
