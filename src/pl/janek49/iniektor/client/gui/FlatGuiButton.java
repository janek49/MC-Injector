package pl.janek49.iniektor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import pl.janek49.iniektor.api.Reflector;

public class FlatGuiButton extends GuiButton {

    public int posX, posY, width, height;
    public boolean enabled = true;

    public FlatGuiButton(int buttonId, int x, int y, String buttonText) {
        super(buttonId, x, y, buttonText);
        posX = x;
        posY = y;
        width = 200;
        height = 20;
    }

    public FlatGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);
        posX = x;
        posY = y;
        width = widthIn;
        height = heightIn;
    }

    public void renderButton(Minecraft mc, int mouseX, int mouseY) {
        FontRenderer fontrenderer = Reflector.MC.fontRenderer;
        boolean hovered = mouseX >= posX && mouseY >= posY && mouseX < posX + width && mouseY < posY + height;

        int j = 0xCC222222;
        if (!enabled) {
            j = 0xCCAAAAAA;
        } else if (hovered) {
            j = 0xAA222222;
        }
        RenderUtil.drawRect(posX, posY, posX + width, posY + height, j);
        super.mouseDragged(mc, mouseX, mouseY);
        RenderUtil.drawCenteredString(fontrenderer, super.displayString, posX + width / 2, posY + (height - 8) / 2, 0xDDDDDD);
    }

    public int getId() {
        return super.id;
    }
}
