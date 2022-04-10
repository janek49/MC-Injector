package pl.janek49.iniektor.client.clickgui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import pl.janek49.iniektor.client.gui.RenderUtil;
import pl.janek49.iniektor.api.Reflector;

import java.awt.*;

public class ClickButton extends ClickComponent {

    interface ActionHandler {
        public void onClick(ClickButton btn, int mouseX, int mouseY);
    }

    protected ActionHandler handler;
    public String caption;
    protected boolean wasClicked = false;

    public ClickButton(ClickPanel parent, String text, ActionHandler actionHandler) {
        this.parent = parent;
        this.handler = actionHandler;
        this.caption = text;
        setBounds(new Rectangle(defSpacing, parent.getContentHeight(), parent.width - defSpacing * 2, 13));
    }

    @Override
    public boolean handleMouseClick(int mouseX, int mouseY, boolean wasHandled) {
        if (wasHandled)
            return true;

        Rectangle tr = new Rectangle(bounds);
        tr.setLocation(parent.translateX(x), parent.translateY(y));
        isHover = tr.contains(mouseX, mouseY);
        isClicked = isHover && Mouse.isButtonDown(0);

        if (isClicked && !wasClicked) {
            wasClicked = true;
            if (handler != null)
                handler.onClick(this, mouseX, mouseY);
            wasHandled = true;
        } else if (!isClicked)
            wasClicked = false;

        return wasHandled;
    }

    @Override
    public void render(int mouseX, int mouseY, int screenW, int screenH) {
        Rectangle tr = new Rectangle(bounds);
        tr.setLocation(parent.translateX(x), parent.translateY(y));

        // boolean isHover = tr.contains(mouseX, mouseY);
        //  boolean isClicked = isHover && Mouse.isButtonDown(0);

        int color1 = 0, color2 = 0;

        if (isClicked) {
            color1 = 0xAA0047AB;
            color2 = 0xAA00008B;
        } else if (isHover) {
            color1 = 0xAAAAAAAA;
            color2 = 0xAAAAAAAA;
        } else {
            color1 = 0xAA777777;
            color2 = 0xAA777777;
        }

        RenderUtil.drawGradientRect(parent.translateX(x), parent.translateY(y), parent.translateX(x + width), parent.translateY(y + height), color1, color2);

        GL11.glColor4f(0.9f, 0.9f, 0.9f, 1);
        RenderUtil.drawCenteredString(Reflector.MINECRAFT.fontRenderer, caption, parent.x + (parent.width / 2), parent.translateY(y + 3), 0xFFFFFF);
    }
}
