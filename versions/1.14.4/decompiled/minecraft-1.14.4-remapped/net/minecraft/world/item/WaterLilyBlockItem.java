package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class WaterLilyBlockItem extends BlockItem {
   public WaterLilyBlockItem(Block block, Item.Properties item$Properties) {
      super(block, item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      return InteractionResult.PASS;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      HitResult var5 = getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
      if(var5.getType() == HitResult.Type.MISS) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else {
         if(var5.getType() == HitResult.Type.BLOCK) {
            BlockHitResult var6 = (BlockHitResult)var5;
            BlockPos var7 = var6.getBlockPos();
            Direction var8 = var6.getDirection();
            if(!level.mayInteract(player, var7) || !player.mayUseItemAt(var7.relative(var8), var8, var4)) {
               return new InteractionResultHolder(InteractionResult.FAIL, var4);
            }

            BlockPos var9 = var7.above();
            BlockState var10 = level.getBlockState(var7);
            Material var11 = var10.getMaterial();
            FluidState var12 = level.getFluidState(var7);
            if((var12.getType() == Fluids.WATER || var11 == Material.ICE) && level.isEmptyBlock(var9)) {
               level.setBlock(var9, Blocks.LILY_PAD.defaultBlockState(), 11);
               if(player instanceof ServerPlayer) {
                  CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, var9, var4);
               }

               if(!player.abilities.instabuild) {
                  var4.shrink(1);
               }

               player.awardStat(Stats.ITEM_USED.get(this));
               level.playSound(player, var7, SoundEvents.LILY_PAD_PLACE, SoundSource.BLOCKS, 1.0F, 1.0F);
               return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
            }
         }

         return new InteractionResultHolder(InteractionResult.FAIL, var4);
      }
   }
}
