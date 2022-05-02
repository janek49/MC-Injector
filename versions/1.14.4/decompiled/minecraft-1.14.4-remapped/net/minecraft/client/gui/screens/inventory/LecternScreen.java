package net.minecraft.client.gui.screens.inventory;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.BookViewScreen;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.LecternMenu;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public class LecternScreen extends BookViewScreen implements MenuAccess {
   private final LecternMenu menu;
   private final ContainerListener listener = new ContainerListener() {
      public void refreshContainer(AbstractContainerMenu abstractContainerMenu, NonNullList nonNullList) {
         LecternScreen.this.bookChanged();
      }

      public void slotChanged(AbstractContainerMenu abstractContainerMenu, int var2, ItemStack itemStack) {
         LecternScreen.this.bookChanged();
      }

      public void setContainerData(AbstractContainerMenu abstractContainerMenu, int var2, int var3) {
         if(var2 == 0) {
            LecternScreen.this.pageChanged();
         }

      }
   };

   public LecternScreen(LecternMenu menu, Inventory inventory, Component component) {
      this.menu = menu;
   }

   public LecternMenu getMenu() {
      return this.menu;
   }

   protected void init() {
      super.init();
      this.menu.addSlotListener(this.listener);
   }

   public void onClose() {
      this.minecraft.player.closeContainer();
      super.onClose();
   }

   public void removed() {
      super.removed();
      this.menu.removeSlotListener(this.listener);
   }

   protected void createMenuControls() {
      if(this.minecraft.player.mayBuild()) {
         this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.get("gui.done", new Object[0]), (button) -> {
            this.minecraft.setScreen((Screen)null);
         }));
         this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.get("lectern.take_book", new Object[0]), (button) -> {
            this.sendButtonClick(3);
         }));
      } else {
         super.createMenuControls();
      }

   }

   protected void pageBack() {
      this.sendButtonClick(1);
   }

   protected void pageForward() {
      this.sendButtonClick(2);
   }

   protected boolean forcePage(int i) {
      if(i != this.menu.getPage()) {
         this.sendButtonClick(100 + i);
         return true;
      } else {
         return false;
      }
   }

   private void sendButtonClick(int i) {
      this.minecraft.gameMode.handleInventoryButtonClick(this.menu.containerId, i);
   }

   public boolean isPauseScreen() {
      return false;
   }

   private void bookChanged() {
      ItemStack var1 = this.menu.getBook();
      this.setBookAccess(BookViewScreen.BookAccess.fromItem(var1));
   }

   private void pageChanged() {
      this.setPage(this.menu.getPage());
   }

   // $FF: synthetic method
   public AbstractContainerMenu getMenu() {
      return this.getMenu();
   }
}
