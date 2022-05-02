package net.minecraft.world.level.levelgen.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.RandomScatteredFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.SwamplandHutPiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class SwamplandHutFeature extends RandomScatteredFeature {
   private static final List SWAMPHUT_ENEMIES = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.WITCH, 1, 1, 1)});
   private static final List SWAMPHUT_ANIMALS = Lists.newArrayList(new Biome.SpawnerData[]{new Biome.SpawnerData(EntityType.CAT, 1, 1, 1)});

   public SwamplandHutFeature(Function function) {
      super(function);
   }

   public String getFeatureName() {
      return "Swamp_Hut";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return SwamplandHutFeature.FeatureStart::<init>;
   }

   protected int getRandomSalt() {
      return 14357620;
   }

   public List getSpecialEnemies() {
      return SWAMPHUT_ENEMIES;
   }

   public List getSpecialAnimals() {
      return SWAMPHUT_ANIMALS;
   }

   public boolean isSwamphut(LevelAccessor levelAccessor, BlockPos blockPos) {
      StructureStart var3 = this.getStructureAt(levelAccessor, blockPos, true);
      if(var3 != StructureStart.INVALID_START && var3 instanceof SwamplandHutFeature.FeatureStart && !var3.getPieces().isEmpty()) {
         StructurePiece var4 = (StructurePiece)var3.getPieces().get(0);
         return var4 instanceof SwamplandHutPiece;
      } else {
         return false;
      }
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         SwamplandHutPiece var6 = new SwamplandHutPiece(this.random, var3 * 16, var4 * 16);
         this.pieces.add(var6);
         this.calculateBoundingBox();
      }
   }
}
