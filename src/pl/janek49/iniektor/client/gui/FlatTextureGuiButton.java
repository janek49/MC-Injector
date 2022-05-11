package pl.janek49.iniektor.client.gui;

import org.lwjgl.opengl.GL11;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.gui.DynamicTexture;

public class FlatTextureGuiButton extends FlatGuiButton {

    public DynamicTexture texture;

    public FlatTextureGuiButton(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, DynamicTexture image) {
        super(buttonId, x, y, widthIn, heightIn, buttonText);

        texture = image;
    }


    public void renderButton(int mouseX, int mouseY) {
        boolean hovered = isHovered(mouseX, mouseY);

        int j = 0xCC222222;
        if (!enabled) {
            j = 0xCCAAAAAA;
        } else if (hovered) {
            j = 0xAA222222;
        }
        RenderUtil.drawRect(posX, posY, posX + width, posY + height, j);

        FontUtil.drawCenteredString(fontRenderer, text, posX + (width / 2), posY + (height - 18), 0xDDDDDD);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);

        int marginTop = 8;
        int marginSides = 30;

        float scale = (float) (width - marginSides) / 256f;

        GL11.glPushMatrix();
        GL11.glColor4f(1f, 1f, 1f, 1f);
        GL11.glScalef(scale, scale, scale);


        Minecraft.getTextureManager().bindTexture(Minecraft.getTextureManager().getDynamicTextureLocation("test", texture));
        RenderUtil.drawTexturedModalRect((posX + (marginSides / 2)) / scale, (posY + marginTop) / scale, 0, 0, 256, 256);

        GL11.glPopMatrix();
        GL11.glDisable(GL11.GL_BLEND);
    }

}
