package net.minecraft.world.level.levelgen.structure;

import java.util.Arrays;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.OceanRuinConfiguration;
import net.minecraft.world.level.levelgen.feature.RandomScatteredFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.OceanRuinPieces;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class OceanRuinFeature extends RandomScatteredFeature {
   public OceanRuinFeature(Function function) {
      super(function);
   }

   public String getFeatureName() {
      return "Ocean_Ruin";
   }

   public int getLookupRange() {
      return 3;
   }

   protected int getSpacing(ChunkGenerator chunkGenerator) {
      return chunkGenerator.getSettings().getOceanRuinSpacing();
   }

   protected int getSeparation(ChunkGenerator chunkGenerator) {
      return chunkGenerator.getSettings().getOceanRuinSeparation();
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return OceanRuinFeature.OceanRuinStart::<init>;
   }

   protected int getRandomSalt() {
      return 14357621;
   }

   public static class OceanRuinStart extends StructureStart {
      public OceanRuinStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         OceanRuinConfiguration var6 = (OceanRuinConfiguration)chunkGenerator.getStructureConfiguration(biome, Feature.OCEAN_RUIN);
         int var7 = var3 * 16;
         int var8 = var4 * 16;
         BlockPos var9 = new BlockPos(var7, 90, var8);
         Rotation var10 = Rotation.values()[this.random.nextInt(Rotation.values().length)];
         OceanRuinPieces.addPieces(structureManager, var9, var10, this.pieces, this.random, var6);
         this.calculateBoundingBox();
      }
   }

   public static enum Type {
      WARM("warm"),
      COLD("cold");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(OceanRuinFeature.Type::getName, (oceanRuinFeature$Type) -> {
         return oceanRuinFeature$Type;
      }));
      private final String name;

      private Type(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static OceanRuinFeature.Type byName(String name) {
         return (OceanRuinFeature.Type)BY_NAME.get(name);
      }
   }
}
