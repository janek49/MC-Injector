package net.minecraft.world.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.EyeOfEnder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseOnContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EndPortalFrameBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class EnderEyeItem extends Item {
   public EnderEyeItem(Item.Properties item$Properties) {
      super(item$Properties);
   }

   public InteractionResult useOn(UseOnContext useOnContext) {
      Level var2 = useOnContext.getLevel();
      BlockPos var3 = useOnContext.getClickedPos();
      BlockState var4 = var2.getBlockState(var3);
      if(var4.getBlock() == Blocks.END_PORTAL_FRAME && !((Boolean)var4.getValue(EndPortalFrameBlock.HAS_EYE)).booleanValue()) {
         if(var2.isClientSide) {
            return InteractionResult.SUCCESS;
         } else {
            BlockState var5 = (BlockState)var4.setValue(EndPortalFrameBlock.HAS_EYE, Boolean.valueOf(true));
            Block.pushEntitiesUp(var4, var5, var2, var3);
            var2.setBlock(var3, var5, 2);
            var2.updateNeighbourForOutputSignal(var3, Blocks.END_PORTAL_FRAME);
            useOnContext.getItemInHand().shrink(1);
            var2.levelEvent(1503, var3, 0);
            BlockPattern.BlockPatternMatch var6 = EndPortalFrameBlock.getOrCreatePortalShape().find(var2, var3);
            if(var6 != null) {
               BlockPos var7 = var6.getFrontTopLeft().offset(-3, 0, -3);

               for(int var8 = 0; var8 < 3; ++var8) {
                  for(int var9 = 0; var9 < 3; ++var9) {
                     var2.setBlock(var7.offset(var8, 0, var9), Blocks.END_PORTAL.defaultBlockState(), 2);
                  }
               }

               var2.globalLevelEvent(1038, var7.offset(1, 0, 1), 0);
            }

            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.PASS;
      }
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      HitResult var5 = getPlayerPOVHitResult(level, player, ClipContext.Fluid.NONE);
      if(var5.getType() == HitResult.Type.BLOCK && level.getBlockState(((BlockHitResult)var5).getBlockPos()).getBlock() == Blocks.END_PORTAL_FRAME) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else {
         player.startUsingItem(interactionHand);
         if(!level.isClientSide) {
            BlockPos var6 = level.getChunkSource().getGenerator().findNearestMapFeature(level, "Stronghold", new BlockPos(player), 100, false);
            if(var6 != null) {
               EyeOfEnder var7 = new EyeOfEnder(level, player.x, player.y + (double)(player.getBbHeight() / 2.0F), player.z);
               var7.setItem(var4);
               var7.signalTo(var6);
               level.addFreshEntity(var7);
               if(player instanceof ServerPlayer) {
                  CriteriaTriggers.USED_ENDER_EYE.trigger((ServerPlayer)player, var6);
               }

               level.playSound((Player)null, player.x, player.y, player.z, SoundEvents.ENDER_EYE_LAUNCH, SoundSource.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
               level.levelEvent((Player)null, 1003, new BlockPos(player), 0);
               if(!player.abilities.instabuild) {
                  var4.shrink(1);
               }

               player.awardStat(Stats.ITEM_USED.get(this));
               return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
            }
         }

         return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
      }
   }
}
