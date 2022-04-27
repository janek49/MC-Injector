package pl.janek49.iniektor.api.gui;

import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.api.*;
import pl.janek49.iniektor.api.client.Minecraft;

@ClassImitator.ResolveClass(version = Version.DEFAULT, name = "net/minecraft/client/gui/FontRenderer")
public class FontRenderer extends ClassImitator {

    public static ClassInformation target;

    @ResolveMethod(name = "drawString", descriptor = "(Ljava/lang/String;FFIZ)I")
    public static MethodDefinition _drawString;

    @ResolveMethod(name = "getStringWidth", descriptor = "(Ljava/lang/String;)I")
    public static MethodDefinition _getStringWidth;

    @ResolveField(name = "FONT_HEIGHT")
    public static FieldDefinition FONT_HEIGHT;

    public static int drawString(String text, float x, float y, int color, boolean shadow) {
        return (int) _drawString.call(text, x, y, color, shadow);
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
    public Object getInstance() {
        return Minecraft.fontRenderer.get();
    }
}
