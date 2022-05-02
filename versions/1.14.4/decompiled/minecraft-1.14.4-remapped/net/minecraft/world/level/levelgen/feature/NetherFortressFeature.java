package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.NetherBridgePieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class NetherFortressFeature extends StructureFeature {
   private static final List FORTRESS_ENEMIES = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.BLAZE, 10, 2, 3), new Biome.SpawnerData(EntityType.ZOMBIE_PIGMAN, 5, 4, 4), new Biome.SpawnerData(EntityType.WITHER_SKELETON, 8, 5, 5), new Biome.SpawnerData(EntityType.SKELETON, 2, 5, 5), new Biome.SpawnerData(EntityType.MAGMA_CUBE, 3, 4, 4)});

   public NetherFortressFeature(Function function) {
      super(function);
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      int var5 = var3 >> 4;
      int var6 = var4 >> 4;
      random.setSeed((long)(var5 ^ var6 << 4) ^ chunkGenerator.getSeed());
      random.nextInt();
      if(random.nextInt(3) != 0) {
         return false;
      } else if(var3 != (var5 << 4) + 4 + random.nextInt(8)) {
         return false;
      } else if(var4 != (var6 << 4) + 4 + random.nextInt(8)) {
         return false;
      } else {
         Biome var7 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
         return chunkGenerator.isBiomeValidStartForStructure(var7, Feature.NETHER_BRIDGE);
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return NetherFortressFeature.NetherBridgeStart::<init>;
   }

   public String getFeatureName() {
      return "Fortress";
   }

   public int getLookupRange() {
      return 8;
   }

   public List getSpecialEnemies() {
      return FORTRESS_ENEMIES;
   }

   public static class NetherBridgeStart extends StructureStart {
      public NetherBridgeStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         NetherBridgePieces.StartPiece var6 = new NetherBridgePieces.StartPiece(this.random, (var3 << 4) + 2, (var4 << 4) + 2);
         this.pieces.add(var6);
         var6.addChildren(var6, this.pieces, this.random);
         List<StructurePiece> var7 = var6.pendingChildren;

         while(!var7.isEmpty()) {
            int var8 = this.random.nextInt(var7.size());
            StructurePiece var9 = (StructurePiece)var7.remove(var8);
            var9.addChildren(var6, this.pieces, this.random);
         }

         this.calculateBoundingBox();
         this.moveInsideHeights(this.random, 48, 70);
      }
   }
}
