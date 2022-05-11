package pl.janek49.iniektor.client;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.gui.Blaze3DWindow;
import pl.janek49.iniektor.api.gui.Gui;
import pl.janek49.iniektor.api.gui.GuiMainMenu;
import pl.janek49.iniektor.api.reflection.Reflector;
import pl.janek49.iniektor.client.config.ConfigManager;
import pl.janek49.iniektor.client.events.EventManager;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.gui.GuiManager;
import pl.janek49.iniektor.client.gui.GuiScreenIniektorMain;
import pl.janek49.iniektor.client.gui.KeyboardHandler;
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
    public long windowId;

    public IniektorClient() {
        INSTANCE = this;
        eventManager = new EventManager();
        guiManager = new GuiManager();
        reflector = new Reflector();
        if (Reflector.isOnOrAbvVersion(Version.MC1_14_4)) {
            keyboardHandler = new KeyboardHandler.Mc114KeyBoardHandler();
        } else {
            keyboardHandler = new KeyboardHandler();
        }
        configManager = new ConfigManager();
        moduleManager = new ModuleManager();

        eventManager.registerHandler(EventRender2D.class, guiManager);

        gui = new Gui(Minecraft.ingameGUI.get());

        if (Reflector.USE_NEW_API)
            windowId = new Blaze3DWindow(Minecraft.window.get()).getWindowId();

        isInitialized = true;
    }

    public void onGameTick() {
        if (!isInitialized) return;

        Object cs = Minecraft.currentScreen.get();

        if (cs != null && cs.getClass() == GuiMainMenu.target.javaClass) {
            Minecraft.displayGuiScreen(new GuiScreenIniektorMain());
        }

    }
}
