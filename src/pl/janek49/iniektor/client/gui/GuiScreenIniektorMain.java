package pl.janek49.iniektor.client.gui;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import net.minecraft.client.gui.GuiSelectWorld;
import pl.janek49.iniektor.client.hook.Reflector;
import pl.janek49.iniektor.client.hook.WrapperMisc;

import java.awt.*;

public class GuiScreenIniektorMain extends IniektorGuiScreen {

    private Color currentColor = Color.getHSBColor(0, 0.7f, 0.9f);
    private Color currentColor2 = Color.getHSBColor(0.2f, 0.7f, 0.9f);

    public GuiScreenIniektorMain() {
    }

    @Override
    public void initGui() {
        guiButtons.clear();

        int btnAmount = 3;
        int btnWidth = 80;
        int btnGap = 5;
        int btnRealWidth = btnWidth + btnGap;
        int btnY = (getHeight() / 2) - (btnWidth / 2);
        int btnBoxWidth = (btnAmount * (btnRealWidth)) - btnGap;
        int btn1Pos = (getWidth() / 2) - (btnBoxWidth / 2);

        guiButtons.add(new FlatGuiButton(0, btn1Pos, btnY, 80, 80, "Singleplayer"));
        guiButtons.add(new FlatGuiButton(1, btn1Pos + (btnRealWidth * 1), btnY, 80, 80, "Multiplayer"));
        guiButtons.add(new FlatGuiButton(2, btn1Pos + (btnRealWidth * 2), btnY, 80, 80, "Settings"));
    }

    @Override
    public void onButtonClick(FlatGuiButton button) {
        if (button.getId() == 0) {
            this.mc.displayGuiScreen(WrapperMisc.GuiSelectWorld.newType(this));
        } else if (button.getId() == 1) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (button.getId() == 2) {
            this.mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
    }

    @Override
    public void renderScreen(int mouseX, int mouseY) {
        RenderUtil.drawGradientRect(0, 0, getWidth(), getHeight(), RenderUtil.getArgbFromColor(currentColor), RenderUtil.getArgbFromColor(currentColor2));

        RenderUtil.drawCenteredStringShadow(this, Reflector.MC.fontRenderer, "Iniektor v0.1", getWidth() / 2, 10, 0xFFFFFF);
        RenderUtil.drawCenteredStringShadow(this, Reflector.MC.fontRenderer, "by janek49", getWidth() / 2, getHeight() - 20, 0xFFFFFF);

        currentColor = RenderUtil.getNextColor(currentColor, 0.005f, 0, 0);
        currentColor2 = RenderUtil.getNextColor(currentColor2, 0.005f, 0, 0);

        super.renderScreen(mouseX, mouseY);
    }
}
