package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.ChestBlockEntity;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.material.FluidState;

public abstract class StructurePiece {
   protected static final BlockState CAVE_AIR = Blocks.CAVE_AIR.defaultBlockState();
   protected BoundingBox boundingBox;
   @Nullable
   private Direction orientation;
   private Mirror mirror;
   private Rotation rotation;
   protected int genDepth;
   private final StructurePieceType type;
   private static final Set SHAPE_CHECK_BLOCKS = ImmutableSet.builder().add(Blocks.NETHER_BRICK_FENCE).add(Blocks.TORCH).add(Blocks.WALL_TORCH).add(Blocks.OAK_FENCE).add(Blocks.SPRUCE_FENCE).add(Blocks.DARK_OAK_FENCE).add(Blocks.ACACIA_FENCE).add(Blocks.BIRCH_FENCE).add(Blocks.JUNGLE_FENCE).add(Blocks.LADDER).add(Blocks.IRON_BARS).build();

   protected StructurePiece(StructurePieceType type, int genDepth) {
      this.type = type;
      this.genDepth = genDepth;
   }

   public StructurePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
      this(structurePieceType, compoundTag.getInt("GD"));
      if(compoundTag.contains("BB")) {
         this.boundingBox = new BoundingBox(compoundTag.getIntArray("BB"));
      }

      int var3 = compoundTag.getInt("O");
      this.setOrientation(var3 == -1?null:Direction.from2DDataValue(var3));
   }

   public final CompoundTag createTag() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.putString("id", Registry.STRUCTURE_PIECE.getKey(this.getType()).toString());
      compoundTag.put("BB", this.boundingBox.createTag());
      Direction var2 = this.getOrientation();
      compoundTag.putInt("O", var2 == null?-1:var2.get2DDataValue());
      compoundTag.putInt("GD", this.genDepth);
      this.addAdditionalSaveData(compoundTag);
      return compoundTag;
   }

   protected abstract void addAdditionalSaveData(CompoundTag var1);

   public void addChildren(StructurePiece structurePiece, List list, Random random) {
   }

   public abstract boolean postProcess(LevelAccessor var1, Random var2, BoundingBox var3, ChunkPos var4);

   public BoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public int getGenDepth() {
      return this.genDepth;
   }

   public boolean isCloseToChunk(ChunkPos chunkPos, int var2) {
      int var3 = chunkPos.x << 4;
      int var4 = chunkPos.z << 4;
      return this.boundingBox.intersects(var3 - var2, var4 - var2, var3 + 15 + var2, var4 + 15 + var2);
   }

   public static StructurePiece findCollisionPiece(List list, BoundingBox boundingBox) {
      for(StructurePiece var3 : list) {
         if(var3.getBoundingBox() != null && var3.getBoundingBox().intersects(boundingBox)) {
            return var3;
         }
      }

      return null;
   }

   protected boolean edgesLiquid(BlockGetter blockGetter, BoundingBox boundingBox) {
      int var3 = Math.max(this.boundingBox.x0 - 1, boundingBox.x0);
      int var4 = Math.max(this.boundingBox.y0 - 1, boundingBox.y0);
      int var5 = Math.max(this.boundingBox.z0 - 1, boundingBox.z0);
      int var6 = Math.min(this.boundingBox.x1 + 1, boundingBox.x1);
      int var7 = Math.min(this.boundingBox.y1 + 1, boundingBox.y1);
      int var8 = Math.min(this.boundingBox.z1 + 1, boundingBox.z1);
      BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

      for(int var10 = var3; var10 <= var6; ++var10) {
         for(int var11 = var5; var11 <= var8; ++var11) {
            if(blockGetter.getBlockState(var9.set(var10, var4, var11)).getMaterial().isLiquid()) {
               return true;
            }

            if(blockGetter.getBlockState(var9.set(var10, var7, var11)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int var10 = var3; var10 <= var6; ++var10) {
         for(int var11 = var4; var11 <= var7; ++var11) {
            if(blockGetter.getBlockState(var9.set(var10, var11, var5)).getMaterial().isLiquid()) {
               return true;
            }

            if(blockGetter.getBlockState(var9.set(var10, var11, var8)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      for(int var10 = var5; var10 <= var8; ++var10) {
         for(int var11 = var4; var11 <= var7; ++var11) {
            if(blockGetter.getBlockState(var9.set(var3, var11, var10)).getMaterial().isLiquid()) {
               return true;
            }

            if(blockGetter.getBlockState(var9.set(var6, var11, var10)).getMaterial().isLiquid()) {
               return true;
            }
         }
      }

      return false;
   }

   protected int getWorldX(int var1, int var2) {
      Direction var3 = this.getOrientation();
      if(var3 == null) {
         return var1;
      } else {
         switch(var3) {
         case NORTH:
         case SOUTH:
            return this.boundingBox.x0 + var1;
         case WEST:
            return this.boundingBox.x1 - var2;
         case EAST:
            return this.boundingBox.x0 + var2;
         default:
            return var1;
         }
      }
   }

   protected int getWorldY(int i) {
      return this.getOrientation() == null?i:i + this.boundingBox.y0;
   }

   protected int getWorldZ(int var1, int var2) {
      Direction var3 = this.getOrientation();
      if(var3 == null) {
         return var2;
      } else {
         switch(var3) {
         case NORTH:
            return this.boundingBox.z1 - var2;
         case SOUTH:
            return this.boundingBox.z0 + var2;
         case WEST:
         case EAST:
            return this.boundingBox.z0 + var1;
         default:
            return var2;
         }
      }
   }

   protected void placeBlock(LevelAccessor levelAccessor, BlockState blockState, int var3, int var4, int var5, BoundingBox boundingBox) {
      BlockPos var7 = new BlockPos(this.getWorldX(var3, var5), this.getWorldY(var4), this.getWorldZ(var3, var5));
      if(boundingBox.isInside(var7)) {
         if(this.mirror != Mirror.NONE) {
            blockState = blockState.mirror(this.mirror);
         }

         if(this.rotation != Rotation.NONE) {
            blockState = blockState.rotate(this.rotation);
         }

         levelAccessor.setBlock(var7, blockState, 2);
         FluidState var8 = levelAccessor.getFluidState(var7);
         if(!var8.isEmpty()) {
            levelAccessor.getLiquidTicks().scheduleTick(var7, var8.getType(), 0);
         }

         if(SHAPE_CHECK_BLOCKS.contains(blockState.getBlock())) {
            levelAccessor.getChunk(var7).markPosForPostprocessing(var7);
         }

      }
   }

   protected BlockState getBlock(BlockGetter blockGetter, int var2, int var3, int var4, BoundingBox boundingBox) {
      int var6 = this.getWorldX(var2, var4);
      int var7 = this.getWorldY(var3);
      int var8 = this.getWorldZ(var2, var4);
      BlockPos var9 = new BlockPos(var6, var7, var8);
      return !boundingBox.isInside(var9)?Blocks.AIR.defaultBlockState():blockGetter.getBlockState(var9);
   }

   protected boolean isInterior(LevelReader levelReader, int var2, int var3, int var4, BoundingBox boundingBox) {
      int var6 = this.getWorldX(var2, var4);
      int var7 = this.getWorldY(var3 + 1);
      int var8 = this.getWorldZ(var2, var4);
      BlockPos var9 = new BlockPos(var6, var7, var8);
      return !boundingBox.isInside(var9)?false:var7 < levelReader.getHeight(Heightmap.Types.OCEAN_FLOOR_WG, var6, var8);
   }

   protected void generateAirBox(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5, int var6, int var7, int var8) {
      for(int var9 = var4; var9 <= var7; ++var9) {
         for(int var10 = var3; var10 <= var6; ++var10) {
            for(int var11 = var5; var11 <= var8; ++var11) {
               this.placeBlock(levelAccessor, Blocks.AIR.defaultBlockState(), var10, var9, var11, boundingBox);
            }
         }
      }

   }

   protected void generateBox(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5, int var6, int var7, int var8, BlockState var9, BlockState var10, boolean var11) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if(!var11 || !this.getBlock(levelAccessor, var13, var12, var14, boundingBox).isAir()) {
                  if(var12 != var4 && var12 != var7 && var13 != var3 && var13 != var6 && var14 != var5 && var14 != var8) {
                     this.placeBlock(levelAccessor, var10, var13, var12, var14, boundingBox);
                  } else {
                     this.placeBlock(levelAccessor, var9, var13, var12, var14, boundingBox);
                  }
               }
            }
         }
      }

   }

   protected void generateBox(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5, int var6, int var7, int var8, boolean var9, Random random, StructurePiece.BlockSelector structurePiece$BlockSelector) {
      for(int var12 = var4; var12 <= var7; ++var12) {
         for(int var13 = var3; var13 <= var6; ++var13) {
            for(int var14 = var5; var14 <= var8; ++var14) {
               if(!var9 || !this.getBlock(levelAccessor, var13, var12, var14, boundingBox).isAir()) {
                  structurePiece$BlockSelector.next(random, var13, var12, var14, var12 == var4 || var12 == var7 || var13 == var3 || var13 == var6 || var14 == var5 || var14 == var8);
                  this.placeBlock(levelAccessor, structurePiece$BlockSelector.getNext(), var13, var12, var14, boundingBox);
               }
            }
         }
      }

   }

   protected void generateMaybeBox(LevelAccessor levelAccessor, BoundingBox boundingBox, Random random, float var4, int var5, int var6, int var7, int var8, int var9, int var10, BlockState var11, BlockState var12, boolean var13, boolean var14) {
      for(int var15 = var6; var15 <= var9; ++var15) {
         for(int var16 = var5; var16 <= var8; ++var16) {
            for(int var17 = var7; var17 <= var10; ++var17) {
               if(random.nextFloat() <= var4 && (!var13 || !this.getBlock(levelAccessor, var16, var15, var17, boundingBox).isAir()) && (!var14 || this.isInterior(levelAccessor, var16, var15, var17, boundingBox))) {
                  if(var15 != var6 && var15 != var9 && var16 != var5 && var16 != var8 && var17 != var7 && var17 != var10) {
                     this.placeBlock(levelAccessor, var12, var16, var15, var17, boundingBox);
                  } else {
                     this.placeBlock(levelAccessor, var11, var16, var15, var17, boundingBox);
                  }
               }
            }
         }
      }

   }

   protected void maybeGenerateBlock(LevelAccessor levelAccessor, BoundingBox boundingBox, Random random, float var4, int var5, int var6, int var7, BlockState blockState) {
      if(random.nextFloat() < var4) {
         this.placeBlock(levelAccessor, blockState, var5, var6, var7, boundingBox);
      }

   }

   protected void generateUpperHalfSphere(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3, int var4, int var5, int var6, int var7, int var8, BlockState blockState, boolean var10) {
      float var11 = (float)(var6 - var3 + 1);
      float var12 = (float)(var7 - var4 + 1);
      float var13 = (float)(var8 - var5 + 1);
      float var14 = (float)var3 + var11 / 2.0F;
      float var15 = (float)var5 + var13 / 2.0F;

      for(int var16 = var4; var16 <= var7; ++var16) {
         float var17 = (float)(var16 - var4) / var12;

         for(int var18 = var3; var18 <= var6; ++var18) {
            float var19 = ((float)var18 - var14) / (var11 * 0.5F);

            for(int var20 = var5; var20 <= var8; ++var20) {
               float var21 = ((float)var20 - var15) / (var13 * 0.5F);
               if(!var10 || !this.getBlock(levelAccessor, var18, var16, var20, boundingBox).isAir()) {
                  float var22 = var19 * var19 + var17 * var17 + var21 * var21;
                  if(var22 <= 1.05F) {
                     this.placeBlock(levelAccessor, blockState, var18, var16, var20, boundingBox);
                  }
               }
            }
         }
      }

   }

   protected void fillColumnDown(LevelAccessor levelAccessor, BlockState blockState, int var3, int var4, int var5, BoundingBox boundingBox) {
      int var7 = this.getWorldX(var3, var5);
      int var8 = this.getWorldY(var4);
      int var9 = this.getWorldZ(var3, var5);
      if(boundingBox.isInside(new BlockPos(var7, var8, var9))) {
         while((levelAccessor.isEmptyBlock(new BlockPos(var7, var8, var9)) || levelAccessor.getBlockState(new BlockPos(var7, var8, var9)).getMaterial().isLiquid()) && var8 > 1) {
            levelAccessor.setBlock(new BlockPos(var7, var8, var9), blockState, 2);
            --var8;
         }

      }
   }

   protected boolean createChest(LevelAccessor levelAccessor, BoundingBox boundingBox, Random random, int var4, int var5, int var6, ResourceLocation resourceLocation) {
      BlockPos var8 = new BlockPos(this.getWorldX(var4, var6), this.getWorldY(var5), this.getWorldZ(var4, var6));
      return this.createChest(levelAccessor, boundingBox, random, var8, resourceLocation, (BlockState)null);
   }

   public static BlockState reorient(BlockGetter blockGetter, BlockPos blockPos, BlockState var2) {
      Direction var3 = null;

      for(Direction var5 : Direction.Plane.HORIZONTAL) {
         BlockPos var6 = blockPos.relative(var5);
         BlockState var7 = blockGetter.getBlockState(var6);
         if(var7.getBlock() == Blocks.CHEST) {
            return var2;
         }

         if(var7.isSolidRender(blockGetter, var6)) {
            if(var3 != null) {
               var3 = null;
               break;
            }

            var3 = var5;
         }
      }

      if(var3 != null) {
         return (BlockState)var2.setValue(HorizontalDirectionalBlock.FACING, var3.getOpposite());
      } else {
         Direction var4 = (Direction)var2.getValue(HorizontalDirectionalBlock.FACING);
         BlockPos var5 = blockPos.relative(var4);
         if(blockGetter.getBlockState(var5).isSolidRender(blockGetter, var5)) {
            var4 = var4.getOpposite();
            var5 = blockPos.relative(var4);
         }

         if(blockGetter.getBlockState(var5).isSolidRender(blockGetter, var5)) {
            var4 = var4.getClockWise();
            var5 = blockPos.relative(var4);
         }

         if(blockGetter.getBlockState(var5).isSolidRender(blockGetter, var5)) {
            var4 = var4.getOpposite();
            blockPos.relative(var4);
         }

         return (BlockState)var2.setValue(HorizontalDirectionalBlock.FACING, var4);
      }
   }

   protected boolean createChest(LevelAccessor levelAccessor, BoundingBox boundingBox, Random random, BlockPos blockPos, ResourceLocation resourceLocation, @Nullable BlockState blockState) {
      if(boundingBox.isInside(blockPos) && levelAccessor.getBlockState(blockPos).getBlock() != Blocks.CHEST) {
         if(blockState == null) {
            blockState = reorient(levelAccessor, blockPos, Blocks.CHEST.defaultBlockState());
         }

         levelAccessor.setBlock(blockPos, blockState, 2);
         BlockEntity var7 = levelAccessor.getBlockEntity(blockPos);
         if(var7 instanceof ChestBlockEntity) {
            ((ChestBlockEntity)var7).setLootTable(resourceLocation, random.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   protected boolean createDispenser(LevelAccessor levelAccessor, BoundingBox boundingBox, Random random, int var4, int var5, int var6, Direction direction, ResourceLocation resourceLocation) {
      BlockPos var9 = new BlockPos(this.getWorldX(var4, var6), this.getWorldY(var5), this.getWorldZ(var4, var6));
      if(boundingBox.isInside(var9) && levelAccessor.getBlockState(var9).getBlock() != Blocks.DISPENSER) {
         this.placeBlock(levelAccessor, (BlockState)Blocks.DISPENSER.defaultBlockState().setValue(DispenserBlock.FACING, direction), var4, var5, var6, boundingBox);
         BlockEntity var10 = levelAccessor.getBlockEntity(var9);
         if(var10 instanceof DispenserBlockEntity) {
            ((DispenserBlockEntity)var10).setLootTable(resourceLocation, random.nextLong());
         }

         return true;
      } else {
         return false;
      }
   }

   public void move(int var1, int var2, int var3) {
      this.boundingBox.move(var1, var2, var3);
   }

   @Nullable
   public Direction getOrientation() {
      return this.orientation;
   }

   public void setOrientation(@Nullable Direction orientation) {
      this.orientation = orientation;
      if(orientation == null) {
         this.rotation = Rotation.NONE;
         this.mirror = Mirror.NONE;
      } else {
         switch(orientation) {
         case SOUTH:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.NONE;
            break;
         case WEST:
            this.mirror = Mirror.LEFT_RIGHT;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         case EAST:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.CLOCKWISE_90;
            break;
         default:
            this.mirror = Mirror.NONE;
            this.rotation = Rotation.NONE;
         }
      }

   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public StructurePieceType getType() {
      return this.type;
   }

   public abstract static class BlockSelector {
      protected BlockState next = Blocks.AIR.defaultBlockState();

      public abstract void next(Random var1, int var2, int var3, int var4, boolean var5);

      public BlockState getNext() {
         return this.next;
      }
   }
}
