package net.minecraft.world.level.levelgen.feature;

import java.util.Random;
import java.util.Set;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.LevelSimulatedRW;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.AbstractTreeFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;

public class GroundBushFeature extends AbstractTreeFeature {
   private final BlockState leaf;
   private final BlockState trunk;

   public GroundBushFeature(Function function, BlockState trunk, BlockState leaf) {
      super(function, false);
      this.trunk = trunk;
      this.leaf = leaf;
   }

   public boolean doPlace(Set set, LevelSimulatedRW levelSimulatedRW, Random random, BlockPos blockPos, BoundingBox boundingBox) {
      blockPos = levelSimulatedRW.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, blockPos).below();
      if(isGrassOrDirt(levelSimulatedRW, blockPos)) {
         blockPos = blockPos.above();
         this.setBlock(set, levelSimulatedRW, blockPos, this.trunk, boundingBox);

         for(int var6 = blockPos.getY(); var6 <= blockPos.getY() + 2; ++var6) {
            int var7 = var6 - blockPos.getY();
            int var8 = 2 - var7;

            for(int var9 = blockPos.getX() - var8; var9 <= blockPos.getX() + var8; ++var9) {
               int var10 = var9 - blockPos.getX();

               for(int var11 = blockPos.getZ() - var8; var11 <= blockPos.getZ() + var8; ++var11) {
                  int var12 = var11 - blockPos.getZ();
                  if(Math.abs(var10) != var8 || Math.abs(var12) != var8 || random.nextInt(2) != 0) {
                     BlockPos var13 = new BlockPos(var9, var6, var11);
                     if(isAirOrLeaves(levelSimulatedRW, var13)) {
                        this.setBlock(set, levelSimulatedRW, var13, this.leaf, boundingBox);
                     }
                  }
               }
            }
         }
      }

      return true;
   }
}
