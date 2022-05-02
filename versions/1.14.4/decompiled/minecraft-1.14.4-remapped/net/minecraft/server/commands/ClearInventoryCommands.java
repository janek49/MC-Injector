package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemPredicateArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class ClearInventoryCommands {
   private static final DynamicCommandExceptionType ERROR_SINGLE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("clear.failed.single", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_MULTIPLE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("clear.failed.multiple", new Object[]{object});
   });

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("clear").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).executes((commandContext) -> {
         return clearInventory((CommandSourceStack)commandContext.getSource(), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getPlayerOrException()), (itemStack) -> {
            return true;
         }, -1);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((commandContext) -> {
         return clearInventory((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), (itemStack) -> {
            return true;
         }, -1);
      })).then(((RequiredArgumentBuilder)Commands.argument("item", ItemPredicateArgument.itemPredicate()).executes((commandContext) -> {
         return clearInventory((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ItemPredicateArgument.getItemPredicate(commandContext, "item"), -1);
      })).then(Commands.argument("maxCount", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return clearInventory((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), ItemPredicateArgument.getItemPredicate(commandContext, "item"), IntegerArgumentType.getInteger(commandContext, "maxCount"));
      })))));
   }

   private static int clearInventory(CommandSourceStack commandSourceStack, Collection collection, Predicate predicate, int var3) throws CommandSyntaxException {
      int var4 = 0;

      for(ServerPlayer var6 : collection) {
         var4 += var6.inventory.clearInventory(predicate, var3);
         var6.containerMenu.broadcastChanges();
         var6.broadcastCarriedItem();
      }

      if(var4 == 0) {
         if(collection.size() == 1) {
            throw ERROR_SINGLE.create(((ServerPlayer)collection.iterator().next()).getName().getColoredString());
         } else {
            throw ERROR_MULTIPLE.create(Integer.valueOf(collection.size()));
         }
      } else {
         if(var3 == 0) {
            if(collection.size() == 1) {
               commandSourceStack.sendSuccess(new TranslatableComponent("commands.clear.test.single", new Object[]{Integer.valueOf(var4), ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
            } else {
               commandSourceStack.sendSuccess(new TranslatableComponent("commands.clear.test.multiple", new Object[]{Integer.valueOf(var4), Integer.valueOf(collection.size())}), true);
            }
         } else if(collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.clear.success.single", new Object[]{Integer.valueOf(var4), ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.clear.success.multiple", new Object[]{Integer.valueOf(var4), Integer.valueOf(collection.size())}), true);
         }

         return var4;
      }
   }
}
