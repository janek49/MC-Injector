package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

public class GiveCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("give").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((commandContext) -> {
         return giveItem((CommandSourceStack)commandContext.getSource(), ItemArgument.getItem(commandContext, "item"), EntityArgument.getPlayers(commandContext, "targets"), 1);
      })).then(Commands.argument("count", IntegerArgumentType.integer(1)).executes((commandContext) -> {
         return giveItem((CommandSourceStack)commandContext.getSource(), ItemArgument.getItem(commandContext, "item"), EntityArgument.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "count"));
      })))));
   }

   private static int giveItem(CommandSourceStack commandSourceStack, ItemInput itemInput, Collection collection, int var3) throws CommandSyntaxException {
      for(ServerPlayer var5 : collection) {
         int var6 = var3;

         while(var6 > 0) {
            int var7 = Math.min(itemInput.getItem().getMaxStackSize(), var6);
            var6 -= var7;
            ItemStack var8 = itemInput.createItemStack(var7, false);
            boolean var9 = var5.inventory.add(var8);
            if(var9 && var8.isEmpty()) {
               var8.setCount(1);
               ItemEntity var10 = var5.drop(var8, false);
               if(var10 != null) {
                  var10.makeFakeItem();
               }

               var5.level.playSound((Player)null, var5.x, var5.y, var5.z, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.2F, ((var5.getRandom().nextFloat() - var5.getRandom().nextFloat()) * 0.7F + 1.0F) * 2.0F);
               var5.inventoryMenu.broadcastChanges();
            } else {
               ItemEntity var10 = var5.drop(var8, false);
               if(var10 != null) {
                  var10.setNoPickUpDelay();
                  var10.setOwner(var5.getUUID());
               }
            }
         }
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.give.success.single", new Object[]{Integer.valueOf(var3), itemInput.createItemStack(var3, false).getDisplayName(), ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.give.success.single", new Object[]{Integer.valueOf(var3), itemInput.createItemStack(var3, false).getDisplayName(), Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }
}
