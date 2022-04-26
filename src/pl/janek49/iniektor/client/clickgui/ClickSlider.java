package pl.janek49.iniektor.client.clickgui;

import org.lwjgl.input.Mouse;
import pl.janek49.iniektor.api.WrapperMisc;
import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.gui.FontUtil;
import pl.janek49.iniektor.client.gui.RenderUtil;

import java.awt.*;

public class ClickSlider extends ClickComponent {

    private String caption;
    private ActionHandler handler;
    public float min, max, value;

    private boolean isDrag = false;

    interface ActionHandler {
        public void onValueChanged(ClickSlider slider, float wasBefore, float isNow);
    }

    public ClickSlider(ClickPanel parent, String text, float value, float min, float max, ActionHandler actionHandler) {
        setBounds(new Rectangle(defSpacing, parent.getContentHeight(), parent.width - defSpacing * 2, 18));
        this.parent = parent;
        this.caption = text;
        this.value = value;
        this.min = min;
        this.max = max;
        this.handler = actionHandler;
    }

    @Override
    public void render(int mouseX, int mouseY, int screenW, int screenH) {
        float percentage = ((value - min)) / (max - min);
        float overlay = width * percentage;

        Rectangle sliderRect = new Rectangle(0, 12, width, 4);

        FontUtil.drawString(IniektorClient.INSTANCE.guiManager.getClickGuiFont(), String.format("%s: %.3f", caption, value), parent.translateX(x+2), parent.translateY(y - 2), 0xFFFFFF);

        RenderUtil.drawRect(parent.translateX(x), parent.translateY(y + sliderRect.y),
                parent.translateX(x + sliderRect.width), parent.translateY(y + sliderRect.y + sliderRect.height), 0xFF777777);

        RenderUtil.drawRect(parent.translateX(x), parent.translateY(y + sliderRect.y),
                parent.translateX((int) (x + overlay)), parent.translateY(y + sliderRect.y + sliderRect.height), 0xAA1158CD);
    }

    @Override
    public boolean handleMouseClick(int mouseX, int mouseY, boolean wasHandled) {
        if (wasHandled)
            return true;

        Rectangle sliderRect = new Rectangle(0, y + 12, width, 4);
        sliderRect.setLocation(parent.translateX(sliderRect.x), parent.translateY(sliderRect.y));

        isHover = (parent.parentGui == null || parent.parentGui.draggedPanel == null) && sliderRect.contains(mouseX, mouseY);

        isClicked = isHover && Mouse.isButtonDown(0);
        if (isClicked && !isDrag) {
            isDrag = true;
            WrapperMisc.playPressSound();
        } else if (!Mouse.isButtonDown(0)) {
            isDrag = false;
        }

        if (isDrag) {
            float offset = mouseX - sliderRect.x;
            float mtp = offset / width;
            float absVal = min + ((max - min) * mtp);
            float oldVal = value;
            value = absVal > max ? max : absVal < min ? min : absVal;
            if (handler != null) {
                handler.onValueChanged(this, oldVal, value);
            }
            wasHandled = true;
        }

        return wasHandled;
    }
}
