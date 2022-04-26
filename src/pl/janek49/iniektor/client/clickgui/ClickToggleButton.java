package pl.janek49.iniektor.client.clickgui;

import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;
import pl.janek49.iniektor.api.WrapperMisc;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.gui.FontUtil;
import pl.janek49.iniektor.client.gui.RenderUtil;

import java.awt.*;

public class ClickToggleButton extends ClickButton {
    public boolean toggled = false;

    public ClickPanel configPanel;
    public boolean showConfigPanel;

    public boolean wasRightClicked;

    public ClickToggleButton(ClickPanel parent, String text, ActionHandler actionHandler) {
        super(parent, text, actionHandler);
    }

    public void playSound() {
        WrapperMisc.playPressSound();
    }

    @Override
    public boolean handleMouseClick(int mouseX, int mouseY, boolean wasHandled) {
        if (wasHandled)
            return true;

        Rectangle tr = new Rectangle(bounds);
        tr.setLocation(parent.translateX(x), parent.translateY(y));

        isHover = (parent.parentGui == null || parent.parentGui.draggedPanel == null) && tr.contains(mouseX, mouseY);
        isClicked = isHover && Mouse.isButtonDown(0);
        boolean isRightClicked = isHover && Mouse.isButtonDown(1);

        if (parent == null || parent.parentGui == null || parent.parentGui.draggedPanel != parent) {
            if (isClicked && !wasClicked) {
                wasClicked = true;
                toggled = !toggled;
                if (handler != null) {
                    handler.onClick(this, mouseX, mouseY);
                    WrapperMisc.playPressSound();
                }
                wasHandled = true;
            } else if (!isClicked)
                wasClicked = false;


            if (isRightClicked && !wasRightClicked) {
                wasRightClicked = true;
                if (configPanel != null) {
                    showConfigPanel = !showConfigPanel;
                    WrapperMisc.playPressSound();
                }
                wasHandled = true;
            } else if (!isRightClicked)
                wasRightClicked = false;
        }

        if (configPanel != null && showConfigPanel)
            wasHandled = configPanel.handleMouseClick(mouseX, mouseY, wasHandled);


        return wasHandled;
    }

    public void render(int mouseX, int mouseY, int screenW, int screenH) {
        int color1 = 0;

        Rectangle tr = new Rectangle(bounds);
        tr.setLocation(parent.translateX(x), parent.translateY(y));

        boolean isHover = this.isHover || this.showConfigPanel;

        if (isClicked) {
            color1 = 0x880047AB;
        } else if (toggled) {
            if (isHover) {
                color1 = 0xAA3380DF;
            } else {
                color1 = 0xAA1158CD;
            }
        } else if (isHover) {
            color1 = 0xAAAAAAAA;
        } else {
            color1 = 0xAA444444;
        }

        RenderUtil.drawRect(parent.translateX(x), parent.translateY(y), parent.translateX(x + width), parent.translateY(y + height), color1);

        GL11.glColor4f(0.9f, 0.9f, 0.9f, 1);

        FontUtil.drawString(IniektorClient.INSTANCE.guiManager.getClickGuiFont(), caption, parent.x + x + 2, parent.translateY(y) + 1, 0xFFFFFF);

        if (configPanel != null) {
            if (showConfigPanel) {
                configPanel.setLocation(parent.translateX(x + width + defSpacing), parent.translateY(y));
                configPanel.render(mouseX, mouseY, screenW, screenH);
            }

            FontUtil.drawString(IniektorClient.INSTANCE.guiManager.getFont(8), showConfigPanel ? "<" : ">", parent.x + width - 7, parent.translateY(y) + 2, 0xFFFFFF);
        }
    }
}
