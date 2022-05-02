package net.minecraft.world.level.block;

import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.boss.wither.WitherBoss;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SkullBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SkullBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.material.Material;

public class WitherSkullBlock extends SkullBlock {
   @Nullable
   private static BlockPattern witherPatternFull;
   @Nullable
   private static BlockPattern witherPatternBase;

   protected WitherSkullBlock(Block.Properties block$Properties) {
      super(SkullBlock.Types.WITHER_SKELETON, block$Properties);
   }

   public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, @Nullable LivingEntity livingEntity, ItemStack itemStack) {
      super.setPlacedBy(level, blockPos, blockState, livingEntity, itemStack);
      BlockEntity var6 = level.getBlockEntity(blockPos);
      if(var6 instanceof SkullBlockEntity) {
         checkSpawn(level, blockPos, (SkullBlockEntity)var6);
      }

   }

   public static void checkSpawn(Level level, BlockPos blockPos, SkullBlockEntity skullBlockEntity) {
      if(!level.isClientSide) {
         Block var3 = skullBlockEntity.getBlockState().getBlock();
         boolean var4 = var3 == Blocks.WITHER_SKELETON_SKULL || var3 == Blocks.WITHER_SKELETON_WALL_SKULL;
         if(var4 && blockPos.getY() >= 2 && level.getDifficulty() != Difficulty.PEACEFUL) {
            BlockPattern var5 = getOrCreateWitherFull();
            BlockPattern.BlockPatternMatch var6 = var5.find(level, blockPos);
            if(var6 != null) {
               for(int var7 = 0; var7 < var5.getWidth(); ++var7) {
                  for(int var8 = 0; var8 < var5.getHeight(); ++var8) {
                     BlockInWorld var9 = var6.getBlock(var7, var8, 0);
                     level.setBlock(var9.getPos(), Blocks.AIR.defaultBlockState(), 2);
                     level.levelEvent(2001, var9.getPos(), Block.getId(var9.getState()));
                  }
               }

               WitherBoss var7 = (WitherBoss)EntityType.WITHER.create(level);
               BlockPos var8 = var6.getBlock(1, 2, 0).getPos();
               var7.moveTo((double)var8.getX() + 0.5D, (double)var8.getY() + 0.55D, (double)var8.getZ() + 0.5D, var6.getForwards().getAxis() == Direction.Axis.X?0.0F:90.0F, 0.0F);
               var7.yBodyRot = var6.getForwards().getAxis() == Direction.Axis.X?0.0F:90.0F;
               var7.makeInvulnerable();

               for(ServerPlayer var10 : level.getEntitiesOfClass(ServerPlayer.class, var7.getBoundingBox().inflate(50.0D))) {
                  CriteriaTriggers.SUMMONED_ENTITY.trigger(var10, var7);
               }

               level.addFreshEntity(var7);

               for(int var9 = 0; var9 < var5.getWidth(); ++var9) {
                  for(int var10 = 0; var10 < var5.getHeight(); ++var10) {
                     level.blockUpdated(var6.getBlock(var9, var10, 0).getPos(), Blocks.AIR);
                  }
               }

            }
         }
      }
   }

   public static boolean canSpawnMob(Level level, BlockPos blockPos, ItemStack itemStack) {
      return itemStack.getItem() == Items.WITHER_SKELETON_SKULL && blockPos.getY() >= 2 && level.getDifficulty() != Difficulty.PEACEFUL && !level.isClientSide?getOrCreateWitherBase().find(level, blockPos) != null:false;
   }

   private static BlockPattern getOrCreateWitherFull() {
      if(witherPatternFull == null) {
         witherPatternFull = BlockPatternBuilder.start().aisle(new String[]{"^^^", "###", "~#~"}).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SOUL_SAND))).where('^', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_SKULL).or(BlockStatePredicate.forBlock(Blocks.WITHER_SKELETON_WALL_SKULL)))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return witherPatternFull;
   }

   private static BlockPattern getOrCreateWitherBase() {
      if(witherPatternBase == null) {
         witherPatternBase = BlockPatternBuilder.start().aisle(new String[]{"   ", "###", "~#~"}).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SOUL_SAND))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return witherPatternBase;
   }
}
