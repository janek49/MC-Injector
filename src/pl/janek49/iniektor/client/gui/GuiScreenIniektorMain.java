package pl.janek49.iniektor.client.gui;

import net.minecraft.client.gui.GuiMultiplayer;
import net.minecraft.client.gui.GuiOptions;
import pl.janek49.iniektor.api.WrapperMisc;
import pl.janek49.iniektor.client.IniektorClient;

import java.awt.*;

public class GuiScreenIniektorMain extends IniektorGuiScreen {

    private Color currentColor = Color.getHSBColor(0, 0.8f, 0.8f);
    private Color currentColor2 = Color.getHSBColor(0.1f, 0.8f, 0.8f);

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

        guiButtons.add(new FlatTextureGuiButton(0, btn1Pos, btnY, 80, 80, "Singleplayer", "singleplayer.png"));
        guiButtons.add(new FlatTextureGuiButton(1, btn1Pos + (btnRealWidth * 1), btnY, 80, 80, "Multiplayer", "multiplayer.png"));
        guiButtons.add(new FlatTextureGuiButton(2, btn1Pos + (btnRealWidth * 2), btnY, 80, 80, "Settings", "settings.png"));
    }

    @Override
    public void onButtonClick(FlatGuiButton button) {
        if (button.getId() == 0) {
            this.mc.displayGuiScreen(WrapperMisc.GuiSinglePlayer.newType(this));
        } else if (button.getId() == 1) {
            this.mc.displayGuiScreen(new GuiMultiplayer(this));
        } else if (button.getId() == 2) {
            this.mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
    }

    @Override
    public void renderScreen(int mouseX, int mouseY) {
        RenderUtil.drawGradientRect(0, 0, getWidth(), getHeight(), RenderUtil.getArgbFromColor(currentColor), RenderUtil.getArgbFromColor(currentColor2));

        FontUtil.drawCenteredString(IniektorClient.INSTANCE.guiManager.getDefaultFont(), "Iniektor v0.1", getWidth() / 2, 10, 0xFFFFFF);
        FontUtil.drawCenteredString(IniektorClient.INSTANCE.guiManager.getDefaultFont(), "by janek49", getWidth() / 2, getHeight() - 20, 0xFFFFFF);

        currentColor = RenderUtil.getNextColor(currentColor, 0.003f, 0, 0);
        currentColor2 = RenderUtil.getNextColor(currentColor2, 0.004f, 0, 0);

        super.renderScreen(mouseX, mouseY);
    }
}
