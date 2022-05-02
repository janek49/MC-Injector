package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ItemEnchantmentArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

public class EnchantCommand {
   private static final DynamicCommandExceptionType ERROR_NOT_LIVING_ENTITY = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.enchant.failed.entity", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_NO_ITEM = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.enchant.failed.itemless", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_INCOMPATIBLE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.enchant.failed.incompatible", new Object[]{object});
   });
   private static final Dynamic2CommandExceptionType ERROR_LEVEL_TOO_HIGH = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.enchant.failed.level", new Object[]{var0, var1});
   });
   private static final SimpleCommandExceptionType ERROR_NOTHING_HAPPENED = new SimpleCommandExceptionType(new TranslatableComponent("commands.enchant.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("enchant").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.entities()).then(((RequiredArgumentBuilder)Commands.argument("enchantment", ItemEnchantmentArgument.enchantment()).executes((commandContext) -> {
         return enchant((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), ItemEnchantmentArgument.getEnchantment(commandContext, "enchantment"), 1);
      })).then(Commands.argument("level", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return enchant((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), ItemEnchantmentArgument.getEnchantment(commandContext, "enchantment"), IntegerArgumentType.getInteger(commandContext, "level"));
      })))));
   }

   private static int enchant(CommandSourceStack commandSourceStack, Collection collection, Enchantment enchantment, int var3) throws CommandSyntaxException {
      if(var3 > enchantment.getMaxLevel()) {
         throw ERROR_LEVEL_TOO_HIGH.create(Integer.valueOf(var3), Integer.valueOf(enchantment.getMaxLevel()));
      } else {
         int var4 = 0;

         for(Entity var6 : collection) {
            if(var6 instanceof LivingEntity) {
               LivingEntity var7 = (LivingEntity)var6;
               ItemStack var8 = var7.getMainHandItem();
               if(!var8.isEmpty()) {
                  if(enchantment.canEnchant(var8) && EnchantmentHelper.isEnchantmentCompatible(EnchantmentHelper.getEnchantments(var8).keySet(), enchantment)) {
                     var8.enchant(enchantment, var3);
                     ++var4;
                  } else if(collection.size() == 1) {
                     throw ERROR_INCOMPATIBLE.create(var8.getItem().getName(var8).getString());
                  }
               } else if(collection.size() == 1) {
                  throw ERROR_NO_ITEM.create(var7.getName().getString());
               }
            } else if(collection.size() == 1) {
               throw ERROR_NOT_LIVING_ENTITY.create(var6.getName().getString());
            }
         }

         if(var4 == 0) {
            throw ERROR_NOTHING_HAPPENED.create();
         } else {
            if(collection.size() == 1) {
               commandSourceStack.sendSuccess(new TranslatableComponent("commands.enchant.success.single", new Object[]{enchantment.getFullname(var3), ((Entity)collection.iterator().next()).getDisplayName()}), true);
            } else {
               commandSourceStack.sendSuccess(new TranslatableComponent("commands.enchant.success.multiple", new Object[]{enchantment.getFullname(var3), Integer.valueOf(collection.size())}), true);
            }

            return var4;
         }
      }
   }
}
