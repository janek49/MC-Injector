package net.minecraft.world.level.levelgen.feature;

import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.RandomScatteredFeature;
import net.minecraft.world.level.levelgen.feature.ShipwreckConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.ShipwreckPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ShipwreckFeature extends RandomScatteredFeature {
   public ShipwreckFeature(Function function) {
      super(function);
   }

   public String getFeatureName() {
      return "Shipwreck";
   }

   public int getLookupRange() {
      return 3;
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return ShipwreckFeature.FeatureStart::<init>;
   }

   protected int getRandomSalt() {
      return 165745295;
   }

   protected int getSpacing(ChunkGenerator chunkGenerator) {
      return chunkGenerator.getSettings().getShipwreckSpacing();
   }

   protected int getSeparation(ChunkGenerator chunkGenerator) {
      return chunkGenerator.getSettings().getShipwreckSeparation();
   }

   public static class FeatureStart extends StructureStart {
      public FeatureStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         ShipwreckConfiguration var6 = (ShipwreckConfiguration)chunkGenerator.getStructureConfiguration(biome, Feature.SHIPWRECK);
         Rotation var7 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         BlockPos var8 = new BlockPos(var3 * 16, 90, var4 * 16);
         ShipwreckPieces.addPieces(structureManager, var8, var7, this.pieces, this.random, var6);
         this.calculateBoundingBox();
      }
   }
}
