package net.minecraft.realms;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.InputConstants;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.realms.AbstractRealmsButton;
import net.minecraft.realms.Realms;
import net.minecraft.realms.RealmsAnvilLevelStorageSource;
import net.minecraft.realms.RealmsButton;
import net.minecraft.realms.RealmsConfirmResultListener;
import net.minecraft.realms.RealmsEditBox;
import net.minecraft.realms.RealmsGuiEventListener;
import net.minecraft.realms.RealmsScreenProxy;
import net.minecraft.world.item.ItemStack;

@ClientJarOnly
public abstract class RealmsScreen extends RealmsGuiEventListener implements RealmsConfirmResultListener {
   public static final int SKIN_HEAD_U = 8;
   public static final int SKIN_HEAD_V = 8;
   public static final int SKIN_HEAD_WIDTH = 8;
   public static final int SKIN_HEAD_HEIGHT = 8;
   public static final int SKIN_HAT_U = 40;
   public static final int SKIN_HAT_V = 8;
   public static final int SKIN_HAT_WIDTH = 8;
   public static final int SKIN_HAT_HEIGHT = 8;
   public static final int SKIN_TEX_WIDTH = 64;
   public static final int SKIN_TEX_HEIGHT = 64;
   private Minecraft minecraft;
   public int width;
   public int height;
   private final RealmsScreenProxy proxy = new RealmsScreenProxy(this);

   public RealmsScreenProxy getProxy() {
      return this.proxy;
   }

   public void init() {
   }

   public void init(Minecraft minecraft, int var2, int var3) {
      this.minecraft = minecraft;
   }

   public void drawCenteredString(String string, int var2, int var3, int var4) {
      this.proxy.drawCenteredString(string, var2, var3, var4);
   }

   public int draw(String string, int var2, int var3, int var4, boolean var5) {
      return this.proxy.draw(string, var2, var3, var4, var5);
   }

   public void drawString(String string, int var2, int var3, int var4) {
      this.drawString(string, var2, var3, var4, true);
   }

   public void drawString(String string, int var2, int var3, int var4, boolean var5) {
      this.proxy.drawString(string, var2, var3, var4, false);
   }

   public void blit(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.proxy.blit(var1, var2, var3, var4, var5, var6);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7, int var8, int var9) {
      GuiComponent.blit(var0, var1, var6, var7, var2, var3, var4, var5, var8, var9);
   }

   public static void blit(int var0, int var1, float var2, float var3, int var4, int var5, int var6, int var7) {
      GuiComponent.blit(var0, var1, var2, var3, var4, var5, var6, var7);
   }

   public void fillGradient(int var1, int var2, int var3, int var4, int var5, int var6) {
      this.proxy.fillGradient(var1, var2, var3, var4, var5, var6);
   }

   public void renderBackground() {
      this.proxy.renderBackground();
   }

   public boolean isPauseScreen() {
      return this.proxy.isPauseScreen();
   }

   public void renderBackground(int i) {
      this.proxy.renderBackground(i);
   }

   public void render(int var1, int var2, float var3) {
      for(int var4 = 0; var4 < this.proxy.buttons().size(); ++var4) {
         ((AbstractRealmsButton)this.proxy.buttons().get(var4)).render(var1, var2, var3);
      }

   }

   public void renderTooltip(ItemStack itemStack, int var2, int var3) {
      this.proxy.renderTooltip(itemStack, var2, var3);
   }

   public void renderTooltip(String string, int var2, int var3) {
      this.proxy.renderTooltip(string, var2, var3);
   }

   public void renderTooltip(List list, int var2, int var3) {
      this.proxy.renderTooltip(list, var2, var3);
   }

   public static void bind(String string) {
      Realms.bind(string);
   }

   public void tick() {
      this.tickButtons();
   }

   protected void tickButtons() {
      for(AbstractRealmsButton<?> var2 : this.buttons()) {
         var2.tick();
      }

   }

   public int width() {
      return this.proxy.width;
   }

   public int height() {
      return this.proxy.height;
   }

   public int fontLineHeight() {
      return this.proxy.fontLineHeight();
   }

   public int fontWidth(String string) {
      return this.proxy.fontWidth(string);
   }

   public void fontDrawShadow(String string, int var2, int var3, int var4) {
      this.proxy.fontDrawShadow(string, var2, var3, var4);
   }

   public List fontSplit(String string, int var2) {
      return this.proxy.fontSplit(string, var2);
   }

   public void childrenClear() {
      this.proxy.childrenClear();
   }

   public void addWidget(RealmsGuiEventListener realmsGuiEventListener) {
      this.proxy.addWidget(realmsGuiEventListener);
   }

   public void removeWidget(RealmsGuiEventListener realmsGuiEventListener) {
      this.proxy.removeWidget(realmsGuiEventListener);
   }

   public boolean hasWidget(RealmsGuiEventListener realmsGuiEventListener) {
      return this.proxy.hasWidget(realmsGuiEventListener);
   }

   public void buttonsAdd(AbstractRealmsButton abstractRealmsButton) {
      this.proxy.buttonsAdd(abstractRealmsButton);
   }

   public List buttons() {
      return this.proxy.buttons();
   }

   protected void buttonsClear() {
      this.proxy.buttonsClear();
   }

   protected void focusOn(RealmsGuiEventListener realmsGuiEventListener) {
      this.proxy.magicalSpecialHackyFocus(realmsGuiEventListener.getProxy());
   }

   public RealmsEditBox newEditBox(int var1, int var2, int var3, int var4, int var5) {
      return this.newEditBox(var1, var2, var3, var4, var5, "");
   }

   public RealmsEditBox newEditBox(int var1, int var2, int var3, int var4, int var5, String string) {
      return new RealmsEditBox(var1, var2, var3, var4, var5, string);
   }

   public void confirmResult(boolean var1, int var2) {
   }

   public static String getLocalizedString(String string) {
      return Realms.getLocalizedString(string, new Object[0]);
   }

   public static String getLocalizedString(String var0, Object... objects) {
      return Realms.getLocalizedString(var0, objects);
   }

   public List getLocalizedStringWithLineWidth(String string, int var2) {
      return this.minecraft.font.split(I18n.get(string, new Object[0]), var2);
   }

   public RealmsAnvilLevelStorageSource getLevelStorageSource() {
      return new RealmsAnvilLevelStorageSource(Minecraft.getInstance().getLevelSource());
   }

   public void removed() {
   }

   protected void removeButton(RealmsButton realmsButton) {
      this.proxy.removeButton(realmsButton);
   }

   protected void setKeyboardHandlerSendRepeatsToGui(boolean keyboardHandlerSendRepeatsToGui) {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(keyboardHandlerSendRepeatsToGui);
   }

   protected boolean isKeyDown(int i) {
      return InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), i);
   }

   protected void narrateLabels() {
      this.getProxy().narrateLabels();
   }

   public boolean isFocused(RealmsGuiEventListener realmsGuiEventListener) {
      return this.getProxy().getFocused() == realmsGuiEventListener.getProxy();
   }
}
