package pl.janek49.iniektor.client.modules.impl;

import net.minecraft.client.Minecraft;
import org.lwjgl.input.Keyboard;
import pl.janek49.iniektor.client.clickgui.GuiScreenClickGui;
import pl.janek49.iniektor.client.modules.Module;

public class ClickGui extends Module {
    public ClickGui() {
        super("ClickGui", Keyboard.KEY_RSHIFT, Category.MISC);
    }

    @Override
    public void onEnable() {
        Minecraft.getMinecraft().displayGuiScreen(new GuiScreenClickGui());
        isEnabled = false;
    }

    @Override
    public void onDisable() {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }
}
