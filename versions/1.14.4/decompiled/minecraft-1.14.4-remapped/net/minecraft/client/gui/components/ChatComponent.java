package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.GuiMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.ComponentRenderUtils;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ChatComponent extends GuiComponent {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Minecraft minecraft;
   private final List recentChat = Lists.newArrayList();
   private final List allMessages = Lists.newArrayList();
   private final List trimmedMessages = Lists.newArrayList();
   private int chatScrollbarPos;
   private boolean newMessageSinceScroll;

   public ChatComponent(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void render(int i) {
      if(this.minecraft.options.chatVisibility != ChatVisiblity.HIDDEN) {
         int var2 = this.getLinesPerPage();
         int var3 = this.trimmedMessages.size();
         if(var3 > 0) {
            boolean var4 = false;
            if(this.isChatFocused()) {
               var4 = true;
            }

            double var5 = this.getScale();
            int var7 = Mth.ceil((double)this.getWidth() / var5);
            GlStateManager.pushMatrix();
            GlStateManager.translatef(2.0F, 8.0F, 0.0F);
            GlStateManager.scaled(var5, var5, 1.0D);
            double var8 = this.minecraft.options.chatOpacity * 0.8999999761581421D + 0.10000000149011612D;
            double var10 = this.minecraft.options.textBackgroundOpacity;
            int var12 = 0;

            for(int var13 = 0; var13 + this.chatScrollbarPos < this.trimmedMessages.size() && var13 < var2; ++var13) {
               GuiMessage var14 = (GuiMessage)this.trimmedMessages.get(var13 + this.chatScrollbarPos);
               if(var14 != null) {
                  int var15 = i - var14.getAddedTime();
                  if(var15 < 200 || var4) {
                     double var16 = var4?1.0D:getTimeFactor(var15);
                     int var18 = (int)(255.0D * var16 * var8);
                     int var19 = (int)(255.0D * var16 * var10);
                     ++var12;
                     if(var18 > 3) {
                        int var20 = 0;
                        int var21 = -var13 * 9;
                        fill(-2, var21 - 9, 0 + var7 + 4, var21, var19 << 24);
                        String var22 = var14.getMessage().getColoredString();
                        GlStateManager.enableBlend();
                        this.minecraft.font.drawShadow(var22, 0.0F, (float)(var21 - 8), 16777215 + (var18 << 24));
                        GlStateManager.disableAlphaTest();
                        GlStateManager.disableBlend();
                     }
                  }
               }
            }

            if(var4) {
               this.minecraft.font.getClass();
               int var13 = 9;
               GlStateManager.translatef(-3.0F, 0.0F, 0.0F);
               int var14 = var3 * var13 + var3;
               int var15 = var12 * var13 + var12;
               int var16 = this.chatScrollbarPos * var15 / var3;
               int var17 = var15 * var15 / var14;
               if(var14 != var15) {
                  int var18 = var16 > 0?170:96;
                  int var19 = this.newMessageSinceScroll?13382451:3355562;
                  fill(0, -var16, 2, -var16 - var17, var19 + (var18 << 24));
                  fill(2, -var16, 1, -var16 - var17, 13421772 + (var18 << 24));
               }
            }

            GlStateManager.popMatrix();
         }
      }
   }

   private static double getTimeFactor(int i) {
      double var1 = (double)i / 200.0D;
      var1 = 1.0D - var1;
      var1 = var1 * 10.0D;
      var1 = Mth.clamp(var1, 0.0D, 1.0D);
      var1 = var1 * var1;
      return var1;
   }

   public void clearMessages(boolean b) {
      this.trimmedMessages.clear();
      this.allMessages.clear();
      if(b) {
         this.recentChat.clear();
      }

   }

   public void addMessage(Component component) {
      this.addMessage(component, 0);
   }

   public void addMessage(Component component, int var2) {
      this.addMessage(component, var2, this.minecraft.gui.getGuiTicks(), false);
      LOGGER.info("[CHAT] {}", component.getString().replaceAll("\r", "\\\\r").replaceAll("\n", "\\\\n"));
   }

   private void addMessage(Component component, int var2, int var3, boolean var4) {
      if(var2 != 0) {
         this.removeById(var2);
      }

      int var5 = Mth.floor((double)this.getWidth() / this.getScale());
      List<Component> var6 = ComponentRenderUtils.wrapComponents(component, var5, this.minecraft.font, false, false);
      boolean var7 = this.isChatFocused();

      for(Component var9 : var6) {
         if(var7 && this.chatScrollbarPos > 0) {
            this.newMessageSinceScroll = true;
            this.scrollChat(1.0D);
         }

         this.trimmedMessages.add(0, new GuiMessage(var3, var9, var2));
      }

      while(this.trimmedMessages.size() > 100) {
         this.trimmedMessages.remove(this.trimmedMessages.size() - 1);
      }

      if(!var4) {
         this.allMessages.add(0, new GuiMessage(var3, component, var2));

         while(this.allMessages.size() > 100) {
            this.allMessages.remove(this.allMessages.size() - 1);
         }
      }

   }

   public void rescaleChat() {
      this.trimmedMessages.clear();
      this.resetChatScroll();

      for(int var1 = this.allMessages.size() - 1; var1 >= 0; --var1) {
         GuiMessage var2 = (GuiMessage)this.allMessages.get(var1);
         this.addMessage(var2.getMessage(), var2.getId(), var2.getAddedTime(), true);
      }

   }

   public List getRecentChat() {
      return this.recentChat;
   }

   public void addRecentChat(String string) {
      if(this.recentChat.isEmpty() || !((String)this.recentChat.get(this.recentChat.size() - 1)).equals(string)) {
         this.recentChat.add(string);
      }

   }

   public void resetChatScroll() {
      this.chatScrollbarPos = 0;
      this.newMessageSinceScroll = false;
   }

   public void scrollChat(double d) {
      this.chatScrollbarPos = (int)((double)this.chatScrollbarPos + d);
      int var3 = this.trimmedMessages.size();
      if(this.chatScrollbarPos > var3 - this.getLinesPerPage()) {
         this.chatScrollbarPos = var3 - this.getLinesPerPage();
      }

      if(this.chatScrollbarPos <= 0) {
         this.chatScrollbarPos = 0;
         this.newMessageSinceScroll = false;
      }

   }

   @Nullable
   public Component getClickedComponentAt(double var1, double var3) {
      if(!this.isChatFocused()) {
         return null;
      } else {
         double var5 = this.getScale();
         double var7 = var1 - 2.0D;
         double var9 = (double)this.minecraft.window.getGuiScaledHeight() - var3 - 40.0D;
         var7 = (double)Mth.floor(var7 / var5);
         var9 = (double)Mth.floor(var9 / var5);
         if(var7 >= 0.0D && var9 >= 0.0D) {
            int var11 = Math.min(this.getLinesPerPage(), this.trimmedMessages.size());
            if(var7 <= (double)Mth.floor((double)this.getWidth() / this.getScale())) {
               this.minecraft.font.getClass();
               if(var9 < (double)(9 * var11 + var11)) {
                  this.minecraft.font.getClass();
                  int var12 = (int)(var9 / 9.0D + (double)this.chatScrollbarPos);
                  if(var12 >= 0 && var12 < this.trimmedMessages.size()) {
                     GuiMessage var13 = (GuiMessage)this.trimmedMessages.get(var12);
                     int var14 = 0;

                     for(Component var16 : var13.getMessage()) {
                        if(var16 instanceof TextComponent) {
                           var14 += this.minecraft.font.width(ComponentRenderUtils.stripColor(((TextComponent)var16).getText(), false));
                           if((double)var14 > var7) {
                              return var16;
                           }
                        }
                     }
                  }

                  return null;
               }
            }

            return null;
         } else {
            return null;
         }
      }
   }

   public boolean isChatFocused() {
      return this.minecraft.screen instanceof ChatScreen;
   }

   public void removeById(int i) {
      Iterator<GuiMessage> var2 = this.trimmedMessages.iterator();

      while(var2.hasNext()) {
         GuiMessage var3 = (GuiMessage)var2.next();
         if(var3.getId() == i) {
            var2.remove();
         }
      }

      var2 = this.allMessages.iterator();

      while(var2.hasNext()) {
         GuiMessage var3 = (GuiMessage)var2.next();
         if(var3.getId() == i) {
            var2.remove();
            break;
         }
      }

   }

   public int getWidth() {
      return getWidth(this.minecraft.options.chatWidth);
   }

   public int getHeight() {
      return getHeight(this.isChatFocused()?this.minecraft.options.chatHeightFocused:this.minecraft.options.chatHeightUnfocused);
   }

   public double getScale() {
      return this.minecraft.options.chatScale;
   }

   public static int getWidth(double d) {
      int var2 = 320;
      int var3 = 40;
      return Mth.floor(d * 280.0D + 40.0D);
   }

   public static int getHeight(double d) {
      int var2 = 180;
      int var3 = 20;
      return Mth.floor(d * 160.0D + 20.0D);
   }

   public int getLinesPerPage() {
      return this.getHeight() / 9;
   }
}
