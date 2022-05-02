package net.minecraft.client.gui.components.events;

import com.fox2code.repacker.ClientJarOnly;
import java.util.List;
import java.util.ListIterator;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.events.GuiEventListener;

@ClientJarOnly
public interface ContainerEventHandler extends GuiEventListener {
   List children();

   default Optional getChildAt(double var1, double var3) {
      for(GuiEventListener var6 : this.children()) {
         if(var6.isMouseOver(var1, var3)) {
            return Optional.of(var6);
         }
      }

      return Optional.empty();
   }

   default boolean mouseClicked(double var1, double var3, int var5) {
      for(GuiEventListener var7 : this.children()) {
         if(var7.mouseClicked(var1, var3, var5)) {
            this.setFocused(var7);
            if(var5 == 0) {
               this.setDragging(true);
            }

            return true;
         }
      }

      return false;
   }

   default boolean mouseReleased(double var1, double var3, int var5) {
      this.setDragging(false);
      return this.getChildAt(var1, var3).filter((guiEventListener) -> {
         return guiEventListener.mouseReleased(var1, var3, var5);
      }).isPresent();
   }

   default boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.getFocused() != null && this.isDragging() && var5 == 0?this.getFocused().mouseDragged(var1, var3, var5, var6, var8):false;
   }

   boolean isDragging();

   void setDragging(boolean var1);

   default boolean mouseScrolled(double var1, double var3, double var5) {
      return this.getChildAt(var1, var3).filter((guiEventListener) -> {
         return guiEventListener.mouseScrolled(var1, var3, var5);
      }).isPresent();
   }

   default boolean keyPressed(int var1, int var2, int var3) {
      return this.getFocused() != null && this.getFocused().keyPressed(var1, var2, var3);
   }

   default boolean keyReleased(int var1, int var2, int var3) {
      return this.getFocused() != null && this.getFocused().keyReleased(var1, var2, var3);
   }

   default boolean charTyped(char var1, int var2) {
      return this.getFocused() != null && this.getFocused().charTyped(var1, var2);
   }

   @Nullable
   GuiEventListener getFocused();

   void setFocused(@Nullable GuiEventListener var1);

   default void setInitialFocus(@Nullable GuiEventListener initialFocus) {
      this.setFocused(initialFocus);
   }

   default void magicalSpecialHackyFocus(@Nullable GuiEventListener guiEventListener) {
      this.setFocused(guiEventListener);
   }

   default boolean changeFocus(boolean b) {
      GuiEventListener var2 = this.getFocused();
      boolean var3 = var2 != null;
      if(var3 && var2.changeFocus(b)) {
         return true;
      } else {
         List<? extends GuiEventListener> var4 = this.children();
         int var6 = var4.indexOf(var2);
         int var5;
         if(var3 && var6 >= 0) {
            var5 = var6 + (b?1:0);
         } else if(b) {
            var5 = 0;
         } else {
            var5 = var4.size();
         }

         ListIterator<? extends GuiEventListener> var7 = var4.listIterator(var5);
         BooleanSupplier var8 = b?var7::hasNext:var7::hasPrevious;
         Supplier<? extends GuiEventListener> var9 = b?var7::next:var7::previous;

         while(var8.getAsBoolean()) {
            GuiEventListener var10 = (GuiEventListener)var9.get();
            if(var10.changeFocus(b)) {
               this.setFocused(var10);
               return true;
            }
         }

         this.setFocused((GuiEventListener)null);
         return false;
      }
   }
}
