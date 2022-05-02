package net.minecraft.network.chat;

import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Message;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ContextAwareComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.Entity;

public class ComponentUtils {
   public static Component mergeStyles(Component var0, Style style) {
      return style.isEmpty()?var0:(var0.getStyle().isEmpty()?var0.setStyle(style.copy()):(new TextComponent("")).append(var0).setStyle(style.copy()));
   }

   public static Component updateForEntity(@Nullable CommandSourceStack commandSourceStack, Component var1, @Nullable Entity entity, int var3) throws CommandSyntaxException {
      if(var3 > 100) {
         return var1;
      } else {
         ++var3;
         Component var4 = var1 instanceof ContextAwareComponent?((ContextAwareComponent)var1).resolve(commandSourceStack, entity, var3):var1.copy();

         for(Component var6 : var1.getSiblings()) {
            var4.append(updateForEntity(commandSourceStack, var6, entity, var3));
         }

         return mergeStyles(var4, var1.getStyle());
      }
   }

   public static Component getDisplayName(GameProfile gameProfile) {
      return gameProfile.getName() != null?new TextComponent(gameProfile.getName()):(gameProfile.getId() != null?new TextComponent(gameProfile.getId().toString()):new TextComponent("(unknown)"));
   }

   public static Component formatList(Collection collection) {
      return formatAndSortList(collection, (string) -> {
         return (new TextComponent(string)).withStyle(ChatFormatting.GREEN);
      });
   }

   public static Component formatAndSortList(Collection collection, Function function) {
      if(collection.isEmpty()) {
         return new TextComponent("");
      } else if(collection.size() == 1) {
         return (Component)function.apply(collection.iterator().next());
      } else {
         List<T> var2 = Lists.newArrayList(collection);
         var2.sort(Comparable::compareTo);
         return formatList(collection, function);
      }
   }

   public static Component formatList(Collection collection, Function function) {
      if(collection.isEmpty()) {
         return new TextComponent("");
      } else if(collection.size() == 1) {
         return (Component)function.apply(collection.iterator().next());
      } else {
         Component component = new TextComponent("");
         boolean var3 = true;

         for(T var5 : collection) {
            if(!var3) {
               component.append((new TextComponent(", ")).withStyle(ChatFormatting.GRAY));
            }

            component.append((Component)function.apply(var5));
            var3 = false;
         }

         return component;
      }
   }

   public static Component wrapInSquareBrackets(Component component) {
      return (new TextComponent("[")).append(component).append("]");
   }

   public static Component fromMessage(Message message) {
      return (Component)(message instanceof Component?(Component)message:new TextComponent(message.getString()));
   }
}
