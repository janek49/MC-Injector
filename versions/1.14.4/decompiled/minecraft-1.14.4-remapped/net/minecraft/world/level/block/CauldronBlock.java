package net.minecraft.world.level.block;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BannerItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ShulkerBoxBlock;
import net.minecraft.world.level.block.entity.BannerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CauldronBlock extends Block {
   public static final IntegerProperty LEVEL = BlockStateProperties.LEVEL_CAULDRON;
   private static final VoxelShape INSIDE = box(2.0D, 4.0D, 2.0D, 14.0D, 16.0D, 14.0D);
   protected static final VoxelShape SHAPE = Shapes.join(Shapes.block(), Shapes.or(box(0.0D, 0.0D, 4.0D, 16.0D, 3.0D, 12.0D), new VoxelShape[]{box(4.0D, 0.0D, 0.0D, 12.0D, 3.0D, 16.0D), box(2.0D, 0.0D, 2.0D, 14.0D, 3.0D, 14.0D), INSIDE}), BooleanOp.ONLY_FIRST);

   public CauldronBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(LEVEL, Integer.valueOf(0)));
   }

   public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
      return SHAPE;
   }

   public boolean canOcclude(BlockState blockState) {
      return false;
   }

   public VoxelShape getInteractionShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return INSIDE;
   }

   public void entityInside(BlockState blockState, Level level, BlockPos blockPos, Entity entity) {
      int var5 = ((Integer)blockState.getValue(LEVEL)).intValue();
      float var6 = (float)blockPos.getY() + (6.0F + (float)(3 * var5)) / 16.0F;
      if(!level.isClientSide && entity.isOnFire() && var5 > 0 && entity.getBoundingBox().minY <= (double)var6) {
         entity.clearFire();
         this.setWaterLevel(level, blockPos, blockState, var5 - 1);
      }

   }

   public boolean use(BlockState blockState, Level level, BlockPos blockPos, Player player, InteractionHand interactionHand, BlockHitResult blockHitResult) {
      ItemStack var7 = player.getItemInHand(interactionHand);
      if(var7.isEmpty()) {
         return true;
      } else {
         int var8 = ((Integer)blockState.getValue(LEVEL)).intValue();
         Item var9 = var7.getItem();
         if(var9 == Items.WATER_BUCKET) {
            if(var8 < 3 && !level.isClientSide) {
               if(!player.abilities.instabuild) {
                  player.setItemInHand(interactionHand, new ItemStack(Items.BUCKET));
               }

               player.awardStat(Stats.FILL_CAULDRON);
               this.setWaterLevel(level, blockPos, blockState, 3);
               level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.BUCKET_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else if(var9 == Items.BUCKET) {
            if(var8 == 3 && !level.isClientSide) {
               if(!player.abilities.instabuild) {
                  var7.shrink(1);
                  if(var7.isEmpty()) {
                     player.setItemInHand(interactionHand, new ItemStack(Items.WATER_BUCKET));
                  } else if(!player.inventory.add(new ItemStack(Items.WATER_BUCKET))) {
                     player.drop(new ItemStack(Items.WATER_BUCKET), false);
                  }
               }

               player.awardStat(Stats.USE_CAULDRON);
               this.setWaterLevel(level, blockPos, blockState, 0);
               level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.BUCKET_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
            }

            return true;
         } else if(var9 == Items.GLASS_BOTTLE) {
            if(var8 > 0 && !level.isClientSide) {
               if(!player.abilities.instabuild) {
                  ItemStack var10 = PotionUtils.setPotion(new ItemStack(Items.POTION), Potions.WATER);
                  player.awardStat(Stats.USE_CAULDRON);
                  var7.shrink(1);
                  if(var7.isEmpty()) {
                     player.setItemInHand(interactionHand, var10);
                  } else if(!player.inventory.add(var10)) {
                     player.drop(var10, false);
                  } else if(player instanceof ServerPlayer) {
                     ((ServerPlayer)player).refreshContainer(player.inventoryMenu);
                  }
               }

               level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0F, 1.0F);
               this.setWaterLevel(level, blockPos, blockState, var8 - 1);
            }

            return true;
         } else if(var9 == Items.POTION && PotionUtils.getPotion(var7) == Potions.WATER) {
            if(var8 < 3 && !level.isClientSide) {
               if(!player.abilities.instabuild) {
                  ItemStack var10 = new ItemStack(Items.GLASS_BOTTLE);
                  player.awardStat(Stats.USE_CAULDRON);
                  player.setItemInHand(interactionHand, var10);
                  if(player instanceof ServerPlayer) {
                     ((ServerPlayer)player).refreshContainer(player.inventoryMenu);
                  }
               }

               level.playSound((Player)null, (BlockPos)blockPos, SoundEvents.BOTTLE_EMPTY, SoundSource.BLOCKS, 1.0F, 1.0F);
               this.setWaterLevel(level, blockPos, blockState, var8 + 1);
            }

            return true;
         } else {
            if(var8 > 0 && var9 instanceof DyeableLeatherItem) {
               DyeableLeatherItem var10 = (DyeableLeatherItem)var9;
               if(var10.hasCustomColor(var7) && !level.isClientSide) {
                  var10.clearColor(var7);
                  this.setWaterLevel(level, blockPos, blockState, var8 - 1);
                  player.awardStat(Stats.CLEAN_ARMOR);
                  return true;
               }
            }

            if(var8 > 0 && var9 instanceof BannerItem) {
               if(BannerBlockEntity.getPatternCount(var7) > 0 && !level.isClientSide) {
                  ItemStack var10 = var7.copy();
                  var10.setCount(1);
                  BannerBlockEntity.removeLastPattern(var10);
                  player.awardStat(Stats.CLEAN_BANNER);
                  if(!player.abilities.instabuild) {
                     var7.shrink(1);
                     this.setWaterLevel(level, blockPos, blockState, var8 - 1);
                  }

                  if(var7.isEmpty()) {
                     player.setItemInHand(interactionHand, var10);
                  } else if(!player.inventory.add(var10)) {
                     player.drop(var10, false);
                  } else if(player instanceof ServerPlayer) {
                     ((ServerPlayer)player).refreshContainer(player.inventoryMenu);
                  }
               }

               return true;
            } else if(var8 > 0 && var9 instanceof BlockItem) {
               Block var10 = ((BlockItem)var9).getBlock();
               if(var10 instanceof ShulkerBoxBlock && !level.isClientSide()) {
                  ItemStack var11 = new ItemStack(Blocks.SHULKER_BOX, 1);
                  if(var7.hasTag()) {
                     var11.setTag(var7.getTag().copy());
                  }

                  player.setItemInHand(interactionHand, var11);
                  this.setWaterLevel(level, blockPos, blockState, var8 - 1);
                  player.awardStat(Stats.CLEAN_SHULKER_BOX);
               }

               return true;
            } else {
               return false;
            }
         }
      }
   }

   public void setWaterLevel(Level level, BlockPos blockPos, BlockState blockState, int var4) {
      level.setBlock(blockPos, (BlockState)blockState.setValue(LEVEL, Integer.valueOf(Mth.clamp(var4, 0, 3))), 2);
      level.updateNeighbourForOutputSignal(blockPos, this);
   }

   public void handleRain(Level level, BlockPos blockPos) {
      if(level.random.nextInt(20) == 1) {
         float var3 = level.getBiome(blockPos).getTemperature(blockPos);
         if(var3 >= 0.15F) {
            BlockState var4 = level.getBlockState(blockPos);
            if(((Integer)var4.getValue(LEVEL)).intValue() < 3) {
               level.setBlock(blockPos, (BlockState)var4.cycle(LEVEL), 2);
            }

         }
      }
   }

   public boolean hasAnalogOutputSignal(BlockState blockState) {
      return true;
   }

   public int getAnalogOutputSignal(BlockState blockState, Level level, BlockPos blockPos) {
      return ((Integer)blockState.getValue(LEVEL)).intValue();
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{LEVEL});
   }

   public boolean isPathfindable(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, PathComputationType pathComputationType) {
      return false;
   }
}
