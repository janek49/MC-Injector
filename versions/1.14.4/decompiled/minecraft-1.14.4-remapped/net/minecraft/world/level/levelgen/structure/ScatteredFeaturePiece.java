package net.minecraft.world.level.levelgen.structure;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;

public abstract class ScatteredFeaturePiece extends StructurePiece {
   protected final int width;
   protected final int height;
   protected final int depth;
   protected int heightPosition = -1;

   protected ScatteredFeaturePiece(StructurePieceType structurePieceType, Random random, int var3, int var4, int var5, int width, int height, int depth) {
      super(structurePieceType, 0);
      this.width = width;
      this.height = height;
      this.depth = depth;
      this.setOrientation(Direction.Plane.HORIZONTAL.getRandomDirection(random));
      if(this.getOrientation().getAxis() == Direction.Axis.Z) {
         this.boundingBox = new BoundingBox(var3, var4, var5, var3 + width - 1, var4 + height - 1, var5 + depth - 1);
      } else {
         this.boundingBox = new BoundingBox(var3, var4, var5, var3 + depth - 1, var4 + height - 1, var5 + width - 1);
      }

   }

   protected ScatteredFeaturePiece(StructurePieceType structurePieceType, CompoundTag compoundTag) {
      super(structurePieceType, compoundTag);
      this.width = compoundTag.getInt("Width");
      this.height = compoundTag.getInt("Height");
      this.depth = compoundTag.getInt("Depth");
      this.heightPosition = compoundTag.getInt("HPos");
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putInt("Width", this.width);
      compoundTag.putInt("Height", this.height);
      compoundTag.putInt("Depth", this.depth);
      compoundTag.putInt("HPos", this.heightPosition);
   }

   protected boolean updateAverageGroundHeight(LevelAccessor levelAccessor, BoundingBox boundingBox, int var3) {
      if(this.heightPosition >= 0) {
         return true;
      } else {
         int var4 = 0;
         int var5 = 0;
         BlockPos.MutableBlockPos var6 = new BlockPos.MutableBlockPos();

         for(int var7 = this.boundingBox.z0; var7 <= this.boundingBox.z1; ++var7) {
            for(int var8 = this.boundingBox.x0; var8 <= this.boundingBox.x1; ++var8) {
               var6.set(var8, 64, var7);
               if(boundingBox.isInside(var6)) {
                  var4 += levelAccessor.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var6).getY();
                  ++var5;
               }
            }
         }

         if(var5 == 0) {
            return false;
         } else {
            this.heightPosition = var4 / var5;
            this.boundingBox.move(0, this.heightPosition - this.boundingBox.y0 + var3, 0);
            return true;
         }
      }
   }
}
