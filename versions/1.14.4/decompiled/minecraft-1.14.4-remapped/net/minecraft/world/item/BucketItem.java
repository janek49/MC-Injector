package net.minecraft.world.item;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.BucketPickup;
import net.minecraft.world.level.block.LiquidBlockContainer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FlowingFluid;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public class BucketItem extends Item {
   private final Fluid content;

   public BucketItem(Fluid content, Item.Properties item$Properties) {
      super(item$Properties);
      this.content = content;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      HitResult var5 = getPlayerPOVHitResult(level, player, this.content == Fluids.EMPTY?ClipContext.Fluid.SOURCE_ONLY:ClipContext.Fluid.NONE);
      if(var5.getType() == HitResult.Type.MISS) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else if(var5.getType() != HitResult.Type.BLOCK) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else {
         BlockHitResult var6 = (BlockHitResult)var5;
         BlockPos var7 = var6.getBlockPos();
         if(level.mayInteract(player, var7) && player.mayUseItemAt(var7, var6.getDirection(), var4)) {
            if(this.content == Fluids.EMPTY) {
               BlockState var8 = level.getBlockState(var7);
               if(var8.getBlock() instanceof BucketPickup) {
                  Fluid var9 = ((BucketPickup)var8.getBlock()).takeLiquid(level, var7, var8);
                  if(var9 != Fluids.EMPTY) {
                     player.awardStat(Stats.ITEM_USED.get(this));
                     player.playSound(var9.is(FluidTags.LAVA)?SoundEvents.BUCKET_FILL_LAVA:SoundEvents.BUCKET_FILL, 1.0F, 1.0F);
                     ItemStack var10 = this.createResultItem(var4, player, var9.getBucket());
                     if(!level.isClientSide) {
                        CriteriaTriggers.FILLED_BUCKET.trigger((ServerPlayer)player, new ItemStack(var9.getBucket()));
                     }

                     return new InteractionResultHolder(InteractionResult.SUCCESS, var10);
                  }
               }

               return new InteractionResultHolder(InteractionResult.FAIL, var4);
            } else {
               BlockState var8 = level.getBlockState(var7);
               BlockPos var9 = var8.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER?var7:var6.getBlockPos().relative(var6.getDirection());
               if(this.emptyBucket(player, level, var9, var6)) {
                  this.checkExtraContent(level, var4, var9);
                  if(player instanceof ServerPlayer) {
                     CriteriaTriggers.PLACED_BLOCK.trigger((ServerPlayer)player, var9, var4);
                  }

                  player.awardStat(Stats.ITEM_USED.get(this));
                  return new InteractionResultHolder(InteractionResult.SUCCESS, this.getEmptySuccessItem(var4, player));
               } else {
                  return new InteractionResultHolder(InteractionResult.FAIL, var4);
               }
            }
         } else {
            return new InteractionResultHolder(InteractionResult.FAIL, var4);
         }
      }
   }

   protected ItemStack getEmptySuccessItem(ItemStack var1, Player player) {
      return !player.abilities.instabuild?new ItemStack(Items.BUCKET):var1;
   }

   public void checkExtraContent(Level level, ItemStack itemStack, BlockPos blockPos) {
   }

   private ItemStack createResultItem(ItemStack var1, Player player, Item item) {
      if(player.abilities.instabuild) {
         return var1;
      } else {
         var1.shrink(1);
         if(var1.isEmpty()) {
            return new ItemStack(item);
         } else {
            if(!player.inventory.add(new ItemStack(item))) {
               player.drop(new ItemStack(item), false);
            }

            return var1;
         }
      }
   }

   public boolean emptyBucket(@Nullable Player player, Level level, BlockPos blockPos, @Nullable BlockHitResult blockHitResult) {
      if(!(this.content instanceof FlowingFluid)) {
         return false;
      } else {
         BlockState var5 = level.getBlockState(blockPos);
         Material var6 = var5.getMaterial();
         boolean var7 = !var6.isSolid();
         boolean var8 = var6.isReplaceable();
         if(level.isEmptyBlock(blockPos) || var7 || var8 || var5.getBlock() instanceof LiquidBlockContainer && ((LiquidBlockContainer)var5.getBlock()).canPlaceLiquid(level, blockPos, var5, this.content)) {
            if(level.dimension.isUltraWarm() && this.content.is(FluidTags.WATER)) {
               int var9 = blockPos.getX();
               int var10 = blockPos.getY();
               int var11 = blockPos.getZ();
               level.playSound(player, blockPos, SoundEvents.FIRE_EXTINGUISH, SoundSource.BLOCKS, 0.5F, 2.6F + (level.random.nextFloat() - level.random.nextFloat()) * 0.8F);

               for(int var12 = 0; var12 < 8; ++var12) {
                  level.addParticle(ParticleTypes.LARGE_SMOKE, (double)var9 + Math.random(), (double)var10 + Math.random(), (double)var11 + Math.random(), 0.0D, 0.0D, 0.0D);
               }
            } else if(var5.getBlock() instanceof LiquidBlockContainer && this.content == Fluids.WATER) {
               if(((LiquidBlockContainer)var5.getBlock()).placeLiquid(level, blockPos, var5, ((FlowingFluid)this.content).getSource(false))) {
                  this.playEmptySound(player, level, blockPos);
               }
            } else {
               if(!level.isClientSide && (var7 || var8) && !var6.isLiquid()) {
                  level.destroyBlock(blockPos, true);
               }

               this.playEmptySound(player, level, blockPos);
               level.setBlock(blockPos, this.content.defaultFluidState().createLegacyBlock(), 11);
            }

            return true;
         } else {
            return blockHitResult == null?false:this.emptyBucket(player, level, blockHitResult.getBlockPos().relative(blockHitResult.getDirection()), (BlockHitResult)null);
         }
      }
   }

   protected void playEmptySound(@Nullable Player player, LevelAccessor levelAccessor, BlockPos blockPos) {
      SoundEvent var4 = this.content.is(FluidTags.LAVA)?SoundEvents.BUCKET_EMPTY_LAVA:SoundEvents.BUCKET_EMPTY;
      levelAccessor.playSound(player, blockPos, var4, SoundSource.BLOCKS, 1.0F, 1.0F);
   }
}
