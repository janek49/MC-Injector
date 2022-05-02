package net.minecraft.client.renderer.chunk;

import com.fox2code.repacker.ClientJarOnly;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.material.FluidState;

@ClientJarOnly
public class RenderChunkRegion implements BlockAndBiomeGetter {
   protected final int centerX;
   protected final int centerZ;
   protected final BlockPos start;
   protected final int xLength;
   protected final int yLength;
   protected final int zLength;
   protected final LevelChunk[][] chunks;
   protected final BlockState[] blockStates;
   protected final FluidState[] fluidStates;
   protected final Level level;

   @Nullable
   public static RenderChunkRegion createIfNotEmpty(Level level, BlockPos var1, BlockPos var2, int var3) {
      int var4 = var1.getX() - var3 >> 4;
      int var5 = var1.getZ() - var3 >> 4;
      int var6 = var2.getX() + var3 >> 4;
      int var7 = var2.getZ() + var3 >> 4;
      LevelChunk[][] vars8 = new LevelChunk[var6 - var4 + 1][var7 - var5 + 1];

      for(int var9 = var4; var9 <= var6; ++var9) {
         for(int var10 = var5; var10 <= var7; ++var10) {
            vars8[var9 - var4][var10 - var5] = level.getChunk(var9, var10);
         }
      }

      boolean var9 = true;

      for(int var10 = var1.getX() >> 4; var10 <= var2.getX() >> 4; ++var10) {
         for(int var11 = var1.getZ() >> 4; var11 <= var2.getZ() >> 4; ++var11) {
            LevelChunk var12 = vars8[var10 - var4][var11 - var5];
            if(!var12.isYSpaceEmpty(var1.getY(), var2.getY())) {
               var9 = false;
            }
         }
      }

      if(var9) {
         return null;
      } else {
         int var10 = 1;
         BlockPos var11 = var1.offset(-1, -1, -1);
         BlockPos var12 = var2.offset(1, 1, 1);
         return new RenderChunkRegion(level, var4, var5, vars8, var11, var12);
      }
   }

   public RenderChunkRegion(Level level, int centerX, int centerZ, LevelChunk[][] chunks, BlockPos start, BlockPos var6) {
      this.level = level;
      this.centerX = centerX;
      this.centerZ = centerZ;
      this.chunks = chunks;
      this.start = start;
      this.xLength = var6.getX() - start.getX() + 1;
      this.yLength = var6.getY() - start.getY() + 1;
      this.zLength = var6.getZ() - start.getZ() + 1;
      this.blockStates = new BlockState[this.xLength * this.yLength * this.zLength];
      this.fluidStates = new FluidState[this.xLength * this.yLength * this.zLength];

      for(BlockPos var8 : BlockPos.betweenClosed(start, var6)) {
         int var9 = (var8.getX() >> 4) - centerX;
         int var10 = (var8.getZ() >> 4) - centerZ;
         LevelChunk var11 = chunks[var9][var10];
         int var12 = this.index(var8);
         this.blockStates[var12] = var11.getBlockState(var8);
         this.fluidStates[var12] = var11.getFluidState(var8);
      }

   }

   protected final int index(BlockPos blockPos) {
      return this.index(blockPos.getX(), blockPos.getY(), blockPos.getZ());
   }

   protected int index(int var1, int var2, int var3) {
      int var4 = var1 - this.start.getX();
      int var5 = var2 - this.start.getY();
      int var6 = var3 - this.start.getZ();
      return var6 * this.xLength * this.yLength + var5 * this.xLength + var4;
   }

   public BlockState getBlockState(BlockPos blockPos) {
      return this.blockStates[this.index(blockPos)];
   }

   public FluidState getFluidState(BlockPos blockPos) {
      return this.fluidStates[this.index(blockPos)];
   }

   public int getBrightness(LightLayer lightLayer, BlockPos blockPos) {
      return this.level.getBrightness(lightLayer, blockPos);
   }

   public Biome getBiome(BlockPos blockPos) {
      int var2 = (blockPos.getX() >> 4) - this.centerX;
      int var3 = (blockPos.getZ() >> 4) - this.centerZ;
      return this.chunks[var2][var3].getBiome(blockPos);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos) {
      return this.getBlockEntity(blockPos, LevelChunk.EntityCreationType.IMMEDIATE);
   }

   @Nullable
   public BlockEntity getBlockEntity(BlockPos blockPos, LevelChunk.EntityCreationType levelChunk$EntityCreationType) {
      int var3 = (blockPos.getX() >> 4) - this.centerX;
      int var4 = (blockPos.getZ() >> 4) - this.centerZ;
      return this.chunks[var3][var4].getBlockEntity(blockPos, levelChunk$EntityCreationType);
   }
}
