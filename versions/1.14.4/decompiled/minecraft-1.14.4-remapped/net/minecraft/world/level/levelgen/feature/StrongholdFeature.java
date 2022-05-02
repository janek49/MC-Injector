package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StrongholdPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class StrongholdFeature extends StructureFeature {
   private boolean isSpotSelected;
   private ChunkPos[] strongholdPos;
   private final List discoveredStarts = Lists.newArrayList();
   private long currentSeed;

   public StrongholdFeature(Function function) {
      super(function);
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      if(this.currentSeed != chunkGenerator.getSeed()) {
         this.reset();
      }

      if(!this.isSpotSelected) {
         this.generatePositions(chunkGenerator);
         this.isSpotSelected = true;
      }

      for(ChunkPos var8 : this.strongholdPos) {
         if(var3 == var8.x && var4 == var8.z) {
            return true;
         }
      }

      return false;
   }

   private void reset() {
      this.isSpotSelected = false;
      this.strongholdPos = null;
      this.discoveredStarts.clear();
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return StrongholdFeature.StrongholdStart::<init>;
   }

   public String getFeatureName() {
      return "Stronghold";
   }

   public int getLookupRange() {
      return 8;
   }

   @Nullable
   public BlockPos getNearestGeneratedFeature(Level level, ChunkGenerator chunkGenerator, BlockPos var3, int var4, boolean var5) {
      if(!chunkGenerator.getBiomeSource().canGenerateStructure(this)) {
         return null;
      } else {
         if(this.currentSeed != level.getSeed()) {
            this.reset();
         }

         if(!this.isSpotSelected) {
            this.generatePositions(chunkGenerator);
            this.isSpotSelected = true;
         }

         BlockPos var6 = null;
         BlockPos.MutableBlockPos var7 = new BlockPos.MutableBlockPos();
         double var8 = Double.MAX_VALUE;

         for(ChunkPos var13 : this.strongholdPos) {
            var7.set((var13.x << 4) + 8, 32, (var13.z << 4) + 8);
            double var14 = var7.distSqr(var3);
            if(var6 == null) {
               var6 = new BlockPos(var7);
               var8 = var14;
            } else if(var14 < var8) {
               var6 = new BlockPos(var7);
               var8 = var14;
            }
         }

         return var6;
      }
   }

   private void generatePositions(ChunkGenerator chunkGenerator) {
      this.currentSeed = chunkGenerator.getSeed();
      List<Biome> var2 = Lists.newArrayList();

      for(Biome var4 : Registry.BIOME) {
         if(var4 != null && chunkGenerator.isBiomeValidStartForStructure(var4, Feature.STRONGHOLD)) {
            var2.add(var4);
         }
      }

      int var3 = chunkGenerator.getSettings().getStrongholdsDistance();
      int var4 = chunkGenerator.getSettings().getStrongholdsCount();
      int var5 = chunkGenerator.getSettings().getStrongholdsSpread();
      this.strongholdPos = new ChunkPos[var4];
      int var6 = 0;

      for(StructureStart var8 : this.discoveredStarts) {
         if(var6 < this.strongholdPos.length) {
            this.strongholdPos[var6++] = new ChunkPos(var8.getChunkX(), var8.getChunkZ());
         }
      }

      Random var7 = new Random();
      var7.setSeed(chunkGenerator.getSeed());
      double var8 = var7.nextDouble() * 3.141592653589793D * 2.0D;
      int var10 = var6;
      if(var6 < this.strongholdPos.length) {
         int var11 = 0;
         int var12 = 0;

         for(int var13 = 0; var13 < this.strongholdPos.length; ++var13) {
            double var14 = (double)(4 * var3 + var3 * var12 * 6) + (var7.nextDouble() - 0.5D) * (double)var3 * 2.5D;
            int var16 = (int)Math.round(Math.cos(var8) * var14);
            int var17 = (int)Math.round(Math.sin(var8) * var14);
            BlockPos var18 = chunkGenerator.getBiomeSource().findBiome((var16 << 4) + 8, (var17 << 4) + 8, 112, var2, var7);
            if(var18 != null) {
               var16 = var18.getX() >> 4;
               var17 = var18.getZ() >> 4;
            }

            if(var13 >= var10) {
               this.strongholdPos[var13] = new ChunkPos(var16, var17);
            }

            var8 += 6.283185307179586D / (double)var5;
            ++var11;
            if(var11 == var5) {
               ++var12;
               var11 = 0;
               var5 = var5 + 2 * var5 / (var12 + 1);
               var5 = Math.min(var5, this.strongholdPos.length - var13);
               var8 += var7.nextDouble() * 3.141592653589793D * 2.0D;
            }
         }
      }

   }

   public static class StrongholdStart extends StructureStart {
      public StrongholdStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         int var6 = 0;
         long var7 = chunkGenerator.getSeed();

         while(true) {
            this.pieces.clear();
            this.boundingBox = BoundingBox.getUnknownBox();
            this.random.setLargeFeatureSeed(var7 + (long)(var6++), var3, var4);
            StrongholdPieces.resetPieces();
            StrongholdPieces.StartPiece var9 = new StrongholdPieces.StartPiece(this.random, (var3 << 4) + 2, (var4 << 4) + 2);
            this.pieces.add(var9);
            var9.addChildren(var9, this.pieces, this.random);
            List<StructurePiece> var10 = var9.pendingChildren;

            while(!var10.isEmpty()) {
               int var11 = this.random.nextInt(var10.size());
               StructurePiece var12 = (StructurePiece)var10.remove(var11);
               var12.addChildren(var9, this.pieces, this.random);
            }

            this.calculateBoundingBox();
            this.moveBelowSeaLevel(chunkGenerator.getSeaLevel(), this.random, 10);
            if(!this.pieces.isEmpty() && var9.portalRoomPiece != null) {
               break;
            }
         }

         ((StrongholdFeature)this.getFeature()).discoveredStarts.add(this);
      }
   }
}
