package pl.janek49.iniektor.client.gui;

import org.newdawn.slick.*;
import org.newdawn.slick.font.effects.ColorEffect;

import static org.lwjgl.opengl.GL11.*;

public class UnicodeFontRenderer {

    private final UnicodeFont font;
    public int FONT_HEIGHT;

    public UnicodeFontRenderer(java.awt.Font awtFont) {

        font = new UnicodeFont(awtFont);
        font.addAsciiGlyphs();
        font.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        try {
            font.loadGlyphs();
        } catch (SlickException exception) {
            throw new RuntimeException(exception);
        }
        String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
        FONT_HEIGHT = font.getHeight(alphabet) / 2;
    }

    public int drawString(String string, int x, int y, int color, boolean shadow) {
        if (string == null)
            return 0;
        // glClear(256);
        // glMatrixMode(GL_PROJECTION);
        // glLoadIdentity();
        // IntBuffer buffer = BufferUtils.createIntBuffer(16);
        // glGetInteger(GL_VIEWPORT, buffer);
        // glOrtho(0, buffer.get(2), buffer.get(3), 0, 1000, 3000);
        // glMatrixMode(GL_MODELVIEW);
        // glLoadIdentity();
        // glTranslatef(0, 0, -2000);
        glPushMatrix();
        glScaled(0.5, 0.5, 0.5);

        boolean blend = glIsEnabled(GL_BLEND);
        boolean lighting = glIsEnabled(GL_LIGHTING);
        boolean texture = glIsEnabled(GL_TEXTURE_2D);
        if (!blend)
            glEnable(GL_BLEND);
        if (lighting)
            glDisable(GL_LIGHTING);
        if (texture)
            glDisable(GL_TEXTURE_2D);
        x *= 2;
        y *= 2;
        // glBegin(GL_LINES);
        // glVertex3d(x, y, 0);
        // glVertex3d(x + getStringWidth(string), y + FONT_HEIGHT, 0);
        // glEnd();

        if (shadow)
            font.drawString(x + 4, y + 3, string, new org.newdawn.slick.Color(color).darker());
        font.drawString(x, y, string, new org.newdawn.slick.Color(color));

        if (texture)
            glEnable(GL_TEXTURE_2D);
        if (lighting)
            glEnable(GL_LIGHTING);
        if (!blend)
            glDisable(GL_BLEND);
        glPopMatrix();
        return x;
    }

    public int drawString(String string, int x, int y, int color) {
        return drawString(string, x, y, color, false);
    }

    public int drawString(String string, float x, float y, int color) {
        return drawString(string, (int) x, (int) y, color);
    }

    public int getCharWidth(char c) {
        return getStringWidth(Character.toString(c));
    }

    public int getStringWidth(String string) {
        return font.getWidth(string) / 2;
    }

    public int getStringHeight(String string) {
        return font.getHeight(string) / 2;
    }
}