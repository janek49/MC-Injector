package net.minecraft.client.gui.screens.worldselection;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.function.Consumer;
import java.util.function.Supplier;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.worldselection.CreateWorldScreen;
import net.minecraft.client.gui.screens.worldselection.WorldSelectionList;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;

@ClientJarOnly
public class SelectWorldScreen extends Screen {
   protected final Screen lastScreen;
   private String toolTip;
   private Button deleteButton;
   private Button selectButton;
   private Button renameButton;
   private Button copyButton;
   protected EditBox searchBox;
   private WorldSelectionList list;

   public SelectWorldScreen(Screen lastScreen) {
      super(new TranslatableComponent("selectWorld.title", new Object[0]));
      this.lastScreen = lastScreen;
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      return super.mouseScrolled(var1, var3, var5);
   }

   public void tick() {
      this.searchBox.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.searchBox = new EditBox(this.font, this.width / 2 - 100, 22, 200, 20, this.searchBox, I18n.get("selectWorld.search", new Object[0]));
      this.searchBox.setResponder((string) -> {
         this.list.refreshList(() -> {
            return string;
         }, false);
      });
      this.list = new WorldSelectionList(this, this.minecraft, this.width, this.height, 48, this.height - 64, 36, () -> {
         return this.searchBox.getValue();
      }, this.list);
      this.children.add(this.searchBox);
      this.children.add(this.list);
      this.selectButton = (Button)this.addButton(new Button(this.width / 2 - 154, this.height - 52, 150, 20, I18n.get("selectWorld.select", new Object[0]), (button) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::joinWorld);
      }));
      this.addButton(new Button(this.width / 2 + 4, this.height - 52, 150, 20, I18n.get("selectWorld.create", new Object[0]), (button) -> {
         this.minecraft.setScreen(new CreateWorldScreen(this));
      }));
      this.renameButton = (Button)this.addButton(new Button(this.width / 2 - 154, this.height - 28, 72, 20, I18n.get("selectWorld.edit", new Object[0]), (button) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::editWorld);
      }));
      this.deleteButton = (Button)this.addButton(new Button(this.width / 2 - 76, this.height - 28, 72, 20, I18n.get("selectWorld.delete", new Object[0]), (button) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::deleteWorld);
      }));
      this.copyButton = (Button)this.addButton(new Button(this.width / 2 + 4, this.height - 28, 72, 20, I18n.get("selectWorld.recreate", new Object[0]), (button) -> {
         this.list.getSelectedOpt().ifPresent(WorldSelectionList.WorldListEntry::recreateWorld);
      }));
      this.addButton(new Button(this.width / 2 + 82, this.height - 28, 72, 20, I18n.get("gui.cancel", new Object[0]), (button) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.updateButtonStatus(false);
      this.setInitialFocus(this.searchBox);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return super.keyPressed(var1, var2, var3)?true:this.searchBox.keyPressed(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      return this.searchBox.charTyped(var1, var2);
   }

   public void render(int var1, int var2, float var3) {
      this.toolTip = null;
      this.list.render(var1, var2, var3);
      this.searchBox.render(var1, var2, var3);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2, 8, 16777215);
      super.render(var1, var2, var3);
      if(this.toolTip != null) {
         this.renderTooltip(Lists.newArrayList(Splitter.on("\n").split(this.toolTip)), var1, var2);
      }

   }

   public void setToolTip(String toolTip) {
      this.toolTip = toolTip;
   }

   public void updateButtonStatus(boolean b) {
      this.selectButton.active = b;
      this.deleteButton.active = b;
      this.renameButton.active = b;
      this.copyButton.active = b;
   }

   public void removed() {
      if(this.list != null) {
         this.list.children().forEach(WorldSelectionList.WorldListEntry::close);
      }

   }
}
