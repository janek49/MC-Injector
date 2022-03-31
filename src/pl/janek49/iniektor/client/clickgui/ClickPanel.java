package pl.janek49.iniektor.client.clickgui;

import org.lwjgl.input.Mouse;
import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.client.gui.RenderUtil;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class ClickPanel extends ClickComponent {

    public String title;

    private boolean doDrag = false;
    private int dragX = 0;
    private int dragY = 0;

    public int headerSize = 15;

    public List<ClickComponent> children = new ArrayList<>();

    public ClickPanel(String title) {
        this.title = title;
    }

    public GuiScreenClickGui parentGui;

    public ClickPanel(String title, int x, int y, int w, int h) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.bounds = new Rectangle(x, y, width, height);
    }

    public boolean handleMouseClick(int mouseX, int mouseY, boolean wasHandled) {
        if (wasHandled)
            return true;

        boolean inArea = bounds.contains(mouseX, mouseY);
        if (parentGui != null && (parentGui.draggedPanel == null || parentGui.draggedPanel == this)) {
            boolean isDown = Mouse.isButtonDown(0);

            if (isDown && !doDrag && inArea) {
                dragX = (mouseX - bounds.x);
                dragY = (mouseY - bounds.y);
                doDrag = dragY < headerSize;

                if (doDrag) {
                    parentGui.draggedPanel = this;
                }

            } else if (!isDown) {
                parentGui.draggedPanel = null;
                doDrag = false;
            }

            if (doDrag) {
                x = mouseX - dragX;
                y = mouseY - dragY;
                bounds = new Rectangle(x, y, width, height);
            }
        }

        for (ClickComponent cc : children) {
            if (cc.handleMouseClick(mouseX, mouseY, wasHandled))
                return true;
        }


        return inArea;
    }


    @Override
    public void render(int mouseX, int mouseY, int screenW, int screenH) {


        RenderUtil.drawBorderedRect(x, y, x + width, y + height, doDrag ? 0XFFAABBCC : 0xFF222222, 0xFF111111);
        RenderUtil.drawString(RenderUtil.getFontrenderer(), title, x + (width / 2 - RenderUtil.getFontrenderer().getStringWidth(title) / 2), y + 3, 0xFFFFFF);

        for (ClickComponent cp : children) {
            cp.render(mouseX, mouseY, screenW, screenH);
        }
    }

    public int getContentHeight() {
        int h = 0;
        for (ClickComponent cc : children) {
            h += cc.height + 2;
        }
        return h;
    }

    public int translateX(int x) {
        return this.x + x;
    }

    public int translateY(int y) {
        return headerSize + this.y + y;
    }

    public void wrapHeight() {
        setBounds(new Rectangle(x, y, width, headerSize + getContentHeight()));
    }
}
