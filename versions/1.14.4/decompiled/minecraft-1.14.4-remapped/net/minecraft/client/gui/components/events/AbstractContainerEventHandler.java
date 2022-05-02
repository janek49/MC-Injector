package net.minecraft.client.gui.components.events;

import com.fox2code.repacker.ClientJarOnly;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.events.ContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;

@ClientJarOnly
public abstract class AbstractContainerEventHandler extends GuiComponent implements ContainerEventHandler {
   @Nullable
   private GuiEventListener focused;
   private boolean isDragging;

   public final boolean isDragging() {
      return this.isDragging;
   }

   public final void setDragging(boolean dragging) {
      this.isDragging = dragging;
   }

   @Nullable
   public GuiEventListener getFocused() {
      return this.focused;
   }

   public void setFocused(@Nullable GuiEventListener focused) {
      this.focused = focused;
   }
}
