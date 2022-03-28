package pl.janek49.iniektor.client.clickgui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import pl.janek49.iniektor.client.gui.RenderUtil;
import pl.janek49.iniektor.client.hook.Reflector;

import java.awt.*;

public class ClickToggleButton extends ClickButton {
    public boolean toggled = false;

    public ClickToggleButton(ClickPanel parent, String text, ActionHandler actionHandler) {
        super(parent, text, actionHandler);
    }

    public void render(int mouseX, int mouseY, int screenW, int screenH) {
        Rectangle tr = new Rectangle(bounds);
        tr.setLocation(parent.translateX(x), parent.translateY(y));

        boolean isHover = tr.contains(mouseX, mouseY);
        boolean isClicked = isHover && Mouse.isButtonDown(0);

        if (isClicked && !wasClicked) {
            wasClicked = true;
            toggled = !toggled;
            if (handler != null)
                handler.onClick(this, mouseX, mouseY);
        } else if (!isClicked)
            wasClicked = false;

        int color1 = 0, color2 = 0;

        if (isClicked) {
            color1 = 0x880047AB;
            color2 = 0x8800008B;
        } else if (toggled) {
            if (isHover) {
                color1 = 0xAA2269CD;
                color2 = 0xAA22229D;
            } else {
                color1 = 0xAA0047AB;
                color2 = 0xAA00008B;
            }
        } else if (isHover) {
            color1 = 0xAAAAAAAA;
            color2 = 0xAAAAAAAA;
        } else {
            color1 = 0xAA777777;
            color2 = 0xAA777777;
        }

        RenderUtil.drawGradientRect(parent.translateX(x), parent.translateY(y), parent.translateX(x + width), parent.translateY(y + height), color1, color2);

        GL11.glColor4f(0.9f, 0.9f, 0.9f, 1);
        RenderUtil.drawCenteredString(Reflector.MC.fontRenderer, caption, parent.x + (parent.width / 2), parent.translateY(y + 3), 0xFFFFFF);
    }
}
