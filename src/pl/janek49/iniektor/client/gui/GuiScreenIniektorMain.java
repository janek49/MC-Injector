package pl.janek49.iniektor.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.*;
import net.minecraft.client.renderer.entity.Render;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.asm.TransformMethodName;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.hook.WrapperMinecraft;

import java.awt.*;

public class GuiScreenIniektorMain extends GuiScreen {

    private Color currentColor = Color.getHSBColor(0, 0.7f, 0.9f);
    private Color currentColor2 = Color.getHSBColor(0.2f, 0.7f, 0.9f);

    public GuiScreenIniektorMain() {
    }

    @TransformMethodName(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiScreen/drawScreen", descriptor = "(IIF)V")
    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawGradientRect(0, 0, super.width, super.height, RenderUtil.getArgbFromColor(currentColor), RenderUtil.getArgbFromColor(currentColor2));

        String s1 = "Iniektor v0.1";
        super.drawCenteredString(Reflector.MC.fontRenderer, s1, super.width / 2, 10, 0xFFFFFF);

        super.drawScreen(mouseX, mouseY, partialTicks);

        currentColor = RenderUtil.getNextColor(currentColor, 0.005f, 0, 0);
        currentColor2 = RenderUtil.getNextColor(currentColor2, 0.005f, 0, 0);

    }
}
