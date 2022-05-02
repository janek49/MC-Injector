package net.minecraft.world.item;

import java.util.Collection;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;

public class DebugStickItem extends Item {
   public DebugStickItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public boolean isFoil(ItemStack itemStack) {
      return true;
   }

   public boolean canAttackBlock(BlockState blockState, Level level, BlockPos blockPos, Player player) {
      if(!level.isClientSide) {
         this.handleInteraction(player, blockState, level, blockPos, false, player.getItemInHand(InteractionHand.MAIN_HAND));
      }

      return false;
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Player var2 = useOnContext.getPlayer();
      Level var3 = useOnContext.getLevel();
      if(!var3.isClientSide && var2 != null) {
         BlockPos var4 = useOnContext.getClickedPos();
         this.handleInteraction(var2, var3.getBlockState(var4), var3, var4, true, useOnContext.getItemInHand());
      }

      return InteractionResult.SUCCESS;
   }

   private void handleInteraction(Player player, BlockState blockState, LevelAccessor levelAccessor, BlockPos blockPos, boolean var5, ItemStack itemStack) {
      if(player.canUseGameMasterBlocks()) {
         Block var7 = blockState.getBlock();
         StateDefinition<Block, BlockState> var8 = var7.getStateDefinition();
         Collection<Property<?>> var9 = var8.getProperties();
         String var10 = Registry.BLOCK.getKey(var7).toString();
         if(var9.isEmpty()) {
            message(player, new TranslatableComponent(this.getDescriptionId() + ".empty", new Object[]{var10}));
         } else {
            CompoundTag var11 = itemStack.getOrCreateTagElement("DebugProperty");
            String var12 = var11.getString(var10);
            Property<?> var13 = var8.getProperty(var12);
            if(var5) {
               if(var13 == null) {
                  var13 = (Property)var9.iterator().next();
               }

               BlockState var14 = cycleState(blockState, var13, player.isSneaking());
               levelAccessor.setBlock(blockPos, var14, 18);
               message(player, new TranslatableComponent(this.getDescriptionId() + ".update", new Object[]{var13.getName(), getNameHelper(var14, var13)}));
            } else {
               var13 = (Property)getRelative(var9, var13, player.isSneaking());
               String var14 = var13.getName();
               var11.putString(var10, var14);
               message(player, new TranslatableComponent(this.getDescriptionId() + ".select", new Object[]{var14, getNameHelper(blockState, var13)}));
            }

         }
      }
   }

   private static BlockState cycleState(BlockState var0, Property property, boolean var2) {
      return (BlockState)var0.setValue(property, (Comparable)getRelative(property.getPossibleValues(), var0.getValue(property), var2));
   }

   private static Object getRelative(Iterable iterable, @Nullable Object var1, boolean var2) {
      return var2?Util.findPreviousInIterable(iterable, var1):Util.findNextInIterable(iterable, var1);
   }

   private static void message(Player player, Component component) {
      ((ServerPlayer)player).sendMessage(component, ChatType.GAME_INFO);
   }

   private static String getNameHelper(BlockState blockState, Property property) {
      return property.getName(blockState.getValue(property));
   }
}
