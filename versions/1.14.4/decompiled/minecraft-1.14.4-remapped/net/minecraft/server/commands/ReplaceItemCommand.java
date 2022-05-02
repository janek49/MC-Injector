package net.minecraft.server.commands;

import com.google.common.collect.Lists;
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
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ReplaceItemCommand {
   public static final SimpleCommandExceptionType ERROR_NOT_A_CONTAINER = new SimpleCommandExceptionType(new TranslatableComponent("commands.replaceitem.block.failed", new Object[0]));
   public static final DynamicCommandExceptionType ERROR_INAPPLICABLE_SLOT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.replaceitem.slot.inapplicable", new Object[]{object});
   });
   public static final Dynamic2CommandExceptionType ERROR_ENTITY_SLOT = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.replaceitem.entity.failed", new Object[]{var0, var1});
   });

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("replaceitem").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("block").then(Commands.argument("pos", BlockPosArgument.blockPos()).then(Commands.argument("slot", SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((commandContext) -> {
         return setBlockItem((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), SlotArgument.getSlot(commandContext, "slot"), ItemArgument.getItem(commandContext, "item").createItemStack(1, false));
      })).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((commandContext) -> {
         return setBlockItem((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), SlotArgument.getSlot(commandContext, "slot"), ItemArgument.getItem(commandContext, "item").createItemStack(IntegerArgumentType.getInteger(commandContext, "count"), true));
      }))))))).then(Commands.literal("entity").then(Commands.argument("targets", EntityArgument.entities()).then(Commands.argument("slot", SlotArgument.slot()).then(((RequiredArgumentBuilder)Commands.argument("item", ItemArgument.item()).executes((commandContext) -> {
         return setEntityItem((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), SlotArgument.getSlot(commandContext, "slot"), ItemArgument.getItem(commandContext, "item").createItemStack(1, false));
      })).then(Commands.argument("count", IntegerArgumentType.integer(1, 64)).executes((commandContext) -> {
         return setEntityItem((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"), SlotArgument.getSlot(commandContext, "slot"), ItemArgument.getItem(commandContext, "item").createItemStack(IntegerArgumentType.getInteger(commandContext, "count"), true));
      })))))));
   }

   private static int setBlockItem(CommandSourceStack commandSourceStack, BlockPos blockPos, int var2, ItemStack itemStack) throws CommandSyntaxException {
      BlockEntity var4 = commandSourceStack.getLevel().getBlockEntity(blockPos);
      if(!(var4 instanceof Container)) {
         throw ERROR_NOT_A_CONTAINER.create();
      } else {
         Container var5 = (Container)var4;
         if(var2 >= 0 && var2 < var5.getContainerSize()) {
            var5.setItem(var2, itemStack);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.replaceitem.block.success", new Object[]{Integer.valueOf(blockPos.getX()), Integer.valueOf(blockPos.getY()), Integer.valueOf(blockPos.getZ()), itemStack.getDisplayName()}), true);
            return 1;
         } else {
            throw ERROR_INAPPLICABLE_SLOT.create(Integer.valueOf(var2));
         }
      }
   }

   private static int setEntityItem(CommandSourceStack commandSourceStack, Collection collection, int var2, ItemStack itemStack) throws CommandSyntaxException {
      List<Entity> var4 = Lists.newArrayListWithCapacity(collection.size());

      for(Entity var6 : collection) {
         if(var6 instanceof ServerPlayer) {
            ((ServerPlayer)var6).inventoryMenu.broadcastChanges();
         }

         if(var6.setSlot(var2, itemStack.copy())) {
            var4.add(var6);
            if(var6 instanceof ServerPlayer) {
               ((ServerPlayer)var6).inventoryMenu.broadcastChanges();
            }
         }
      }

      if(var4.isEmpty()) {
         throw ERROR_ENTITY_SLOT.create(itemStack.getDisplayName(), Integer.valueOf(var2));
      } else {
         if(var4.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.replaceitem.entity.success.single", new Object[]{((Entity)var4.iterator().next()).getDisplayName(), itemStack.getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.replaceitem.entity.success.multiple", new Object[]{Integer.valueOf(var4.size()), itemStack.getDisplayName()}), true);
         }

         return var4.size();
      }
   }
}
