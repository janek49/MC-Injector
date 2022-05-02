package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsScrolledSelectionListProxy;
import net.minecraft.realms.Tezzelator;

@ClientJarOnly
public abstract class RealmsScrolledSelectionList extends RealmsGuiEventListener {
   private final RealmsScrolledSelectionListProxy proxy;

   public RealmsScrolledSelectionList(int var1, int var2, int var3, int var4, int var5) {
      this.proxy = new RealmsScrolledSelectionListProxy(this, var1, var2, var3, var4, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.proxy.render(var1, var2, var3);
   }

   public int width() {
      return this.proxy.getWidth();
   }

   protected void renderItem(int var1, int var2, int var3, int var4, Tezzelator tezzelator, int var6, int var7) {
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.renderItem(var1, var2, var3, var4, Tezzelator.instance, var5, var6);
   }

   public int getItemCount() {
      return 0;
   }

   public boolean selectItem(int var1, int var2, double var3, double var5) {
      return true;
   }

   public boolean isSelectedItem(int i) {
      return false;
   }

   public void renderBackground() {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.proxy.getWidth() / 2 + 124;
   }

   public void scroll(int i) {
      this.proxy.scroll(i);
   }

   public int getScroll() {
      return this.proxy.getScroll();
   }

   protected void renderList(int var1, int var2, int var3, int var4) {
   }

   public GuiEventListener getProxy() {
      return this.proxy;
   }
}
