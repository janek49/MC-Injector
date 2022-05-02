package pl.janek49.iniektor.api.gui;

import org.lwjgl.opengl.GL11;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;
import pl.janek49.iniektor.api.client.Minecraft;

@ClassImitator.ResolveClass(version = Version.MC1_14_4, andAbove = true, value = "net/minecraft/client/gui/Font")
@ClassImitator.ResolveClass(version = Version.DEFAULT, value = "net/minecraft/client/gui/FontRenderer")
public class FontRenderer extends ClassImitator {

    public static ClassInformation target;

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "drawInternal", descriptor = "(Ljava/lang/String;FFIZ)I")
    @ResolveMethod(version = Version.MC1_8_8, andAbove = true, name = "drawString", descriptor = "(Ljava/lang/String;FFIZ)I")
    @ResolveMethod(name = "drawString", descriptor = "(Ljava/lang/String;IIIZ)I")
    public static MethodDefinition _drawString;

    @ResolveMethod(version = Version.MC1_14_4, andAbove = true, name = "width", descriptor = "(Ljava/lang/String;)I")
    @ResolveMethod(name = "getStringWidth", descriptor = "(Ljava/lang/String;)I")
    public static MethodDefinition _getStringWidth;

    @ResolveField(version = Version.MC1_14_4, andAbove = true, value = "lineHeight")
    @ResolveField(value = "FONT_HEIGHT")
    public static FieldDefinition FONT_HEIGHT;

    public static int drawString(String text, float x, float y, int color, boolean shadow) {
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        return (int) _drawString.call(text, (int)x,(int) y, color, shadow);
    }

    public static int drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }

    public static int drawStringWithShadow(String text, float x, float y, int color) {
        return drawString(text, x, y, color, true);
    }

    public static void drawCenteredString(String text, float x, float y, int color, boolean shadow) {
        drawString(text, (float)(x - getStringWidth(text) / 2), (float)y, color, shadow);
    }

    public static void drawCenteredStringWithShadow(String text, float x, float y, int color) {
        drawCenteredString(text, x, y, color, true);
    }

    public static void drawCenteredString(String text, float x, float y, int color) {
        drawCenteredString(text, x, y, color, false);
    }

    public static int getStringWidth(String text) {
        return (int) _getStringWidth.call(text);
    }

    public static int getFontHeight(){
        return FONT_HEIGHT.get();
    }

    @Override
    public Object getInstanceBehind() {
        return Minecraft.fontRenderer.get();
    }
}
