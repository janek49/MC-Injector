package pl.janek49.iniektor.client.gui;

import pl.janek49.iniektor.agent.Logger;
import pl.janek49.iniektor.agent.Version;
import pl.janek49.iniektor.agent.annotation.RenameMethod;
import pl.janek49.iniektor.api.WrapperMisc;
import pl.janek49.iniektor.api.WrapperResolution;

import java.util.ArrayList;
import java.util.List;

public class IniektorGuiScreen {

    public IniektorGuiScreen() {

    }

    //the patch for this class overwrites constructor, so all initializations have to be done in this void
    private void initClass() {
        guiButtons = new ArrayList<>();
    }

    protected List<FlatGuiButton> guiButtons;

    @RenameMethod(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiScreen/drawScreen", descriptor = "(IIF)V")
    public void _drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            renderScreen(mouseX, mouseY);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @RenameMethod(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiScreen/initGui", descriptor = "()V")
    public void _initGui() {
        initGui();
    }

    @RenameMethod(version = Version.DEFAULT, name = "net/minecraft/client/gui/GuiScreen/mouseClicked", descriptor = "(III)V")
    public void _mouseClicked(int mouseX, int mouseY, int mouseButton) {
       // onMouseClicked(mouseX, mouseY, mouseButton);
    }


    @RenameMethod(version = Version.DEFAULT, name = "net/minecraft/client/gui/screens/Screen/render", descriptor = "(IIF)V")
    public void _mc114_drawScreen(int mouseX, int mouseY, float partialTicks) {
        try {
            renderScreen(mouseX, mouseY);
        } catch (Throwable ex) {
            ex.printStackTrace();
        }
    }

    @RenameMethod(version = Version.DEFAULT, name = "net/minecraft/client/gui/screens/Screen/init", descriptor = "()V")
    public void _mc114_init() {
        initGui();
    }

    public void renderScreen(int mouseX, int mouseY) {
        if (MouseHelper.isButtonDown(0)) {
            onMouseClicked(mouseX, mouseY, 0);
        }

        for (FlatGuiButton gb : guiButtons) {
            gb.renderButton(mouseX, mouseY);
        }
    }

    protected void onMouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (int i = 0; i < guiButtons.size(); ++i) {
                FlatGuiButton guibutton = this.guiButtons.get(i);
                if (guibutton.mousePressed(mouseX, mouseY)) {
                    try {
                        WrapperMisc.playPressSound();
                    } catch (Throwable ex) {
                        ex.printStackTrace();
                    }
                    onButtonClick(guibutton);
                }
            }
        }
    }

    public void onButtonClick(FlatGuiButton btn) {
    }

    public void initGui() {
    }

    public int getWidth() {
        return WrapperResolution.getScreenBounds().width;
    }

    public int getHeight() {
        return WrapperResolution.getScreenBounds().height;
    }

}
