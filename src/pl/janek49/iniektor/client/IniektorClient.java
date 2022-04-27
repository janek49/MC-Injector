package pl.janek49.iniektor.client;

import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.gui.Gui;
import pl.janek49.iniektor.api.gui.GuiMainMenu;
import pl.janek49.iniektor.client.config.ConfigManager;
import pl.janek49.iniektor.client.events.EventManager;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.gui.GuiManager;
import pl.janek49.iniektor.client.gui.GuiScreenIniektorMain;
import pl.janek49.iniektor.client.gui.KeyboardHandler;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.client.modules.ModuleManager;

public class IniektorClient {
    public static IniektorClient INSTANCE;

    public EventManager eventManager;
    public GuiManager guiManager;
    public KeyboardHandler keyboardHandler;
    public Reflector reflector;
    public ModuleManager moduleManager;
    public ConfigManager configManager;

    public boolean isInitialized;

    public Gui gui;

    public IniektorClient() {
        INSTANCE = this;
        eventManager = new EventManager();
        guiManager = new GuiManager();
        keyboardHandler = new KeyboardHandler();
        reflector = new Reflector();
        configManager = new ConfigManager();
        moduleManager = new ModuleManager();

        eventManager.registerHandler(EventRender2D.class, guiManager);
        eventManager.registerHandler(EventGameTick.class, moduleManager);

        gui = Gui.fromObj(Gui.class, Gui.constructor.newInstance());

        isInitialized = true;
    }

    public void onGameTick() {
        if (!isInitialized) return;

        if (Minecraft.currentScreen.get().getClass() == GuiMainMenu.target.javaClass) {
            Minecraft.displayGuiScreen(new GuiScreenIniektorMain());
        }
    }
}
