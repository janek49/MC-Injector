package net.minecraft.client.gui.screens;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.JsonSyntaxException;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.AbstractContainerEventHandler;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public abstract class Screen extends AbstractContainerEventHandler implements Widget {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Set ALLOWED_PROTOCOLS = Sets.newHashSet(new String[]{"http", "https"});
   protected final Component title;
   protected final List children = Lists.newArrayList();
   @Nullable
   protected Minecraft minecraft;
   protected ItemRenderer itemRenderer;
   public int width;
   public int height;
   protected final List buttons = Lists.newArrayList();
   public boolean passEvents;
   protected Font font;
   private URI clickedLink;

   protected Screen(Component title) {
      this.title = title;
   }

   public Component getTitle() {
      return this.title;
   }

   public String getNarrationMessage() {
      return this.getTitle().getString();
   }

   public void render(int var1, int var2, float var3) {
      for(int var4 = 0; var4 < this.buttons.size(); ++var4) {
         ((AbstractWidget)this.buttons.get(var4)).render(var1, var2, var3);
      }

   }

   public boolean keyPressed(int var1, int var2, int var3) {
      if(var1 == 256 && this.shouldCloseOnEsc()) {
         this.onClose();
         return true;
      } else if(var1 == 258) {
         boolean var4 = !hasShiftDown();
         if(!this.changeFocus(var4)) {
            this.changeFocus(var4);
         }

         return true;
      } else {
         return super.keyPressed(var1, var2, var3);
      }
   }

   public boolean shouldCloseOnEsc() {
      return true;
   }

   public void onClose() {
      this.minecraft.setScreen((Screen)null);
   }

   protected AbstractWidget addButton(AbstractWidget abstractWidget) {
      this.buttons.add(abstractWidget);
      this.children.add(abstractWidget);
      return abstractWidget;
   }

   protected void renderTooltip(ItemStack itemStack, int var2, int var3) {
      this.renderTooltip(this.getTooltipFromItem(itemStack), var2, var3);
   }

   public List getTooltipFromItem(ItemStack itemStack) {
      List<Component> list = itemStack.getTooltipLines(this.minecraft.player, this.minecraft.options.advancedItemTooltips?TooltipFlag.Default.ADVANCED:TooltipFlag.Default.NORMAL);
      List<String> var3 = Lists.newArrayList();

      for(Component var5 : list) {
         var3.add(var5.getColoredString());
      }

      return var3;
   }

   public void renderTooltip(String string, int var2, int var3) {
      this.renderTooltip(Arrays.asList(new String[]{string}), var2, var3);
   }

   public void renderTooltip(List list, int var2, int var3) {
      if(!list.isEmpty()) {
         GlStateManager.disableRescaleNormal();
         Lighting.turnOff();
         GlStateManager.disableLighting();
         GlStateManager.disableDepthTest();
         int var4 = 0;

         for(String var6 : list) {
            int var7 = this.font.width(var6);
            if(var7 > var4) {
               var4 = var7;
            }
         }

         int var5 = var2 + 12;
         int var6 = var3 - 12;
         int var8 = 8;
         if(list.size() > 1) {
            var8 += 2 + (list.size() - 1) * 10;
         }

         if(var5 + var4 > this.width) {
            var5 -= 28 + var4;
         }

         if(var6 + var8 + 6 > this.height) {
            var6 = this.height - var8 - 6;
         }

         this.blitOffset = 300;
         this.itemRenderer.blitOffset = 300.0F;
         int var9 = -267386864;
         this.fillGradient(var5 - 3, var6 - 4, var5 + var4 + 3, var6 - 3, -267386864, -267386864);
         this.fillGradient(var5 - 3, var6 + var8 + 3, var5 + var4 + 3, var6 + var8 + 4, -267386864, -267386864);
         this.fillGradient(var5 - 3, var6 - 3, var5 + var4 + 3, var6 + var8 + 3, -267386864, -267386864);
         this.fillGradient(var5 - 4, var6 - 3, var5 - 3, var6 + var8 + 3, -267386864, -267386864);
         this.fillGradient(var5 + var4 + 3, var6 - 3, var5 + var4 + 4, var6 + var8 + 3, -267386864, -267386864);
         int var10 = 1347420415;
         int var11 = 1344798847;
         this.fillGradient(var5 - 3, var6 - 3 + 1, var5 - 3 + 1, var6 + var8 + 3 - 1, 1347420415, 1344798847);
         this.fillGradient(var5 + var4 + 2, var6 - 3 + 1, var5 + var4 + 3, var6 + var8 + 3 - 1, 1347420415, 1344798847);
         this.fillGradient(var5 - 3, var6 - 3, var5 + var4 + 3, var6 - 3 + 1, 1347420415, 1347420415);
         this.fillGradient(var5 - 3, var6 + var8 + 2, var5 + var4 + 3, var6 + var8 + 3, 1344798847, 1344798847);

         for(int var12 = 0; var12 < list.size(); ++var12) {
            String var13 = (String)list.get(var12);
            this.font.drawShadow(var13, (float)var5, (float)var6, -1);
            if(var12 == 0) {
               var6 += 2;
            }

            var6 += 10;
         }

         this.blitOffset = 0;
         this.itemRenderer.blitOffset = 0.0F;
         GlStateManager.enableLighting();
         GlStateManager.enableDepthTest();
         Lighting.turnOn();
         GlStateManager.enableRescaleNormal();
      }
   }

   protected void renderComponentHoverEffect(Component component, int var2, int var3) {
      if(component != null && component.getStyle().getHoverEvent() != null) {
         HoverEvent var4 = component.getStyle().getHoverEvent();
         if(var4.getAction() == HoverEvent.Action.SHOW_ITEM) {
            ItemStack var5 = ItemStack.EMPTY;

            try {
               Tag var6 = TagParser.parseTag(var4.getValue().getString());
               if(var6 instanceof CompoundTag) {
                  var5 = ItemStack.of((CompoundTag)var6);
               }
            } catch (CommandSyntaxException var10) {
               ;
            }

            if(var5.isEmpty()) {
               this.renderTooltip(ChatFormatting.RED + "Invalid Item!", var2, var3);
            } else {
               this.renderTooltip(var5, var2, var3);
            }
         } else if(var4.getAction() == HoverEvent.Action.SHOW_ENTITY) {
            if(this.minecraft.options.advancedItemTooltips) {
               try {
                  CompoundTag var5 = TagParser.parseTag(var4.getValue().getString());
                  List<String> var6 = Lists.newArrayList();
                  Component var7 = Component.Serializer.fromJson(var5.getString("name"));
                  if(var7 != null) {
                     var6.add(var7.getColoredString());
                  }

                  if(var5.contains("type", 8)) {
                     String var8 = var5.getString("type");
                     var6.add("Type: " + var8);
                  }

                  var6.add(var5.getString("id"));
                  this.renderTooltip(var6, var2, var3);
               } catch (CommandSyntaxException | JsonSyntaxException var9) {
                  this.renderTooltip(ChatFormatting.RED + "Invalid Entity!", var2, var3);
               }
            }
         } else if(var4.getAction() == HoverEvent.Action.SHOW_TEXT) {
            this.renderTooltip(this.minecraft.font.split(var4.getValue().getColoredString(), Math.max(this.width / 2, 200)), var2, var3);
         }

         GlStateManager.disableLighting();
      }
   }

   protected void insertText(String string, boolean var2) {
   }

   public boolean handleComponentClicked(Component component) {
      if(component == null) {
         return false;
      } else {
         ClickEvent var2 = component.getStyle().getClickEvent();
         if(hasShiftDown()) {
            if(component.getStyle().getInsertion() != null) {
               this.insertText(component.getStyle().getInsertion(), false);
            }
         } else if(var2 != null) {
            if(var2.getAction() == ClickEvent.Action.OPEN_URL) {
               if(!this.minecraft.options.chatLinks) {
                  return false;
               }

               try {
                  URI var3 = new URI(var2.getValue());
                  String var4 = var3.getScheme();
                  if(var4 == null) {
                     throw new URISyntaxException(var2.getValue(), "Missing protocol");
                  }

                  if(!ALLOWED_PROTOCOLS.contains(var4.toLowerCase(Locale.ROOT))) {
                     throw new URISyntaxException(var2.getValue(), "Unsupported protocol: " + var4.toLowerCase(Locale.ROOT));
                  }

                  if(this.minecraft.options.chatLinksPrompt) {
                     this.clickedLink = var3;
                     this.minecraft.setScreen(new ConfirmLinkScreen(this::confirmLink, var2.getValue(), false));
                  } else {
                     this.openLink(var3);
                  }
               } catch (URISyntaxException var5) {
                  LOGGER.error("Can\'t open url for {}", var2, var5);
               }
            } else if(var2.getAction() == ClickEvent.Action.OPEN_FILE) {
               URI var3 = (new File(var2.getValue())).toURI();
               this.openLink(var3);
            } else if(var2.getAction() == ClickEvent.Action.SUGGEST_COMMAND) {
               this.insertText(var2.getValue(), true);
            } else if(var2.getAction() == ClickEvent.Action.RUN_COMMAND) {
               this.sendMessage(var2.getValue(), false);
            } else {
               LOGGER.error("Don\'t know how to handle {}", var2);
            }

            return true;
         }

         return false;
      }
   }

   public void sendMessage(String string) {
      this.sendMessage(string, true);
   }

   public void sendMessage(String string, boolean var2) {
      if(var2) {
         this.minecraft.gui.getChat().addRecentChat(string);
      }

      this.minecraft.player.chat(string);
   }

   public void init(Minecraft minecraft, int width, int height) {
      this.minecraft = minecraft;
      this.itemRenderer = minecraft.getItemRenderer();
      this.font = minecraft.font;
      this.width = width;
      this.height = height;
      this.buttons.clear();
      this.children.clear();
      this.setFocused((GuiEventListener)null);
      this.init();
   }

   public void setSize(int width, int height) {
      this.width = width;
      this.height = height;
   }

   public List children() {
      return this.children;
   }

   protected void init() {
   }

   public void tick() {
   }

   public void removed() {
   }

   public void renderBackground() {
      this.renderBackground(0);
   }

   public void renderBackground(int i) {
      if(this.minecraft.level != null) {
         this.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
      } else {
         this.renderDirtBackground(i);
      }

   }

   public void renderDirtBackground(int i) {
      GlStateManager.disableLighting();
      GlStateManager.disableFog();
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      this.minecraft.getTextureManager().bind(BACKGROUND_LOCATION);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      float var4 = 32.0F;
      var3.begin(7, DefaultVertexFormat.POSITION_TEX_COLOR);
      var3.vertex(0.0D, (double)this.height, 0.0D).uv(0.0D, (double)((float)this.height / 32.0F + (float)i)).color(64, 64, 64, 255).endVertex();
      var3.vertex((double)this.width, (double)this.height, 0.0D).uv((double)((float)this.width / 32.0F), (double)((float)this.height / 32.0F + (float)i)).color(64, 64, 64, 255).endVertex();
      var3.vertex((double)this.width, 0.0D, 0.0D).uv((double)((float)this.width / 32.0F), (double)i).color(64, 64, 64, 255).endVertex();
      var3.vertex(0.0D, 0.0D, 0.0D).uv(0.0D, (double)i).color(64, 64, 64, 255).endVertex();
      var2.end();
   }

   public boolean isPauseScreen() {
      return true;
   }

   private void confirmLink(boolean b) {
      if(b) {
         this.openLink(this.clickedLink);
      }

      this.clickedLink = null;
      this.minecraft.setScreen(this);
   }

   private void openLink(URI uRI) {
      Util.getPlatform().openUri(uRI);
   }

   public static boolean hasControlDown() {
      return Minecraft.ON_OSX?InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 343) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 347):InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 341) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 345);
   }

   public static boolean hasShiftDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 340) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 344);
   }

   public static boolean hasAltDown() {
      return InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 342) || InputConstants.isKeyDown(Minecraft.getInstance().window.getWindow(), 346);
   }

   public static boolean isCut(int i) {
      return i == 88 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isPaste(int i) {
      return i == 86 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isCopy(int i) {
      return i == 67 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public static boolean isSelectAll(int i) {
      return i == 65 && hasControlDown() && !hasShiftDown() && !hasAltDown();
   }

   public void resize(Minecraft minecraft, int var2, int var3) {
      this.init(minecraft, var2, var3);
   }

   public static void wrapScreenError(Runnable runnable, String var1, String var2) {
      try {
         runnable.run();
      } catch (Throwable var6) {
         CrashReport var4 = CrashReport.forThrowable(var6, var1);
         CrashReportCategory var5 = var4.addCategory("Affected screen");
         var5.setDetail("Screen name", () -> {
            return var2;
         });
         throw new ReportedException(var4);
      }
   }

   protected boolean isValidCharacterForName(String string, char var2, int var3) {
      int var4 = string.indexOf(58);
      int var5 = string.indexOf(47);
      return var2 == 58?(var5 == -1 || var3 <= var5) && var4 == -1:(var2 == 47?var3 > var4:var2 == 95 || var2 == 45 || var2 >= 97 && var2 <= 122 || var2 >= 48 && var2 <= 57 || var2 == 46);
   }

   public boolean isMouseOver(double var1, double var3) {
      return true;
   }
}
