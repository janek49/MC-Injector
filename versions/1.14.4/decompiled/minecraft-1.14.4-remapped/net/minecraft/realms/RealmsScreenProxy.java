package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAbstractButtonProxy;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsLabelProxy;
import net.minecraft.realms.RealmsScreen;
import net.minecraft.world.item.ItemStack;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class RealmsScreenProxy extends Screen {
   private final RealmsScreen screen;
   private static final Logger LOGGER = LogManager.getLogger();

   public RealmsScreenProxy(RealmsScreen screen) {
      super(NarratorChatListener.NO_TITLE);
      this.screen = screen;
   }

   public RealmsScreen getScreen() {
      return this.screen;
   }

   public void init(Minecraft minecraft, int var2, int var3) {
      this.screen.init(minecraft, var2, var3);
      super.init(minecraft, var2, var3);
   }

   public void init() {
      this.screen.init();
      super.init();
   }

   public void drawCenteredString(String string, int var2, int var3, int var4) {
      super.drawCenteredString(this.font, string, var2, var3, var4);
   }

   public void drawString(String string, int var2, int var3, int var4, boolean var5) {
      if(var5) {
         super.drawString(this.font, string, var2, var3, var4);
      } else {
         this.font.draw(string, (float)var2, (float)var3, var4);
      }

   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.screen.blit(var1, var2, var3, var4, var5, var6);
      super.blit(var1, var2, var3, var4, var5, var6);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      GuiComponent.blit(var0, var1, var6, var7, var2, var3, var4, var5, var8, var9);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7) {
      GuiComponent.blit(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6) {
      super.fillGradient(var1, var2, var3, var4, var5, var6);
   }

   public void renderBackground() {
      super.renderBackground();
   }

   public boolean isPauseScreen() {
      return super.isPauseScreen();
   }

   public void renderBackground(int i) {
      super.renderBackground(i);
   }

   public void render(int var1, int var2, float var3) {
      this.screen.render(var1, var2, var3);
   }

   public void renderTooltip(ItemStack itemStack, int var2, int var3) {
      super.renderTooltip(itemStack, var2, var3);
   }

   public void renderTooltip(String string, int var2, int var3) {
      super.renderTooltip(string, var2, var3);
   }

   public void renderTooltip(List list, int var2, int var3) {
      super.renderTooltip(list, var2, var3);
   }

   public void tick() {
      this.screen.tick();
      super.tick();
   }

   public int width() {
      return this.width;
   }

   public int height() {
      return this.height;
   }

   public int fontLineHeight() {
      this.font.getClass();
      return 9;
   }

   public int fontWidth(String string) {
      return this.font.width(string);
   }

   public void fontDrawShadow(String string, int var2, int var3, int var4) {
      this.font.drawShadow(string, (float)var2, (float)var3, var4);
   }

   public List fontSplit(String string, int var2) {
      return this.font.split(string, var2);
   }

   public void childrenClear() {
      this.children.clear();
   }

   public void addWidget(RealmsGuiEventListener realmsGuiEventListener) {
      if(this.hasWidget(realmsGuiEventListener) || !this.children.add(realmsGuiEventListener.getProxy())) {
         LOGGER.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
      }

   }

   public void narrateLabels() {
      List<String> var1 = (List)this.children.stream().filter((guiEventListener) -> {
         return guiEventListener instanceof RealmsLabelProxy;
      }).map((guiEventListener) -> {
         return ((RealmsLabelProxy)guiEventListener).getLabel().getText();
      }).collect(Collectors.toList());
      Realms.narrateNow((Iterable)var1);
   }

   public void removeWidget(RealmsGuiEventListener realmsGuiEventListener) {
      if(!this.hasWidget(realmsGuiEventListener) || !this.children.remove(realmsGuiEventListener.getProxy())) {
         LOGGER.error("Tried to add the same widget multiple times: " + realmsGuiEventListener);
      }

   }

   public boolean hasWidget(RealmsGuiEventListener realmsGuiEventListener) {
      return this.children.contains(realmsGuiEventListener.getProxy());
   }

   public void buttonsAdd(AbstractRealmsButton abstractRealmsButton) {
      this.addButton(abstractRealmsButton.getProxy());
   }

   public List buttons() {
      List<AbstractRealmsButton<?>> list = Lists.newArrayListWithExpectedSize(this.buttons.size());

      for(AbstractWidget var3 : this.buttons) {
         list.add(((RealmsAbstractButtonProxy)var3).getButton());
      }

      return list;
   }

   public void buttonsClear() {
      Set<GuiEventListener> var1 = Sets.newHashSet(this.buttons);
      this.children.removeIf(var1::contains);
      this.buttons.clear();
   }

   public void removeButton(RealmsButton realmsButton) {
      this.children.remove(realmsButton.getProxy());
      this.buttons.remove(realmsButton.getProxy());
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      return this.screen.mouseClicked(var1, var3, var5)?true:super.mouseClicked(var1, var3, var5);
   }

   public boolean mouseReleased(double var1, double var3, int var5) {
      return this.screen.mouseReleased(var1, var3, var5);
   }

   public boolean mouseDragged(double var1, double var3, int var5, double var6, double var8) {
      return this.screen.mouseDragged(var1, var3, var5, var6, var8)?true:super.mouseDragged(var1, var3, var5, var6, var8);
   }

   public boolean keyPressed(int var1, int var2, int var3) {
      return this.screen.keyPressed(var1, var2, var3)?true:super.keyPressed(var1, var2, var3);
   }

   public boolean charTyped(char var1, int var2) {
      return this.screen.charTyped(var1, var2)?true:super.charTyped(var1, var2);
   }

   public void removed() {
      this.screen.removed();
      super.removed();
   }

   public int draw(String string, int var2, int var3, int var4, boolean var5) {
      return var5?this.font.drawShadow(string, (float)var2, (float)var3, var4):this.font.draw(string, (float)var2, (float)var3, var4);
   }

   public Font getFont() {
      return this.font;
   }
}
