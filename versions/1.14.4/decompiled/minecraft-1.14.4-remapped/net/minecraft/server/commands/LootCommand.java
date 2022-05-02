package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.ResourceLocationArgument;
import net.minecraft.commands.arguments.SlotArgument;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.commands.arguments.coordinates.Vec3Argument;
import net.minecraft.commands.arguments.item.ItemArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ReplaceItemCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.LootTables;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootCommand {
   public static final SuggestionProvider SUGGEST_LOOT_TABLE = (commandContext, suggestionsBuilder) -> {
      LootTables var2 = ((CommandSourceStack)commandContext.getSource()).getServer().getLootTables();
      return SharedSuggestionProvider.suggestResource((Iterable)var2.getIds(), suggestionsBuilder);
   };
   private static final DynamicCommandExceptionType ERROR_NO_HELD_ITEMS = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.drop.no_held_items", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_NO_LOOT_TABLE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.drop.no_loot_table", new Object[]{object});
   });

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)addTargets(Commands.literal("loot").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      }), (var0, lootCommand$DropConsumer) -> {
         return var0.then(Commands.literal("fish").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((commandContext) -> {
            return dropFishingLoot(commandContext, ResourceLocationArgument.getId(commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), ItemStack.EMPTY, lootCommand$DropConsumer);
         })).then(Commands.argument("tool", ItemArgument.item()).executes((commandContext) -> {
            return dropFishingLoot(commandContext, ResourceLocationArgument.getId(commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), ItemArgument.getItem(commandContext, "tool").createItemStack(1, false), lootCommand$DropConsumer);
         }))).then(Commands.literal("mainhand").executes((commandContext) -> {
            return dropFishingLoot(commandContext, ResourceLocationArgument.getId(commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.MAINHAND), lootCommand$DropConsumer);
         }))).then(Commands.literal("offhand").executes((commandContext) -> {
            return dropFishingLoot(commandContext, ResourceLocationArgument.getId(commandContext, "loot_table"), BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.OFFHAND), lootCommand$DropConsumer);
         }))))).then(Commands.literal("loot").then(Commands.argument("loot_table", ResourceLocationArgument.id()).suggests(SUGGEST_LOOT_TABLE).executes((commandContext) -> {
            return dropChestLoot(commandContext, ResourceLocationArgument.getId(commandContext, "loot_table"), lootCommand$DropConsumer);
         }))).then(Commands.literal("kill").then(Commands.argument("target", EntityArgument.entity()).executes((commandContext) -> {
            return dropKillLoot(commandContext, EntityArgument.getEntity(commandContext, "target"), lootCommand$DropConsumer);
         }))).then(Commands.literal("mine").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("pos", BlockPosArgument.blockPos()).executes((commandContext) -> {
            return dropBlockLoot(commandContext, BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), ItemStack.EMPTY, lootCommand$DropConsumer);
         })).then(Commands.argument("tool", ItemArgument.item()).executes((commandContext) -> {
            return dropBlockLoot(commandContext, BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), ItemArgument.getItem(commandContext, "tool").createItemStack(1, false), lootCommand$DropConsumer);
         }))).then(Commands.literal("mainhand").executes((commandContext) -> {
            return dropBlockLoot(commandContext, BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.MAINHAND), lootCommand$DropConsumer);
         }))).then(Commands.literal("offhand").executes((commandContext) -> {
            return dropBlockLoot(commandContext, BlockPosArgument.getLoadedBlockPos(commandContext, "pos"), getSourceHandItem((CommandSourceStack)commandContext.getSource(), EquipmentSlot.OFFHAND), lootCommand$DropConsumer);
         }))));
      }));
   }

   private static ArgumentBuilder addTargets(ArgumentBuilder var0, LootCommand.TailProvider lootCommand$TailProvider) {
      return var0.then(((LiteralArgumentBuilder)Commands.literal("replace").then(Commands.literal("entity").then(Commands.argument("entities", EntityArgument.entities()).then(lootCommand$TailProvider.construct(Commands.argument("slot", SlotArgument.slot()), (commandContext, list, lootCommand$Callback) -> {
         return entityReplace(EntityArgument.getEntities(commandContext, "entities"), SlotArgument.getSlot(commandContext, "slot"), list.size(), list, lootCommand$Callback);
      }).then(lootCommand$TailProvider.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (commandContext, list, lootCommand$Callback) -> {
         return entityReplace(EntityArgument.getEntities(commandContext, "entities"), SlotArgument.getSlot(commandContext, "slot"), IntegerArgumentType.getInteger(commandContext, "count"), list, lootCommand$Callback);
      })))))).then(Commands.literal("block").then(Commands.argument("targetPos", BlockPosArgument.blockPos()).then(lootCommand$TailProvider.construct(Commands.argument("slot", SlotArgument.slot()), (commandContext, list, lootCommand$Callback) -> {
         return blockReplace((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "targetPos"), SlotArgument.getSlot(commandContext, "slot"), list.size(), list, lootCommand$Callback);
      }).then(lootCommand$TailProvider.construct(Commands.argument("count", IntegerArgumentType.integer(0)), (commandContext, list, lootCommand$Callback) -> {
         return blockReplace((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "targetPos"), IntegerArgumentType.getInteger(commandContext, "slot"), IntegerArgumentType.getInteger(commandContext, "count"), list, lootCommand$Callback);
      })))))).then(Commands.literal("insert").then(lootCommand$TailProvider.construct(Commands.argument("targetPos", BlockPosArgument.blockPos()), (commandContext, list, lootCommand$Callback) -> {
         return blockDistribute((CommandSourceStack)commandContext.getSource(), BlockPosArgument.getLoadedBlockPos(commandContext, "targetPos"), list, lootCommand$Callback);
      }))).then(Commands.literal("give").then(lootCommand$TailProvider.construct(Commands.argument("players", EntityArgument.players()), (commandContext, list, lootCommand$Callback) -> {
         return playerGive(EntityArgument.getPlayers(commandContext, "players"), list, lootCommand$Callback);
      }))).then(Commands.literal("spawn").then(lootCommand$TailProvider.construct(Commands.argument("targetPos", Vec3Argument.vec3()), (commandContext, list, lootCommand$Callback) -> {
         return dropInWorld((CommandSourceStack)commandContext.getSource(), Vec3Argument.getVec3(commandContext, "targetPos"), list, lootCommand$Callback);
      })));
   }

   private static Container getContainer(CommandSourceStack commandSourceStack, BlockPos blockPos) throws CommandSyntaxException {
      BlockEntity var2 = commandSourceStack.getLevel().getBlockEntity(blockPos);
      if(!(var2 instanceof Container)) {
         throw ReplaceItemCommand.ERROR_NOT_A_CONTAINER.create();
      } else {
         return (Container)var2;
      }
   }

   private static int blockDistribute(CommandSourceStack commandSourceStack, BlockPos blockPos, List list, LootCommand.Callback lootCommand$Callback) throws CommandSyntaxException {
      Container var4 = getContainer(commandSourceStack, blockPos);
      List<ItemStack> var5 = Lists.newArrayListWithCapacity(list.size());

      for(ItemStack var7 : list) {
         if(distributeToContainer(var4, var7.copy())) {
            var4.setChanged();
            var5.add(var7);
         }
      }

      lootCommand$Callback.accept(var5);
      return var5.size();
   }

   private static boolean distributeToContainer(Container container, ItemStack itemStack) {
      boolean var2 = false;

      for(int var3 = 0; var3 < container.getContainerSize() && !itemStack.isEmpty(); ++var3) {
         ItemStack var4 = container.getItem(var3);
         if(container.canPlaceItem(var3, itemStack)) {
            if(var4.isEmpty()) {
               container.setItem(var3, itemStack);
               var2 = true;
               break;
            }

            if(canMergeItems(var4, itemStack)) {
               int var5 = itemStack.getMaxStackSize() - var4.getCount();
               int var6 = Math.min(itemStack.getCount(), var5);
               itemStack.shrink(var6);
               var4.grow(var6);
               var2 = true;
            }
         }
      }

      return var2;
   }

   private static int blockReplace(CommandSourceStack commandSourceStack, BlockPos blockPos, int var2, int var3, List list, LootCommand.Callback lootCommand$Callback) throws CommandSyntaxException {
      Container var6 = getContainer(commandSourceStack, blockPos);
      int var7 = var6.getContainerSize();
      if(var2 >= 0 && var2 < var7) {
         List<ItemStack> var8 = Lists.newArrayListWithCapacity(list.size());

         for(int var9 = 0; var9 < var3; ++var9) {
            int var10 = var2 + var9;
            ItemStack var11 = var9 < list.size()?(ItemStack)list.get(var9):ItemStack.EMPTY;
            if(var6.canPlaceItem(var10, var11)) {
               var6.setItem(var10, var11);
               var8.add(var11);
            }
         }

         lootCommand$Callback.accept(var8);
         return var8.size();
      } else {
         throw ReplaceItemCommand.ERROR_INAPPLICABLE_SLOT.create(Integer.valueOf(var2));
      }
   }

   private static boolean canMergeItems(ItemStack var0, ItemStack var1) {
      return var0.getItem() == var1.getItem() && var0.getDamageValue() == var1.getDamageValue() && var0.getCount() <= var0.getMaxStackSize() && Objects.equals(var0.getTag(), var1.getTag());
   }

   private static int playerGive(Collection collection, List list, LootCommand.Callback lootCommand$Callback) throws CommandSyntaxException {
      List<ItemStack> list = Lists.newArrayListWithCapacity(list.size());

      for(ItemStack var5 : list) {
         for(ServerPlayer var7 : collection) {
            if(var7.inventory.add(var5.copy())) {
               list.add(var5);
            }
         }
      }

      lootCommand$Callback.accept(list);
      return list.size();
   }

   private static void setSlots(Entity entity, List var1, int var2, int var3, List var4) {
      for(int var5 = 0; var5 < var3; ++var5) {
         ItemStack var6 = var5 < var1.size()?(ItemStack)var1.get(var5):ItemStack.EMPTY;
         if(entity.setSlot(var2 + var5, var6.copy())) {
            var4.add(var6);
         }
      }

   }

   private static int entityReplace(Collection collection, int var1, int var2, List list, LootCommand.Callback lootCommand$Callback) throws CommandSyntaxException {
      List<ItemStack> list = Lists.newArrayListWithCapacity(list.size());

      for(Entity var7 : collection) {
         if(var7 instanceof ServerPlayer) {
            ServerPlayer var8 = (ServerPlayer)var7;
            var8.inventoryMenu.broadcastChanges();
            setSlots(var7, list, var1, var2, list);
            var8.inventoryMenu.broadcastChanges();
         } else {
            setSlots(var7, list, var1, var2, list);
         }
      }

      lootCommand$Callback.accept(list);
      return list.size();
   }

   private static int dropInWorld(CommandSourceStack commandSourceStack, Vec3 vec3, List list, LootCommand.Callback lootCommand$Callback) throws CommandSyntaxException {
      ServerLevel var4 = commandSourceStack.getLevel();
      list.forEach((itemStack) -> {
         ItemEntity var3 = new ItemEntity(var4, vec3.x, vec3.y, vec3.z, itemStack.copy());
         var3.setDefaultPickUpDelay();
         var4.addFreshEntity(var3);
      });
      lootCommand$Callback.accept(list);
      return list.size();
   }

   private static void callback(CommandSourceStack commandSourceStack, List list) {
      if(list.size() == 1) {
         ItemStack var2 = (ItemStack)list.get(0);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.drop.success.single", new Object[]{Integer.valueOf(var2.getCount()), var2.getDisplayName()}), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.drop.success.multiple", new Object[]{Integer.valueOf(list.size())}), false);
      }

   }

   private static void callback(CommandSourceStack commandSourceStack, List list, ResourceLocation resourceLocation) {
      if(list.size() == 1) {
         ItemStack var3 = (ItemStack)list.get(0);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.drop.success.single_with_table", new Object[]{Integer.valueOf(var3.getCount()), var3.getDisplayName(), resourceLocation}), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.drop.success.multiple_with_table", new Object[]{Integer.valueOf(list.size()), resourceLocation}), false);
      }

   }

   private static ItemStack getSourceHandItem(CommandSourceStack commandSourceStack, EquipmentSlot equipmentSlot) throws CommandSyntaxException {
      Entity var2 = commandSourceStack.getEntityOrException();
      if(var2 instanceof LivingEntity) {
         return ((LivingEntity)var2).getItemBySlot(equipmentSlot);
      } else {
         throw ERROR_NO_HELD_ITEMS.create(var2.getDisplayName());
      }
   }

   private static int dropBlockLoot(CommandContext commandContext, BlockPos blockPos, ItemStack itemStack, LootCommand.DropConsumer lootCommand$DropConsumer) throws CommandSyntaxException {
      CommandSourceStack var4 = (CommandSourceStack)commandContext.getSource();
      ServerLevel var5 = var4.getLevel();
      BlockState var6 = var5.getBlockState(blockPos);
      BlockEntity var7 = var5.getBlockEntity(blockPos);
      LootContext.Builder var8 = (new LootContext.Builder(var5)).withParameter(LootContextParams.BLOCK_POS, blockPos).withParameter(LootContextParams.BLOCK_STATE, var6).withOptionalParameter(LootContextParams.BLOCK_ENTITY, var7).withOptionalParameter(LootContextParams.THIS_ENTITY, var4.getEntity()).withParameter(LootContextParams.TOOL, itemStack);
      List<ItemStack> var9 = var6.getDrops(var8);
      return lootCommand$DropConsumer.accept(commandContext, var9, (list) -> {
         callback(var4, list, var6.getBlock().getLootTable());
      });
   }

   private static int dropKillLoot(CommandContext commandContext, Entity entity, LootCommand.DropConsumer lootCommand$DropConsumer) throws CommandSyntaxException {
      if(!(entity instanceof LivingEntity)) {
         throw ERROR_NO_LOOT_TABLE.create(entity.getDisplayName());
      } else {
         ResourceLocation var3 = ((LivingEntity)entity).getLootTable();
         CommandSourceStack var4 = (CommandSourceStack)commandContext.getSource();
         LootContext.Builder var5 = new LootContext.Builder(var4.getLevel());
         Entity var6 = var4.getEntity();
         if(var6 instanceof Player) {
            var5.withParameter(LootContextParams.LAST_DAMAGE_PLAYER, (Player)var6);
         }

         var5.withParameter(LootContextParams.DAMAGE_SOURCE, DamageSource.MAGIC);
         var5.withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, var6);
         var5.withOptionalParameter(LootContextParams.KILLER_ENTITY, var6);
         var5.withParameter(LootContextParams.THIS_ENTITY, entity);
         var5.withParameter(LootContextParams.BLOCK_POS, new BlockPos(var4.getPosition()));
         LootTable var7 = var4.getServer().getLootTables().get(var3);
         List<ItemStack> var8 = var7.getRandomItems(var5.create(LootContextParamSets.ENTITY));
         return lootCommand$DropConsumer.accept(commandContext, var8, (list) -> {
            callback(var4, list, var3);
         });
      }
   }

   private static int dropChestLoot(CommandContext commandContext, ResourceLocation resourceLocation, LootCommand.DropConsumer lootCommand$DropConsumer) throws CommandSyntaxException {
      CommandSourceStack var3 = (CommandSourceStack)commandContext.getSource();
      LootContext.Builder var4 = (new LootContext.Builder(var3.getLevel())).withOptionalParameter(LootContextParams.THIS_ENTITY, var3.getEntity()).withParameter(LootContextParams.BLOCK_POS, new BlockPos(var3.getPosition()));
      return drop(commandContext, resourceLocation, var4.create(LootContextParamSets.CHEST), lootCommand$DropConsumer);
   }

   private static int dropFishingLoot(CommandContext commandContext, ResourceLocation resourceLocation, BlockPos blockPos, ItemStack itemStack, LootCommand.DropConsumer lootCommand$DropConsumer) throws CommandSyntaxException {
      CommandSourceStack var5 = (CommandSourceStack)commandContext.getSource();
      LootContext var6 = (new LootContext.Builder(var5.getLevel())).withParameter(LootContextParams.BLOCK_POS, blockPos).withParameter(LootContextParams.TOOL, itemStack).create(LootContextParamSets.FISHING);
      return drop(commandContext, resourceLocation, var6, lootCommand$DropConsumer);
   }

   private static int drop(CommandContext commandContext, ResourceLocation resourceLocation, LootContext lootContext, LootCommand.DropConsumer lootCommand$DropConsumer) throws CommandSyntaxException {
      CommandSourceStack var4 = (CommandSourceStack)commandContext.getSource();
      LootTable var5 = var4.getServer().getLootTables().get(resourceLocation);
      List<ItemStack> var6 = var5.getRandomItems(lootContext);
      return lootCommand$DropConsumer.accept(commandContext, var6, (list) -> {
         callback(var4, list);
      });
   }

   @FunctionalInterface
   interface Callback {
      void accept(List var1) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface DropConsumer {
      int accept(CommandContext var1, List var2, LootCommand.Callback var3) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface TailProvider {
      ArgumentBuilder construct(ArgumentBuilder var1, LootCommand.DropConsumer var2);
   }
}
