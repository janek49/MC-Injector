package pl.janek49.iniektor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import pl.janek49.iniektor.api.Reflector;
import pl.janek49.iniektor.client.IniektorClient;

public class FlatGuiButton {

    public int posX, posY, width, height;
    public boolean enabled = true;
    public String text;
    public int id;
    public UnicodeFontRenderer fontRenderer;

    public FlatGuiButton(int buttonId, int x, int y, String buttonText) {
        this(buttonId, x, y, 200, 20, buttonText);
    }

    public FlatGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText) {
        posX = x;
        posY = y;
        width = widthIn;
        height = heightIn;
        text = buttonText;
        id = buttonId;
        fontRenderer = IniektorClient.INSTANCE.guiManager.getDefaultFont();
    }

    public void renderButton(Minecraft mc, int mouseX, int mouseY) {
        FontRenderer fontrenderer = Reflector.MINECRAFT.fontRenderer;
        boolean hovered = mouseX >= posX && mouseY >= posY && mouseX < posX + width && mouseY < posY + height;

        int j = 0xCC222222;
        if (!enabled) {
            j = 0xCCAAAAAA;
        } else if (hovered) {
            j = 0xAA222222;
        }
        RenderUtil.drawRect(posX, posY, posX + width, posY + height, j);
        RenderUtil.drawCenteredString(fontrenderer, text, posX + width / 2, posY + (height - 8) / 2, 0xDDDDDD);
    }

    public boolean mousePressed(Minecraft mc, int mouseX, int mouseY) {
        return this.enabled && mouseX >= this.posX && mouseY >= this.posY && mouseX < this.posX + this.width && mouseY < this.posY + this.height;
    }

    public boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= posX && mouseY >= posY && mouseX < posX + width && mouseY < posY + height;
    }

    public int getId() {
        return id;
    }
}
