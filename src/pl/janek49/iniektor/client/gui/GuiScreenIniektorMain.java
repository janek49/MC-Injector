package pl.janek49.iniektor.client.gui;

import pl.janek49.iniektor.api.WrapperMisc;
import pl.janek49.iniektor.api.client.Minecraft;
import pl.janek49.iniektor.api.gui.DynamicTexture;
import pl.janek49.iniektor.api.gui.TextureUtil;
import pl.janek49.iniektor.client.IniektorClient;

import java.awt.*;
import java.awt.image.BufferedImage;

public class GuiScreenIniektorMain extends IniektorGuiScreen {

    private Color currentColor = Color.getHSBColor(0, 0.8f, 0.8f);
    private Color currentColor2 = Color.getHSBColor(0.1f, 0.8f, 0.8f);

    public static DynamicTexture imgSinglePlayer;
    public static DynamicTexture imgMultiPlayer;
    public static DynamicTexture imgSettings;

    public GuiScreenIniektorMain() {
        if (imgSinglePlayer == null)
            imgSinglePlayer = DynamicTexture.fromInputStream(TextureUtil.getImageStream("singleplayer.png"));
        if (imgMultiPlayer == null)
            imgMultiPlayer = DynamicTexture.fromInputStream(TextureUtil.getImageStream("multiplayer.png"));
        if (imgSettings == null)
            imgSettings = DynamicTexture.fromInputStream(TextureUtil.getImageStream("settings.png"));
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

        guiButtons.add(new FlatTextureGuiButton(0, btn1Pos, btnY, 80, 80, "Singleplayer", imgSinglePlayer));
        guiButtons.add(new FlatTextureGuiButton(1, btn1Pos + btnRealWidth, btnY, 80, 80, "Multiplayer", imgMultiPlayer));
        guiButtons.add(new FlatTextureGuiButton(2, btn1Pos + (btnRealWidth * 2), btnY, 80, 80, "Settings", imgSettings));
    }

    @Override
    public void onButtonClick(FlatGuiButton button) {
        if (button.getId() == 0) {
            Minecraft.displayGuiScreen(WrapperMisc.GuiSinglePlayer.newInstance(this));
        } else if (button.getId() == 1) {
            Minecraft.displayGuiScreen(WrapperMisc.GuiMultiPlayer.newInstance(this));
        } else if (button.getId() == 2) {
            Minecraft.displayGuiScreen(WrapperMisc.GuiOptions.newInstance(this, Minecraft.getGameSettings().getInstanceBehind()));
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
