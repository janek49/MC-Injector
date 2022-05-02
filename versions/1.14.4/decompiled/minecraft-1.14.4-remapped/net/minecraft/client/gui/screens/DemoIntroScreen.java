package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.Util;
import net.minecraft.client.Options;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

@ClientJarOnly
public class DemoIntroScreen extends Screen {
   private static final ResourceLocation DEMO_BACKGROUND_LOCATION = new ResourceLocation("textures/gui/demo_background.png");

   public DemoIntroScreen() {
      super(new TranslatableComponent("demo.help.title", new Object[0]));
   }

   protected void init() {
      int var1 = -16;
      this.addButton(new Button(this.width / 2 - 116, this.height / 2 + 62 + -16, 114, 20, I18n.get("demo.help.buy", new Object[0]), (button) -> {
         button.active = false;
         Util.getPlatform().openUri("http://www.minecraft.net/store?source=demo");
      }));
      this.addButton(new Button(this.width / 2 + 2, this.height / 2 + 62 + -16, 114, 20, I18n.get("demo.help.later", new Object[0]), (button) -> {
         this.minecraft.setScreen((Screen)null);
         this.minecraft.mouseHandler.grabMouse();
      }));
   }

   public void renderBackground() {
      super.renderBackground();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(DEMO_BACKGROUND_LOCATION);
      int var1 = (this.width - 248) / 2;
      int var2 = (this.height - 166) / 2;
      this.blit(var1, var2, 0, 0, 248, 166);
   }

   public void render(int var1, int var2, float var3) {
      this.renderBackground();
      int var4 = (this.width - 248) / 2 + 10;
      int var5 = (this.height - 166) / 2 + 8;
      this.font.draw(this.title.getColoredString(), (float)var4, (float)var5, 2039583);
      var5 = var5 + 12;
      Options var6 = this.minecraft.options;
      this.font.draw(I18n.get("demo.help.movementShort", new Object[]{var6.keyUp.getTranslatedKeyMessage(), var6.keyLeft.getTranslatedKeyMessage(), var6.keyDown.getTranslatedKeyMessage(), var6.keyRight.getTranslatedKeyMessage()}), (float)var4, (float)var5, 5197647);
      this.font.draw(I18n.get("demo.help.movementMouse", new Object[0]), (float)var4, (float)(var5 + 12), 5197647);
      this.font.draw(I18n.get("demo.help.jump", new Object[]{var6.keyJump.getTranslatedKeyMessage()}), (float)var4, (float)(var5 + 24), 5197647);
      this.font.draw(I18n.get("demo.help.inventory", new Object[]{var6.keyInventory.getTranslatedKeyMessage()}), (float)var4, (float)(var5 + 36), 5197647);
      this.font.drawWordWrap(I18n.get("demo.help.fullWrapped", new Object[0]), var4, var5 + 68, 218, 2039583);
      super.render(var1, var2, var3);
   }
}
