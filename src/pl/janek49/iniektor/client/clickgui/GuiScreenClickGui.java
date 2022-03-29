package pl.janek49.iniektor.client.clickgui;

import pl.janek49.iniektor.client.IniektorClient;
import pl.janek49.iniektor.client.config.Property;
import pl.janek49.iniektor.client.gui.IniektorGuiScreen;
import pl.janek49.iniektor.client.gui.RenderUtil;
import pl.janek49.iniektor.client.modules.Module;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GuiScreenClickGui extends IniektorGuiScreen {

    public List<ClickPanel> panels = new ArrayList<>();
    public ClickPanel draggedPanel = null;


    @Override
    public void renderScreen(int mouseX, int mouseY) {
        RenderUtil.drawRect(0, 0, getWidth(), getHeight(), 0x55000000);
        for (ClickPanel cp : panels)
            cp.render(mouseX, mouseY, getWidth(), getHeight());
        super.renderScreen(mouseX, mouseY);
    }

    @Override
    public void initGui() {
        panels.clear();
        HashMap<Module.Category, ClickPanel> panels = new HashMap<>();
        for (Module m : IniektorClient.INSTANCE.moduleManager.modules) {
            ClickPanel cp;
            if (!panels.containsKey(m.category)) {
                cp = new ClickPanel(m.category.toString().charAt(0) + m.category.toString().substring(1).toLowerCase(), 10 + (this.panels.size() * (90)), 15, 80, 100);
                cp.parentGui = this;
                panels.put(m.category, cp);
                this.panels.add(cp);
            }
            cp = panels.get(m.category);
            ClickToggleButton btn = new ClickToggleButton(cp, m.name, new ClickButton.ActionHandler() {
                @Override
                public void onClick(ClickButton btn, int mouseX, int mouseY) {
                    IniektorClient.INSTANCE.moduleManager.setEnabled(m, ((ClickToggleButton) btn).toggled);
                }
            });
            btn.toggled = m.isEnabled;
            cp.children.add(btn);

            List<Property> props = IniektorClient.INSTANCE.configManager.getPropertiesFor(m);
            if (props != null && props.size() > 0) {
                for (Property p : props) {
                    if (btn.configPanel == null)
                        btn.configPanel = new ClickPanel(m.name, 0, 0, 80, 100);
                    if (p.getValue().getClass().equals(Boolean.class)) {
                        ClickToggleButton propBtn = new ClickToggleButton(btn.configPanel, p.propertyName, new ClickButton.ActionHandler() {
                            @Override
                            public void onClick(ClickButton btn, int mouseX, int mouseY) {
                                p.setValue(((ClickToggleButton) btn).toggled);
                            }
                        });
                        propBtn.toggled = (boolean) p.getValue();
                        btn.configPanel.children.add(propBtn);
                    }
                }
                btn.configPanel.wrapHeight();
            }
            cp.wrapHeight();
        }
    }
}
