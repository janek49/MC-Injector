package net.minecraft.client.gui.components;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;

@ClientJarOnly
public class ComponentRenderUtils {
   public static String stripColor(String var0, boolean var1) {
      return !var1 && !Minecraft.getInstance().options.chatColors?ChatFormatting.stripFormatting(var0):var0;
   }

   public static List wrapComponents(Component component, int var1, Font font, boolean var3, boolean var4) {
      int var5 = 0;
      Component var6 = new TextComponent("");
      List<Component> var7 = Lists.newArrayList();
      List<Component> var8 = Lists.newArrayList(component);

      for(int var9 = 0; var9 < ((List)var8).size(); ++var9) {
         Component var10 = (Component)var8.get(var9);
         String var11 = var10.getContents();
         boolean var12 = false;
         if(var11.contains("\n")) {
            int var13 = var11.indexOf(10);
            String var14 = var11.substring(var13 + 1);
            var11 = var11.substring(0, var13 + 1);
            Component var15 = (new TextComponent(var14)).setStyle(var10.getStyle().copy());
            var8.add(var9 + 1, var15);
            var12 = true;
         }

         String var13 = stripColor(var10.getStyle().getLegacyFormatCodes() + var11, var4);
         String var14 = var13.endsWith("\n")?var13.substring(0, var13.length() - 1):var13;
         int var15 = font.width(var14);
         Component var16 = (new TextComponent(var14)).setStyle(var10.getStyle().copy());
         if(var5 + var15 > var1) {
            String var17 = font.substrByWidth(var13, var1 - var5, false);
            String var18 = var17.length() < var13.length()?var13.substring(var17.length()):null;
            if(var18 != null && !var18.isEmpty()) {
               int var19 = var18.charAt(0) != 32?var17.lastIndexOf(32):var17.length();
               if(var19 >= 0 && font.width(var13.substring(0, var19)) > 0) {
                  var17 = var13.substring(0, var19);
                  if(var3) {
                     ++var19;
                  }

                  var18 = var13.substring(var19);
               } else if(var5 > 0 && !var13.contains(" ")) {
                  var17 = "";
                  var18 = var13;
               }

               Component var20 = (new TextComponent(var18)).setStyle(var10.getStyle().copy());
               var8.add(var9 + 1, var20);
            }

            var15 = font.width(var17);
            var16 = new TextComponent(var17);
            var16.setStyle(var10.getStyle().copy());
            var12 = true;
         }

         if(var5 + var15 <= var1) {
            var5 += var15;
            var6.append(var16);
         } else {
            var12 = true;
         }

         if(var12) {
            var7.add(var6);
            var5 = 0;
            var6 = new TextComponent("");
         }
      }

      var7.add(var6);
      return var7;
   }
}
