package net.minecraft.world.level;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ClipContext {
   private final Vec3 from;
   private final Vec3 to;
   private final ClipContext.Block block;
   private final ClipContext.Fluid fluid;
   private final CollisionContext collisionContext;

   public ClipContext(Vec3 from, Vec3 to, ClipContext.Block block, ClipContext.Fluid fluid, Entity entity) {
      this.from = from;
      this.to = to;
      this.block = block;
      this.fluid = fluid;
      this.collisionContext = CollisionContext.of(entity);
   }

   public Vec3 getTo() {
      return this.to;
   }

   public Vec3 getFrom() {
      return this.from;
   }

   public VoxelShape getBlockShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos) {
      return this.block.get(blockState, blockGetter, blockPos, this.collisionContext);
   }

   public VoxelShape getFluidShape(FluidState fluidState, BlockGetter blockGetter, BlockPos blockPos) {
      return this.fluid.canPick(fluidState)?fluidState.getShape(blockGetter, blockPos):Shapes.empty();
   }

   public static enum Block implements ClipContext.ShapeGetter {
      COLLIDER(BlockState::getCollisionShape),
      OUTLINE(BlockState::getShape);

      private final ClipContext.ShapeGetter shapeGetter;

      private Block(ClipContext.ShapeGetter shapeGetter) {
         this.shapeGetter = shapeGetter;
      }

      public VoxelShape get(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
         return this.shapeGetter.get(blockState, blockGetter, blockPos, collisionContext);
      }
   }

   public static enum Fluid {
      NONE((fluidState) -> {
         return false;
      }),
      SOURCE_ONLY(FluidState::isSource),
      ANY((fluidState) -> {
         return !fluidState.isEmpty();
      });

      private final Predicate canPick;

      private Fluid(Predicate canPick) {
         this.canPick = canPick;
      }

      public boolean canPick(FluidState fluidState) {
         return this.canPick.test(fluidState);
      }
   }

   public interface ShapeGetter {
      VoxelShape get(BlockState var1, BlockGetter var2, BlockPos var3, CollisionContext var4);
   }
}
