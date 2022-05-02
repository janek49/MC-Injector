package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import java.util.Collection;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsObjectSelectionListProxy;
import net.minecraft.realms.Tezzelator;

@ClientJarOnly
public abstract class RealmsObjectSelectionList extends RealmsGuiEventListener {
   private final RealmsObjectSelectionListProxy proxy;

   public RealmsObjectSelectionList(int var1, int var2, int var3, int var4, int var5) {
      this.proxy = new RealmsObjectSelectionListProxy(this, var1, var2, var3, var4, var5);
   }

   public void render(int var1, int var2, float var3) {
      this.proxy.render(var1, var2, var3);
   }

   public void addEntry(RealmListEntry realmListEntry) {
      this.proxy.addEntry((ObjectSelectionList.Entry)realmListEntry);
   }

   public void remove(int i) {
      this.proxy.remove(i);
   }

   public void clear() {
      this.proxy.clear();
   }

   public boolean removeEntry(RealmListEntry realmListEntry) {
      return this.proxy.removeEntry((ObjectSelectionList.Entry)realmListEntry);
   }

   public int width() {
      return this.proxy.getWidth();
   }

   protected void renderItem(int var1, int var2, int var3, int var4, Tezzelator tezzelator, int var6, int var7) {
   }

   public void setLeftPos(int leftPos) {
      this.proxy.setLeftPos(leftPos);
   }

   public void renderItem(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.renderItem(var1, var2, var3, var4, Tezzelator.instance, var5, var6);
   }

   public void setSelected(int selected) {
      this.proxy.setSelectedItem(selected);
   }

   public void itemClicked(int var1, int var2, double var3, double var5, int var7) {
   }

   public int getItemCount() {
      return this.proxy.getItemCount();
   }

   public void renderBackground() {
   }

   public int getMaxPosition() {
      return 0;
   }

   public int getScrollbarPosition() {
      return this.proxy.getRowLeft() + this.proxy.getRowWidth();
   }

   public int y0() {
      return this.proxy.y0();
   }

   public int y1() {
      return this.proxy.y1();
   }

   public int headerHeight() {
      return this.proxy.headerHeight();
   }

   public int itemHeight() {
      return this.proxy.itemHeight();
   }

   public void scroll(int i) {
      this.proxy.setScrollAmount((double)i);
   }

   public int getScroll() {
      return (int)this.proxy.getScrollAmount();
   }

   public GuiEventListener getProxy() {
      return this.proxy;
   }

   public int getRowWidth() {
      return (int)((double)this.width() * 0.6D);
   }

   public abstract boolean isFocused();

   public void selectItem(int selected) {
      this.setSelected(selected);
   }

   @Nullable
   public RealmListEntry getSelected() {
      return (RealmListEntry)this.proxy.getSelected();
   }

   public List children() {
      return this.proxy.children();
   }

   public void replaceEntries(Collection collection) {
      this.proxy.replaceEntries(collection);
   }

   public int getRowTop(int i) {
      return this.proxy.getRowTop(i);
   }

   public int getRowLeft() {
      return this.proxy.getRowLeft();
   }
}
