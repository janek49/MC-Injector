package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.AbstractList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.util.Mth;

@ClientJarOnly
public abstract class AbstractSelectionList extends AbstractContainerEventHandler implements Widget {
   protected static final int DRAG_OUTSIDE = -2;
   protected final Minecraft minecraft;
   protected final int itemHeight;
   private final List children = new AbstractSelectionList.TrackedList();
   protected int width;
   protected int height;
   protected int y0;
   protected int y1;
   protected int x1;
   protected int x0;
   protected boolean centerListVertically = true;
   protected int yDrag = -2;
   private double scrollAmount;
   protected boolean renderSelection = true;
   protected boolean renderHeader;
   protected int headerHeight;
   private boolean scrolling;
   private AbstractSelectionList.Entry selected;

   public AbstractSelectionList(Minecraft minecraft, int width, int height, int y0, int y1, int itemHeight) {
      this.minecraft = minecraft;
      this.width = width;
      this.height = height;
      this.y0 = y0;
      this.y1 = y1;
      this.itemHeight = itemHeight;
      this.x0 = 0;
      this.x1 = width;
   }

   public void setRenderSelection(boolean renderSelection) {
      this.renderSelection = renderSelection;
   }

   protected void setRenderHeader(boolean renderHeader, int headerHeight) {
      this.renderHeader = renderHeader;
      this.headerHeight = headerHeight;
      if(!renderHeader) {
         this.headerHeight = 0;
      }

   }

   public int getRowWidth() {
      return 220;
   }

   @Nullable
   public AbstractSelectionList.Entry getSelected() {
      return this.selected;
   }

   public void setSelected(@Nullable AbstractSelectionList.Entry selected) {
      this.selected = selected;
   }

   @Nullable
   public AbstractSelectionList.Entry getFocused() {
      return (AbstractSelectionList.Entry)super.getFocused();
   }

   public final List children() {
      return this.children;
   }

   protected final void clearEntries() {
      this.children.clear();
   }

   protected void replaceEntries(Collection collection) {
      this.children.clear();
      this.children.addAll(collection);
   }

   protected AbstractSelectionList.Entry getEntry(int i) {
      return (AbstractSelectionList.Entry)this.children().get(i);
   }

   protected int addEntry(AbstractSelectionList.Entry abstractSelectionList$Entry) {
      this.children.add(abstractSelectionList$Entry);
      return this.children.size() - 1;
   }

   protected int getItemCount() {
      return this.children().size();
   }

   protected boolean isSelectedItem(int i) {
      return Objects.equals(this.getSelected(), this.children().get(i));
   }

   @Nullable
   protected final AbstractSelectionList.Entry getEntryAtPosition(double var1, double var3) {
      int var5 = this.getRowWidth() / 2;
      int var6 = this.x0 + this.width / 2;
      int var7 = var6 - var5;
      int var8 = var6 + var5;
      int var9 = Mth.floor(var3 - (double)this.y0) - this.headerHeight + (int)this.getScrollAmount() - 4;
      int var10 = var9 / this.itemHeight;
      return var1 < (double)this.getScrollbarPosition() && var1 >= (double)var7 && var1 <= (double)var8 && var10 >= 0 && var9 >= 0 && var10 < this.getItemCount()?(AbstractSelectionList.Entry)this.children().get(var10):null;
   }

   public void updateSize(int width, int height, int y0, int y1) {
      this.width = width;
      this.height = height;
      this.y0 = y0;
      this.y1 = y1;
      this.x0 = 0;
      this.x1 = width;
   }

   public void setLeftPos(int leftPos) {
      this.x0 = leftPos;
      this.x1 = leftPos + this.width;
   }

   protected int getMaxPosition() {
      return this.getItemCount() * this.itemHeight + this.headerHeight;
   }

   protected void clickedHeader(int var1, int var2) {
   }

   protected void renderHeader(int var1, int var2, Tesselator tesselator) {
   }

   protected void renderBackground() {
   }

   protected void renderDecorations(int var1, int var2) {
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      int var4 = this.getScrollbarPosition();
      int var5 = var4 + 6;
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tesselator var6 = Tesselator.getInstance();
      BufferBuilder var7 = var6.getBuilder();
      this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var8 = 32.0F;
      var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var7.vertex((double)this.x0, (double)this.y1, 0.0D).uv((double)((float)this.x0 / 32.0F), (double)((float)(this.y1 + (int)this.getScrollAmount()) / 32.0F)).color(32, 32, 32, 255).endVertex();
      var7.vertex((double)this.x1, (double)this.y1, 0.0D).uv((double)((float)this.x1 / 32.0F), (double)((float)(this.y1 + (int)this.getScrollAmount()) / 32.0F)).color(32, 32, 32, 255).endVertex();
      var7.vertex((double)this.x1, (double)this.y0, 0.0D).uv((double)((float)this.x1 / 32.0F), (double)((float)(this.y0 + (int)this.getScrollAmount()) / 32.0F)).color(32, 32, 32, 255).endVertex();
      var7.vertex((double)this.x0, (double)this.y0, 0.0D).uv((double)((float)this.x0 / 32.0F), (double)((float)(this.y0 + (int)this.getScrollAmount()) / 32.0F)).color(32, 32, 32, 255).endVertex();
      var6.end();
      int var9 = this.getRowLeft();
      int var10 = this.y0 + 4 - (int)this.getScrollAmount();
      if(this.renderHeader) {
         this.renderHeader(var9, var10, var6);
      }

      this.renderList(var9, var10, var1, var2, var3);
      GlStateManager.disableDepthTest();
      this.renderHoleBackground(0, this.y0, 255, 255);
      this.renderHoleBackground(this.y1, this.height, 255, 255);
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ZERO, GlStateManager.DestFactor.ONE);
      GlStateManager.disableAlphaTest();
      GlStateManager.shadeModel(7425);
      GlStateManager.disableTexture();
      int var11 = 4;
      var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var7.vertex((double)this.x0, (double)(this.y0 + 4), 0.0D).uv(0.0D, 1.0D).color(0, 0, 0, 0).endVertex();
      var7.vertex((double)this.x1, (double)(this.y0 + 4), 0.0D).uv(1.0D, 1.0D).color(0, 0, 0, 0).endVertex();
      var7.vertex((double)this.x1, (double)this.y0, 0.0D).uv(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      var7.vertex((double)this.x0, (double)this.y0, 0.0D).uv(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
      var6.end();
      var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var7.vertex((double)this.x0, (double)this.y1, 0.0D).uv(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      var7.vertex((double)this.x1, (double)this.y1, 0.0D).uv(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
      var7.vertex((double)this.x1, (double)(this.y1 - 4), 0.0D).uv(1.0D, 0.0D).color(0, 0, 0, 0).endVertex();
      var7.vertex((double)this.x0, (double)(this.y1 - 4), 0.0D).uv(0.0D, 0.0D).color(0, 0, 0, 0).endVertex();
      var6.end();
      int var12 = this.getMaxScroll();
      if(var12 > 0) {
         int var13 = (int)((float)((this.y1 - this.y0) * (this.y1 - this.y0)) / (float)this.getMaxPosition());
         var13 = Mth.clamp(var13, 32, this.y1 - this.y0 - 8);
         int var14 = (int)this.getScrollAmount() * (this.y1 - this.y0 - var13) / var12 + this.y0;
         if(var14 < this.y0) {
            var14 = this.y0;
         }

         var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var7.vertex((double)var4, (double)this.y1, 0.0D).uv(0.0D, 1.0D).color(0, 0, 0, 255).endVertex();
         var7.vertex((double)var5, (double)this.y1, 0.0D).uv(1.0D, 1.0D).color(0, 0, 0, 255).endVertex();
         var7.vertex((double)var5, (double)this.y0, 0.0D).uv(1.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var7.vertex((double)var4, (double)this.y0, 0.0D).uv(0.0D, 0.0D).color(0, 0, 0, 255).endVertex();
         var6.end();
         var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var7.vertex((double)var4, (double)(var14 + var13), 0.0D).uv(0.0D, 1.0D).color(128, 128, 128, 255).endVertex();
         var7.vertex((double)var5, (double)(var14 + var13), 0.0D).uv(1.0D, 1.0D).color(128, 128, 128, 255).endVertex();
         var7.vertex((double)var5, (double)var14, 0.0D).uv(1.0D, 0.0D).color(128, 128, 128, 255).endVertex();
         var7.vertex((double)var4, (double)var14, 0.0D).uv(0.0D, 0.0D).color(128, 128, 128, 255).endVertex();
         var6.end();
         var7.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
         var7.vertex((double)var4, (double)(var14 + var13 - 1), 0.0D).uv(0.0D, 1.0D).color(192, 192, 192, 255).endVertex();
         var7.vertex((double)(var5 - 1), (double)(var14 + var13 - 1), 0.0D).uv(1.0D, 1.0D).color(192, 192, 192, 255).endVertex();
         var7.vertex((double)(var5 - 1), (double)var14, 0.0D).uv(1.0D, 0.0D).color(192, 192, 192, 255).endVertex();
         var7.vertex((double)var4, (double)var14, 0.0D).uv(0.0D, 0.0D).color(192, 192, 192, 255).endVertex();
         var6.end();
      }

      this.renderDecorations(var1, var2);
      GlStateManager.enableTexture();
      GlStateManager.shadeModel(7424);
      GlStateManager.enableAlphaTest();
      GlStateManager.disableBlend();
   }

   protected void centerScrollOn(AbstractSelectionList.Entry abstractSelectionList$Entry) {
      this.setScrollAmount((double)(this.children().indexOf(abstractSelectionList$Entry) * this.itemHeight + this.itemHeight / 2 - (this.y1 - this.y0) / 2));
   }

   protected void ensureVisible(AbstractSelectionList.Entry abstractSelectionList$Entry) {
      int var2 = this.getRowTop(this.children().indexOf(abstractSelectionList$Entry));
      int var3 = var2 - this.y0 - 4 - this.itemHeight;
      if(var3 < 0) {
         this.scroll(var3);
      }

      int var4 = this.y1 - var2 - this.itemHeight - this.itemHeight;
      if(var4 < 0) {
         this.scroll(-var4);
      }

   }

   private void scroll(int i) {
      this.setScrollAmount(this.getScrollAmount() + (double)i);
      this.yDrag = -2;
   }

   public double getScrollAmount() {
      return this.scrollAmount;
   }

   public void setScrollAmount(double scrollAmount) {
      this.scrollAmount = Mth.clamp(scrollAmount, 0.0D, (double)this.getMaxScroll());
   }

   private int getMaxScroll() {
      return Math.max(0, this.getMaxPosition() - (this.y1 - this.y0 - 4));
   }

   public int getScrollBottom() {
      return (int)this.getScrollAmount() - this.height - this.headerHeight;
   }

   protected void updateScrollingState(double var1, double var3, int var5) {
      this.scrolling = var5 == 0 && var1 >= (double)this.getScrollbarPosition() && var1 < (double)(this.getScrollbarPosition() + 6);
   }

   protected int getScrollbarPosition() {
      return this.width / 2 + 124;
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      this.updateScrollingState(var1, var3, var5);
      if(!this.isMouseOver(var1, var3)) {
         return false;
      } else {
         E var6 = this.getEntryAtPosition(var1, var3);
         if(var6 != null) {
            if(var6.mouseClicked(var1, var3, var5)) {
               this.setFocused(var6);
               this.setDragging(true);
               return true;
            }
         } else if(var5 == 0) {
            this.clickedHeader((int)(var1 - (double)(this.x0 + this.width / 2 - this.getRowWidth() / 2)), (int)(var3 - (double)this.y0) + (int)this.getScrollAmount() - 4);
            return true;
         }

         return this.scrolling;
      }
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      if(this.getFocused() != null) {
         this.getFocused().mouseReleased(var1, var3, var5);
      }

      return false;
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      if(super.mouseDragged(var1, var3, var5, var6, var8)) {
         return true;
      } else if(var5 == 0 && this.scrolling) {
         if(var3 < (double)this.y0) {
            this.setScrollAmount(0.0D);
         } else if(var3 > (double)this.y1) {
            this.setScrollAmount((double)this.getMaxScroll());
         } else {
            double var10 = (double)Math.max(1, this.getMaxScroll());
            int var12 = this.y1 - this.y0;
            int var13 = Mth.clamp((int)((float)(var12 * var12) / (float)this.getMaxPosition()), 32, var12 - 8);
            double var14 = Math.max(1.0D, var10 / (double)(var12 - var13));
            this.setScrollAmount(this.getScrollAmount() + var8 * var14);
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseScrolled(double var1, double var3, double var5) {
      this.setScrollAmount(this.getScrollAmount() - var5 * (double)this.itemHeight / 2.0D);
      return true;
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(super.keyPressed(var1, var2, var3)) {
         return true;
      } else if(var1 == 264) {
         this.moveSelection(1);
         return true;
      } else if(var1 == 265) {
         this.moveSelection(-1);
         return true;
      } else {
         return false;
      }
   }

   protected void moveSelection(int i) {
      if(!this.children().isEmpty()) {
         int var2 = this.children().indexOf(this.getSelected());
         int var3 = Mth.clamp(var2 + i, 0, this.getItemCount() - 1);
         E var4 = (AbstractSelectionList.Entry)this.children().get(var3);
         this.setSelected(var4);
         this.ensureVisible(var4);
      }

   }

   public boolean isMouseOver(double var1, double var3) {
      return var3 >= (double)this.y0 && var3 <= (double)this.y1 && var1 >= (double)this.x0 && var1 <= (double)this.x1;
   }

   protected void renderList(int var1, int var2, int var3, int var4, float var5) {
      int var6 = this.getItemCount();
      Tesselator var7 = Tesselator.getInstance();
      BufferBuilder var8 = var7.getBuilder();

      for(int var9 = 0; var9 < var6; ++var9) {
         int var10 = this.getRowTop(var9);
         int var11 = this.getRowBottom(var9);
         if(var11 >= this.y0 && var10 <= this.y1) {
            int var12 = var2 + var9 * this.itemHeight + this.headerHeight;
            int var13 = this.itemHeight - 4;
            E var14 = this.getEntry(var9);
            int var15 = this.getRowWidth();
            if(this.renderSelection && this.isSelectedItem(var9)) {
               int var16 = this.x0 + this.width / 2 - var15 / 2;
               int var17 = this.x0 + this.width / 2 + var15 / 2;
               GlStateManager.disableTexture();
               float var18 = this.isFocused()?1.0F:0.5F;
               GlStateManager.color4f(var18, var18, var18, 1.0F);
               var8.begin(7, DefaultVertexFormat.POSITION);
               var8.vertex((double)var16, (double)(var12 + var13 + 2), 0.0D).endVertex();
               var8.vertex((double)var17, (double)(var12 + var13 + 2), 0.0D).endVertex();
               var8.vertex((double)var17, (double)(var12 - 2), 0.0D).endVertex();
               var8.vertex((double)var16, (double)(var12 - 2), 0.0D).endVertex();
               var7.end();
               GlStateManager.color4f(0.0F, 0.0F, 0.0F, 1.0F);
               var8.begin(7, DefaultVertexFormat.POSITION);
               var8.vertex((double)(var16 + 1), (double)(var12 + var13 + 1), 0.0D).endVertex();
               var8.vertex((double)(var17 - 1), (double)(var12 + var13 + 1), 0.0D).endVertex();
               var8.vertex((double)(var17 - 1), (double)(var12 - 1), 0.0D).endVertex();
               var8.vertex((double)(var16 + 1), (double)(var12 - 1), 0.0D).endVertex();
               var7.end();
               GlStateManager.enableTexture();
            }

            int var16 = this.getRowLeft();
            var14.render(var9, var10, var16, var15, var13, var3, var4, this.isMouseOver((double)var3, (double)var4) && Objects.equals(this.getEntryAtPosition((double)var3, (double)var4), var14), var5);
         }
      }

   }

   protected int getRowLeft() {
      return this.x0 + this.width / 2 - this.getRowWidth() / 2 + 2;
   }

   protected int getRowTop(int i) {
      return this.y0 + 4 - (int)this.getScrollAmount() + i * this.itemHeight + this.headerHeight;
   }

   private int getRowBottom(int i) {
      return this.getRowTop(i) + this.itemHeight;
   }

   protected boolean isFocused() {
      return false;
   }

   protected void renderHoleBackground(int var1, int var2, int var3, int var4) {
      Tesselator var5 = Tesselator.getInstance();
      BufferBuilder var6 = var5.getBuilder();
      this.minecraft.getTextureManager().bind(GuiComponent.BACKGROUND_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var7 = 32.0F;
      var6.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var6.vertex((double)this.x0, (double)var2, 0.0D).uv(0.0D, (double)((float)var2 / 32.0F)).color(64, 64, 64, var4).endVertex();
      var6.vertex((double)(this.x0 + this.width), (double)var2, 0.0D).uv((double)((float)this.width / 32.0F), (double)((float)var2 / 32.0F)).color(64, 64, 64, var4).endVertex();
      var6.vertex((double)(this.x0 + this.width), (double)var1, 0.0D).uv((double)((float)this.width / 32.0F), (double)((float)var1 / 32.0F)).color(64, 64, 64, var3).endVertex();
      var6.vertex((double)this.x0, (double)var1, 0.0D).uv(0.0D, (double)((float)var1 / 32.0F)).color(64, 64, 64, var3).endVertex();
      var5.end();
   }

   protected AbstractSelectionList.Entry remove(int i) {
      E abstractSelectionList$Entry = (AbstractSelectionList.Entry)this.children.get(i);
      return this.removeEntry((AbstractSelectionList.Entry)this.children.get(i))?abstractSelectionList$Entry:null;
   }

   protected boolean removeEntry(AbstractSelectionList.Entry abstractSelectionList$Entry) {
      boolean var2 = this.children.remove(abstractSelectionList$Entry);
      if(var2 && abstractSelectionList$Entry == this.getSelected()) {
         this.setSelected((AbstractSelectionList.Entry)null);
      }

      return var2;
   }

   @ClientJarOnly
   public abstract static class Entry implements GuiEventListener {
      @Deprecated
      AbstractSelectionList list;

      public abstract void render(int var1, int var2, int var3, int var4, int var5, int var6, int var7, boolean var8, float var9);

      public boolean isMouseOver(double var1, double var3) {
         return Objects.equals(this.list.getEntryAtPosition(var1, var3), this);
      }
   }

   @ClientJarOnly
   class TrackedList extends AbstractList {
      private final List delegate;

      private TrackedList() {
         this.delegate = Lists.newArrayList();
      }

      public AbstractSelectionList.Entry get(int i) {
         return (AbstractSelectionList.Entry)this.delegate.get(i);
      }

      public int size() {
         return this.delegate.size();
      }

      public AbstractSelectionList.Entry set(int var1, AbstractSelectionList.Entry var2) {
         E var3 = (AbstractSelectionList.Entry)this.delegate.set(var1, var2);
         var2.list = AbstractSelectionList.this;
         return var3;
      }

      public void add(int var1, AbstractSelectionList.Entry abstractSelectionList$Entry) {
         this.delegate.add(var1, abstractSelectionList$Entry);
         abstractSelectionList$Entry.list = AbstractSelectionList.this;
      }

      public AbstractSelectionList.Entry remove(int i) {
         return (AbstractSelectionList.Entry)this.delegate.remove(i);
      }

      // $FF: synthetic method
      public Object remove(int var1) {
         return this.remove(var1);
      }

      // $FF: synthetic method
      public void add(int var1, Object var2) {
         this.add(var1, (AbstractSelectionList.Entry)var2);
      }

      // $FF: synthetic method
      public Object set(int var1, Object var2) {
         return this.set(var1, (AbstractSelectionList.Entry)var2);
      }

      // $FF: synthetic method
      public Object get(int var1) {
         return this.get(var1);
      }
   }
}
