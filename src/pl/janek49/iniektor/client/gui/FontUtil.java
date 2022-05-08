package pl.janek49.iniektor.client.gui;

import org.lwjgl.opengl.GL11;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.reflection.Reflector;
import pl.janek49.iniektor.api.gui.FontRenderer;
import pl.janek49.iniektor.client.IniektorClient;

public class FontUtil {

    public static void drawString(UnicodeFontRenderer ufr, String text, float x, float y, int color) {
        if(Reflector.isOnOrAbvVersion(Version.MC1_14_4))
        {
            FontRenderer.drawString(text, x, y, color, true);
            return;
        }

        float fscale = IniektorClient.INSTANCE.guiManager.getFontScale();
        GL11.glPushMatrix();
        GL11.glScalef(fscale, fscale, fscale);
        ufr.drawString(text, x / fscale, y / fscale, color);
        GL11.glPopMatrix();
    }

    public static void drawCenteredString(UnicodeFontRenderer ufr, String text, float x, float y, int color) {
        if(Reflector.isOnOrAbvVersion(Version.MC1_14_4))
        {
            FontRenderer.drawCenteredString(text, x, y, color, true);
            return;
        }


        float fscale = IniektorClient.INSTANCE.guiManager.getFontScale();
        GL11.glPushMatrix();
        GL11.glScalef(fscale, fscale, fscale);
        ufr.drawString(text, ((x / fscale) - ufr.getStringWidth(text) / 2), y / fscale, color);
        GL11.glPopMatrix();

    }
}
