package net.minecraft.world.level.block;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.SnowGolem;
import net.minecraft.world.item.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.pattern.BlockInWorld;
import net.minecraft.world.level.block.state.pattern.BlockPattern;
import net.minecraft.world.level.block.state.pattern.BlockPatternBuilder;
import net.minecraft.world.level.block.state.predicate.BlockMaterialPredicate;
import net.minecraft.world.level.block.state.predicate.BlockStatePredicate;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;

public class CarvedPumpkinBlock extends HorizontalDirectionalBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   @Nullable
   private BlockPattern snowGolemBase;
   @Nullable
   private BlockPattern snowGolemFull;
   @Nullable
   private BlockPattern ironGolemBase;
   @Nullable
   private BlockPattern ironGolemFull;
   private static final Predicate PUMPKINS_PREDICATE = (blockState) -> {
      return blockState != null && (blockState.getBlock() == Blocks.CARVED_PUMPKIN || blockState.getBlock() == Blocks.JACK_O_LANTERN);
   };

   protected CarvedPumpkinBlock(Block.Properties block$Properties) {
      super(block$Properties);
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   public void onPlace(BlockState var1, Level level, BlockPos blockPos, BlockState var4, boolean var5) {
      if(var4.getBlock() != var1.getBlock()) {
         this.trySpawnGolem(level, blockPos);
      }
   }

   public boolean canSpawnGolem(LevelReader levelReader, BlockPos blockPos) {
      return this.getOrCreateSnowGolemBase().find(levelReader, blockPos) != null || this.getOrCreateIronGolemBase().find(levelReader, blockPos) != null;
   }

   private void trySpawnGolem(Level level, BlockPos blockPos) {
      BlockPattern.BlockPatternMatch var3 = this.getOrCreateSnowGolemFull().find(level, blockPos);
      if(var3 != null) {
         for(int var4 = 0; var4 < this.getOrCreateSnowGolemFull().getHeight(); ++var4) {
            BlockInWorld var5 = var3.getBlock(0, var4, 0);
            level.setBlock(var5.getPos(), Blocks.AIR.defaultBlockState(), 2);
            level.levelEvent(2001, var5.getPos(), Block.getId(var5.getState()));
         }

         SnowGolem var4 = (SnowGolem)EntityType.SNOW_GOLEM.create(level);
         BlockPos var5 = var3.getBlock(0, 2, 0).getPos();
         var4.moveTo((double)var5.getX() + 0.5D, (double)var5.getY() + 0.05D, (double)var5.getZ() + 0.5D, 0.0F, 0.0F);
         level.addFreshEntity(var4);

         for(ServerPlayer var7 : level.getEntitiesOfClass(ServerPlayer.class, var4.getBoundingBox().inflate(5.0D))) {
            CriteriaTriggers.SUMMONED_ENTITY.trigger(var7, var4);
         }

         for(int var6 = 0; var6 < this.getOrCreateSnowGolemFull().getHeight(); ++var6) {
            BlockInWorld var7 = var3.getBlock(0, var6, 0);
            level.blockUpdated(var7.getPos(), Blocks.AIR);
         }
      } else {
         var3 = this.getOrCreateIronGolemFull().find(level, blockPos);
         if(var3 != null) {
            for(int var4 = 0; var4 < this.getOrCreateIronGolemFull().getWidth(); ++var4) {
               for(int var5 = 0; var5 < this.getOrCreateIronGolemFull().getHeight(); ++var5) {
                  BlockInWorld var6 = var3.getBlock(var4, var5, 0);
                  level.setBlock(var6.getPos(), Blocks.AIR.defaultBlockState(), 2);
                  level.levelEvent(2001, var6.getPos(), Block.getId(var6.getState()));
               }
            }

            BlockPos var4 = var3.getBlock(1, 2, 0).getPos();
            IronGolem var5 = (IronGolem)EntityType.IRON_GOLEM.create(level);
            var5.setPlayerCreated(true);
            var5.moveTo((double)var4.getX() + 0.5D, (double)var4.getY() + 0.05D, (double)var4.getZ() + 0.5D, 0.0F, 0.0F);
            level.addFreshEntity(var5);

            for(ServerPlayer var7 : level.getEntitiesOfClass(ServerPlayer.class, var5.getBoundingBox().inflate(5.0D))) {
               CriteriaTriggers.SUMMONED_ENTITY.trigger(var7, var5);
            }

            for(int var6 = 0; var6 < this.getOrCreateIronGolemFull().getWidth(); ++var6) {
               for(int var7 = 0; var7 < this.getOrCreateIronGolemFull().getHeight(); ++var7) {
                  BlockInWorld var8 = var3.getBlock(var6, var7, 0);
                  level.blockUpdated(var8.getPos(), Blocks.AIR);
               }
            }
         }
      }

   }

   public BlockState getStateForPlacement(BlockPlaceContext blockPlaceContext) {
      return (BlockState)this.defaultBlockState().setValue(FACING, blockPlaceContext.getHorizontalDirection().getOpposite());
   }

   protected void createBlockStateDefinition(StateDefinition.Builder stateDefinition$Builder) {
      stateDefinition$Builder.add(new Property[]{FACING});
   }

   private BlockPattern getOrCreateSnowGolemBase() {
      if(this.snowGolemBase == null) {
         this.snowGolemBase = BlockPatternBuilder.start().aisle(new String[]{" ", "#", "#"}).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.snowGolemBase;
   }

   private BlockPattern getOrCreateSnowGolemFull() {
      if(this.snowGolemFull == null) {
         this.snowGolemFull = BlockPatternBuilder.start().aisle(new String[]{"^", "#", "#"}).where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.SNOW_BLOCK))).build();
      }

      return this.snowGolemFull;
   }

   private BlockPattern getOrCreateIronGolemBase() {
      if(this.ironGolemBase == null) {
         this.ironGolemBase = BlockPatternBuilder.start().aisle(new String[]{"~ ~", "###", "~#~"}).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return this.ironGolemBase;
   }

   private BlockPattern getOrCreateIronGolemFull() {
      if(this.ironGolemFull == null) {
         this.ironGolemFull = BlockPatternBuilder.start().aisle(new String[]{"~^~", "###", "~#~"}).where('^', BlockInWorld.hasState(PUMPKINS_PREDICATE)).where('#', BlockInWorld.hasState(BlockStatePredicate.forBlock(Blocks.IRON_BLOCK))).where('~', BlockInWorld.hasState(BlockMaterialPredicate.forMaterial(Material.AIR))).build();
      }

      return this.ironGolemFull;
   }
}
