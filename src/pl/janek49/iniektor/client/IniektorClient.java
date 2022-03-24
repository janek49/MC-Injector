package pl.janek49.iniektor.client;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.config.ConfigManager;
import pl.janek49.iniektor.client.events.EventHandler;
import pl.janek49.iniektor.client.events.EventManager;
import pl.janek49.iniektor.client.events.IEvent;
import pl.janek49.iniektor.client.events.impl.EventGameTick;
import pl.janek49.iniektor.client.events.impl.EventRender2D;
import pl.janek49.iniektor.client.gui.GuiManager;
import pl.janek49.iniektor.client.gui.KeyboardHandler;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.modules.ModuleManager;

public class IniektorClient implements EventHandler {
    public static IniektorClient INSTANCE;

    public EventManager eventManager;
    public GuiManager guiManager;
    public KeyboardHandler keyboardHandler;
    public Reflector reflector;
    public ModuleManager moduleManager;
    public ConfigManager configManager;

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
        eventManager.registerHandler(EventGameTick.class, this);
    }

    @Override
    public void onEvent(IEvent event) {
        if (keyboardHandler.isKeyPressed(Keyboard.KEY_F12)) {
            Reflector.TRIGGER_HOTSWAP = true;
            IniektorUtil.showChatMessage("DEV: HotSwapper triggered.");
        }
    }
}
