package pl.janek49.iniektor.client.clickgui;

import net.minecraft.client.renderer.entity.Render;
import org.lwjgl.input.Mouse;
import pl.janek49.iniektor.client.gui.RenderUtil;
import pl.janek49.iniektor.client.hook.Reflector;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

public class ClickPanel extends ClickComponent {

    public String title;

    private boolean doDrag = false;
    private int dragX = 0;
    private int dragY = 0;

    public List<ClickComponent> children = new ArrayList<>();

    public ClickPanel(String title) {
        this.title = title;
    }

    public ClickPanel(String title, int x, int y, int w, int h) {
        this.title = title;
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.bounds = new Rectangle(x, y, width, height);
    }

    @Override
    public void render(int mouseX, int mouseY, int screenW, int screenH) {
        boolean isDown = Mouse.isButtonDown(0);

        if (isDown && !doDrag && bounds.contains(mouseX, mouseY)) {
            dragX = (mouseX - bounds.x);
            dragY = (mouseY - bounds.y);
            doDrag = dragY < 15;
        } else if (!isDown)
            doDrag = false;

        if (doDrag) {
            x = mouseX - dragX;
            y = mouseY - dragY;
            bounds = new Rectangle(x, y, width, height);
        }

        RenderUtil.drawBorderedRect(x, y, x + width, y + height, doDrag ? 0XFFAABBCC : 0xFF222222, 0xFF111111);
        RenderUtil.drawString(Reflector.MC.fontRenderer, title, x + (width / 2 - Reflector.MC.fontRenderer.getStringWidth(title) / 2), y + 3, 0xFFFFFF);

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
        return 15 + this.y + y;
    }
}
