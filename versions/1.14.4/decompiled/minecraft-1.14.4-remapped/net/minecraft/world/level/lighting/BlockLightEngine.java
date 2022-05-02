package net.minecraft.world.level.lighting;

import java.util.concurrent.atomic.AtomicInteger;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightSectionStorage;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public final class BlockLightEngine extends LayerLightEngine {
   private static final Direction[] DIRECTIONS = Direction.values();
   private final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos();

   public BlockLightEngine(LightChunkGetter lightChunkGetter) {
      super(lightChunkGetter, LightLayer.BLOCK, new BlockLightSectionStorage(lightChunkGetter));
   }

   private int getLightEmission(long l) {
      int var3 = BlockPos.getX(l);
      int var4 = BlockPos.getY(l);
      int var5 = BlockPos.getZ(l);
      BlockGetter var6 = this.chunkSource.getChunkForLighting(var3 >> 4, var5 >> 4);
      return var6 != null?var6.getLightEmission(this.pos.set(var3, var4, var5)):0;
   }

   protected int computeLevelFromNeighbor(long var1, long var3, int var5) {
      if(var3 == Long.MAX_VALUE) {
         return 15;
      } else if(var1 == Long.MAX_VALUE) {
         return var5 + 15 - this.getLightEmission(var3);
      } else if(var5 >= 15) {
         return var5;
      } else {
         int var6 = Integer.signum(BlockPos.getX(var3) - BlockPos.getX(var1));
         int var7 = Integer.signum(BlockPos.getY(var3) - BlockPos.getY(var1));
         int var8 = Integer.signum(BlockPos.getZ(var3) - BlockPos.getZ(var1));
         Direction var9 = Direction.fromNormal(var6, var7, var8);
         if(var9 == null) {
            return 15;
         } else {
            AtomicInteger var10 = new AtomicInteger();
            BlockState var11 = this.getStateAndOpacity(var3, var10);
            if(var10.get() >= 15) {
               return 15;
            } else {
               BlockState var12 = this.getStateAndOpacity(var1, (AtomicInteger)null);
               VoxelShape var13 = this.getShape(var12, var1, var9);
               VoxelShape var14 = this.getShape(var11, var3, var9.getOpposite());
               return Shapes.faceShapeOccludes(var13, var14)?15:var5 + Math.max(1, var10.get());
            }
         }
      }
   }

   protected void checkNeighborsAfterUpdate(long var1, int var3, boolean var4) {
      long var5 = SectionPos.blockToSection(var1);

      for(Direction var10 : DIRECTIONS) {
         long var11 = BlockPos.offset(var1, var10);
         long var13 = SectionPos.blockToSection(var11);
         if(var5 == var13 || ((BlockLightSectionStorage)this.storage).storingLightForSection(var13)) {
            this.checkNeighbor(var1, var11, var3, var4);
         }
      }

   }

   protected int getComputedLevel(long var1, long var3, int var5) {
      int var6 = var5;
      if(Long.MAX_VALUE != var3) {
         int var7 = this.computeLevelFromNeighbor(Long.MAX_VALUE, var1, 0);
         if(var5 > var7) {
            var6 = var7;
         }

         if(var6 == 0) {
            return var6;
         }
      }

      long var7 = SectionPos.blockToSection(var1);
      DataLayer var9 = ((BlockLightSectionStorage)this.storage).getDataLayer(var7, true);

      for(Direction var13 : DIRECTIONS) {
         long var14 = BlockPos.offset(var1, var13);
         if(var14 != var3) {
            long var16 = SectionPos.blockToSection(var14);
            DataLayer var18;
            if(var7 == var16) {
               var18 = var9;
            } else {
               var18 = ((BlockLightSectionStorage)this.storage).getDataLayer(var16, true);
            }

            if(var18 != null) {
               int var19 = this.computeLevelFromNeighbor(var14, var1, this.getLevel(var18, var14));
               if(var6 > var19) {
                  var6 = var19;
               }

               if(var6 == 0) {
                  return var6;
               }
            }
         }
      }

      return var6;
   }

   public void onBlockEmissionIncrease(BlockPos blockPos, int var2) {
      ((BlockLightSectionStorage)this.storage).runAllUpdates();
      this.checkEdge(Long.MAX_VALUE, blockPos.asLong(), 15 - var2, true);
   }
}
