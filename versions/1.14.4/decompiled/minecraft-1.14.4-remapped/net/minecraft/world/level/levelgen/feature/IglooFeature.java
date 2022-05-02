package net.minecraft.world.level.levelgen.feature;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.RandomScatteredFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.IglooPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class IglooFeature extends RandomScatteredFeature {
   public IglooFeature(Function function) {
      super(function);
   }

   public String getFeatureName() {
      return "Igloo";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return IglooFeature.FeatureStart::<init>;
   }

   protected int getRandomSalt() {
      return 14357618;
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         NoneFeatureConfiguration var6 = (NoneFeatureConfiguration)chunkGenerator.getStructureConfiguration(biome, Feature.IGLOO);
         int var7 = var3 * 16;
         int var8 = var4 * 16;
         BlockPos var9 = new BlockPos(var7, 90, var8);
         Rotation var10 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         IglooPieces.addPieces(structureManager, var9, var10, this.pieces, this.random, var6);
         this.calculateBoundingBox();
      }
   }
}
