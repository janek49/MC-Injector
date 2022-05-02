package net.minecraft.client.gui.spectator;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.spectator.SpectatorGui;
import net.minecraft.client.gui.spectator.RootSpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuCategory;
import net.minecraft.client.gui.spectator.SpectatorMenuItem;
import net.minecraft.client.gui.spectator.SpectatorMenuListener;
import net.minecraft.client.gui.spectator.categories.SpectatorPage;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@ClientJarOnly
public class SpectatorMenu {
   private static final SpectatorMenuItem CLOSE_ITEM = new SpectatorMenu.CloseSpectatorItem();
   private static final SpectatorMenuItem SCROLL_LEFT = new SpectatorMenu.ScrollMenuItem(-1, true);
   private static final SpectatorMenuItem SCROLL_RIGHT_ENABLED = new SpectatorMenu.ScrollMenuItem(1, true);
   private static final SpectatorMenuItem SCROLL_RIGHT_DISABLED = new SpectatorMenu.ScrollMenuItem(1, false);
   public static final SpectatorMenuItem EMPTY_SLOT = new SpectatorMenuItem() {
      public void selectItem(SpectatorMenu spectatorMenu) {
      }

      public Component getName() {
         return new TextComponent("");
      }

      public void renderIcon(float var1, int var2) {
      }

      public boolean isEnabled() {
         return false;
      }
   };
   private final SpectatorMenuListener listener;
   private final List previousCategories = Lists.newArrayList();
   private SpectatorMenuCategory category = new RootSpectatorMenuCategory();
   private int selectedSlot = -1;
   private int page;

   public SpectatorMenu(SpectatorMenuListener listener) {
      this.listener = listener;
   }

   public SpectatorMenuItem getItem(int i) {
      int var2 = i + this.page * 6;
      return this.page > 0 && i == 0?SCROLL_LEFT:(i == 7?(var2 < this.category.getItems().size()?SCROLL_RIGHT_ENABLED:SCROLL_RIGHT_DISABLED):(i == 8?CLOSE_ITEM:(var2 >= 0 && var2 < this.category.getItems().size()?(SpectatorMenuItem)MoreObjects.firstNonNull(this.category.getItems().get(var2), EMPTY_SLOT):EMPTY_SLOT)));
   }

   public List getItems() {
      List<SpectatorMenuItem> list = Lists.newArrayList();

      for(int var2 = 0; var2 <= 8; ++var2) {
         list.add(this.getItem(var2));
      }

      return list;
   }

   public SpectatorMenuItem getSelectedItem() {
      return this.getItem(this.selectedSlot);
   }

   public SpectatorMenuCategory getSelectedCategory() {
      return this.category;
   }

   public void selectSlot(int selectedSlot) {
      SpectatorMenuItem var2 = this.getItem(selectedSlot);
      if(var2 != EMPTY_SLOT) {
         if(this.selectedSlot == selectedSlot && var2.isEnabled()) {
            var2.selectItem(this);
         } else {
            this.selectedSlot = selectedSlot;
         }
      }

   }

   public void exit() {
      this.listener.onSpectatorMenuClosed(this);
   }

   public int getSelectedSlot() {
      return this.selectedSlot;
   }

   public void selectCategory(SpectatorMenuCategory category) {
      this.previousCategories.add(this.getCurrentPage());
      this.category = category;
      this.selectedSlot = -1;
      this.page = 0;
   }

   public SpectatorPage getCurrentPage() {
      return new SpectatorPage(this.category, this.getItems(), this.selectedSlot);
   }

   @ClientJarOnly
   static class CloseSpectatorItem implements SpectatorMenuItem {
      private CloseSpectatorItem() {
      }

      public void selectItem(SpectatorMenu spectatorMenu) {
         spectatorMenu.exit();
      }

      public Component getName() {
         return new TranslatableComponent("spectatorMenu.close", new Object[0]);
      }

      public void renderIcon(float var1, int var2) {
         Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
         GuiComponent.blit(0, 0, 128.0F, 0.0F, 16, 16, 256, 256);
      }

      public boolean isEnabled() {
         return true;
      }
   }

   @ClientJarOnly
   static class ScrollMenuItem implements SpectatorMenuItem {
      private final int direction;
      private final boolean enabled;

      public ScrollMenuItem(int direction, boolean enabled) {
         this.direction = direction;
         this.enabled = enabled;
      }

      public void selectItem(SpectatorMenu spectatorMenu) {
         spectatorMenu.page = spectatorMenu.page + this.direction;
      }

      public Component getName() {
         return this.direction < 0?new TranslatableComponent("spectatorMenu.previous_page", new Object[0]):new TranslatableComponent("spectatorMenu.next_page", new Object[0]);
      }

      public void renderIcon(float var1, int var2) {
         Minecraft.getInstance().getTextureManager().bind(SpectatorGui.SPECTATOR_LOCATION);
         if(this.direction < 0) {
            GuiComponent.blit(0, 0, 144.0F, 0.0F, 16, 16, 256, 256);
         } else {
            GuiComponent.blit(0, 0, 160.0F, 0.0F, 16, 16, 256, 256);
         }

      }

      public boolean isEnabled() {
         return this.enabled;
      }
   }
}
