package pl.janek49.iniektor.client.clickgui;

import java.awt.*;

public abstract class ClickComponent {
    public int x, y, width, height;
    public Rectangle bounds;

    public int defSpacing = 2;

    public abstract void render(int mouseX, int mouseY, int screenW, int screenH);

    public void setBounds(Rectangle rect) {
        this.bounds = rect;
        this.x = rect.x;
        this.y = rect.y;
        this.width = rect.width;
        this.height = rect.height;
    }

    public void setLocation(int x, int y) {
        setBounds(new Rectangle(x, y, this.width, this.height));
    }
}
