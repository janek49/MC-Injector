package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractSelectionList;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

@ClientJarOnly
public abstract class ContainerObjectSelectionList extends AbstractSelectionList {
   public ContainerObjectSelectionList(Minecraft minecraft, int var2, int var3, int var4, int var5, int var6) {
      super(minecraft, var2, var3, var4, var5, var6);
   }

   public boolean changeFocus(boolean b) {
      boolean var2 = super.changeFocus(b);
      if(var2) {
         this.ensureVisible(this.getFocused());
      }

      return var2;
   }

   protected boolean isSelectedItem(int i) {
      return false;
   }

   @ClientJarOnly
   public abstract static class Entry extends AbstractSelectionList.Entry implements ContainerEventHandler {
      @Nullable
      private GuiEventListener focused;
      private boolean dragging;

      public boolean isDragging() {
         return this.dragging;
      }

      public void setDragging(boolean dragging) {
         this.dragging = dragging;
      }

      public void setFocused(@Nullable GuiEventListener focused) {
         this.focused = focused;
      }

      @Nullable
      public GuiEventListener getFocused() {
         return this.focused;
      }
   }
}
