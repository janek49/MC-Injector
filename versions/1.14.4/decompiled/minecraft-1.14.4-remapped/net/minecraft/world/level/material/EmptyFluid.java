package net.minecraft.world.level.material;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class EmptyFluid extends Fluid {
   public BlockLayer getRenderLayer() {
      return BlockLayer.SOLID;
   }

   public Item getBucket() {
      return Items.AIR;
   }

   public boolean canBeReplacedWith(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos, Fluid fluid, Direction direction) {
      return true;
   }

   public Vec3 getFlow(BlockGetter blockGetter, BlockPos blockPos, FluidState fluidState) {
      return Vec3.ZERO;
   }

   public int getTickDelay(LevelReader levelReader) {
      return 0;
   }

   protected boolean isEmpty() {
      return true;
   }

   protected float getExplosionResistance() {
      return 0.0F;
   }

   public float getHeight(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos) {
      return 0.0F;
   }

   public float getOwnHeight(FluidState fluidState) {
      return 0.0F;
   }

   protected BlockState createLegacyBlock(FluidState fluidState) {
      return Blocks.AIR.defaultBlockState();
   }

   public boolean isSource(FluidState fluidState) {
      return false;
   }

   public int getAmount(FluidState fluidState) {
      return 0;
   }

   public VoxelShape getShape(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos) {
      return Shapes.empty();
   }
}
