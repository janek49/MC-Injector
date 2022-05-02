package net.minecraft.world.level.levelgen.feature;

import java.util.function.Function;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.RandomScatteredFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.JunglePyramidPiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class JunglePyramidFeature extends RandomScatteredFeature {
   public JunglePyramidFeature(Function function) {
      super(function);
   }

   public String getFeatureName() {
      return "Jungle_Pyramid";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return JunglePyramidFeature.FeatureStart::<init>;
   }

   protected int getRandomSalt() {
      return 14357619;
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         JunglePyramidPiece var6 = new JunglePyramidPiece(this.random, var3 * 16, var4 * 16);
         this.pieces.add(var6);
         this.calculateBoundingBox();
      }
   }
}
