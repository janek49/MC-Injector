package pl.janek49.iniektor.client.modules.impl;

import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.reflection.Keys;
import pl.janek49.iniektor.client.clickgui.GuiScreenClickGui;
import pl.janek49.iniektor.client.modules.Module;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui", Keys.KEY_RSHIFT, Category.MISC);
    }

    @Override
    public void onEnable() {
        Minecraft.displayGuiScreen(new GuiScreenClickGui());
        isEnabled = false;
    }

    @Override
    public void onDisable() {
        Minecraft.displayGuiScreen(null);
    }
}
