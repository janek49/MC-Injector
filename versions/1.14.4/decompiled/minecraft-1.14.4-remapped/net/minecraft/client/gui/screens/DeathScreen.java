package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.ConfirmScreen;
import net.minecraft.client.gui.screens.GenericDirtMessageScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

@ClientJarOnly
public class DeathScreen extends Screen {
   private int delayTicker;
   private final Component causeOfDeath;
   private final boolean hardcore;

   public DeathScreen(@Nullable Component causeOfDeath, boolean hardcore) {
      super(new TranslatableComponent(hardcore?"deathScreen.title.hardcore":"deathScreen.title", new Object[0]));
      this.causeOfDeath = causeOfDeath;
      this.hardcore = hardcore;
   }

   protected void init() {
      this.delayTicker = 0;
      String var1;
      String var2;
      if(this.hardcore) {
         var1 = I18n.get("deathScreen.spectate", new Object[0]);
         var2 = I18n.get("deathScreen." + (this.minecraft.isLocalServer()?"deleteWorld":"leaveServer"), new Object[0]);
      } else {
         var1 = I18n.get("deathScreen.respawn", new Object[0]);
         var2 = I18n.get("deathScreen.titleScreen", new Object[0]);
      }

      this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 72, 200, 20, var1, (button) -> {
         this.minecraft.player.respawn();
         this.minecraft.setScreen((Screen)null);
      }));
      Button var3 = (Button)this.addButton(new Button(this.width / 2 - 100, this.height / 4 + 96, 200, 20, var2, (button) -> {
         if(this.hardcore) {
            this.minecraft.setScreen(new TitleScreen());
         } else {
            ConfirmScreen var2 = new ConfirmScreen(this::confirmResult, new TranslatableComponent("deathScreen.quit.confirm", new Object[0]), new TextComponent(""), I18n.get("deathScreen.titleScreen", new Object[0]), I18n.get("deathScreen.respawn", new Object[0]));
            this.minecraft.setScreen(var2);
            var2.setDelay(20);
         }
      }));
      if(!this.hardcore && this.minecraft.getUser() == null) {
         var3.active = false;
      }

      for(AbstractWidget var5 : this.buttons) {
         var5.active = false;
      }

   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   private void confirmResult(boolean b) {
      if(b) {
         if(this.minecraft.level != null) {
            this.minecraft.level.disconnect();
         }

         this.minecraft.clearLevel(new GenericDirtMessageScreen(new TranslatableComponent("menu.savingLevel", new Object[0])));
         this.minecraft.setScreen(new TitleScreen());
      } else {
         this.minecraft.player.respawn();
         this.minecraft.setScreen((Screen)null);
      }

   }

   public void render(int var1, int var2, float var3) {
      this.fillGradient(0, 0, this.width, this.height, 1615855616, -1602211792);
      GlStateManager.pushMatrix();
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      this.drawCenteredString(this.font, this.title.getColoredString(), this.width / 2 / 2, 30, 16777215);
      GlStateManager.popMatrix();
      if(this.causeOfDeath != null) {
         this.drawCenteredString(this.font, this.causeOfDeath.getColoredString(), this.width / 2, 85, 16777215);
      }

      this.drawCenteredString(this.font, I18n.get("deathScreen.score", new Object[0]) + ": " + ChatFormatting.YELLOW + this.minecraft.player.getScore(), this.width / 2, 100, 16777215);
      if(this.causeOfDeath != null && var2 > 85) {
         this.font.getClass();
         if(var2 < 85 + 9) {
            Component var4 = this.getClickedComponentAt(var1);
            if(var4 != null && var4.getStyle().getHoverEvent() != null) {
               this.renderComponentHoverEffect(var4, var1, var2);
            }
         }
      }

      super.render(var1, var2, var3);
   }

   @Nullable
   public Component getClickedComponentAt(int i) {
      if(this.causeOfDeath == null) {
         return null;
      } else {
         int var2 = this.minecraft.font.width(this.causeOfDeath.getColoredString());
         int var3 = this.width / 2 - var2 / 2;
         int var4 = this.width / 2 + var2 / 2;
         int var5 = var3;
         if(i >= var3 && i <= var4) {
            for(Component var7 : this.causeOfDeath) {
               var5 += this.minecraft.font.width(ComponentRenderUtils.stripColor(var7.getContents(), false));
               if(var5 > i) {
                  return var7;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public boolean mouseClicked(double var1, double var3, int var5) {
      if(this.causeOfDeath != null && var3 > 85.0D) {
         this.font.getClass();
         if(var3 < (double)(85 + 9)) {
            Component var6 = this.getClickedComponentAt((int)var1);
            if(var6 != null && var6.getStyle().getClickEvent() != null && var6.getStyle().getClickEvent().getAction() == ClickEvent.Action.OPEN_URL) {
               this.handleComponentClicked(var6);
               return false;
            }
         }
      }

      return super.mouseClicked(var1, var3, var5);
   }

   public boolean isPauseScreen() {
      return false;
   }

   public void tick() {
      super.tick();
      ++this.delayTicker;
      if(this.delayTicker == 20) {
         for(AbstractWidget var2 : this.buttons) {
            var2.active = true;
         }
      }

   }
}
