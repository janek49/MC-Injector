package net.minecraft.world.level.levelgen.feature;

import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.MineshaftConfiguration;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.MineShaftPieces;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class MineshaftFeature extends StructureFeature {
   public MineshaftFeature(Function function) {
      super(function);
   }

   public boolean isFeatureChunk(ChunkGenerator chunkGenerator, Random random, int var3, int var4) {
      ((WorldgenRandom)random).setLargeFeatureSeed(chunkGenerator.getSeed(), var3, var4);
      Biome var5 = chunkGenerator.getBiomeSource().getBiome(new BlockPos((var3 << 4) + 9, 0, (var4 << 4) + 9));
      if(chunkGenerator.isBiomeValidStartForStructure(var5, Feature.MINESHAFT)) {
         MineshaftConfiguration var6 = (MineshaftConfiguration)chunkGenerator.getStructureConfiguration(var5, Feature.MINESHAFT);
         double var7 = var6.probability;
         return random.nextDouble() < var7;
      } else {
         return false;
      }
   }

   public StructureFeature.StructureStartFactory getStartFactory() {
      return MineshaftFeature.MineShaftStart::<init>;
   }

   public String getFeatureName() {
      return "Mineshaft";
   }

   public int getLookupRange() {
      return 8;
   }

   public static class MineShaftStart extends StructureStart {
      public MineShaftStart(StructureFeature structureFeature, int var2, int var3, Biome biome, BoundingBox boundingBox, int var6, long var7) {
         super(structureFeature, var2, var3, biome, boundingBox, var6, var7);
      }

      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
         MineshaftConfiguration var6 = (MineshaftConfiguration)chunkGenerator.getStructureConfiguration(biome, Feature.MINESHAFT);
         MineShaftPieces.MineShaftRoom var7 = new MineShaftPieces.MineShaftRoom(0, this.random, (var3 << 4) + 2, (var4 << 4) + 2, var6.type);
         this.pieces.add(var7);
         var7.addChildren(var7, this.pieces, this.random);
         this.calculateBoundingBox();
         if(var6.type == MineshaftFeature.Type.MESA) {
            int var8 = -5;
            int var9 = chunkGenerator.getSeaLevel() - this.boundingBox.y1 + this.boundingBox.getYSpan() / 2 - -5;
            this.boundingBox.move(0, var9, 0);

            for(StructurePiece var11 : this.pieces) {
               var11.move(0, var9, 0);
            }
         } else {
            this.moveBelowSeaLevel(chunkGenerator.getSeaLevel(), this.random, 10);
         }

      }
   }

   public static enum Type {
      NORMAL("normal"),
      MESA("mesa");

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap(MineshaftFeature.Type::getName, (mineshaftFeature$Type) -> {
         return mineshaftFeature$Type;
      }));
      private final String name;

      private Type(String name) {
         this.name = name;
      }

      public String getName() {
         return this.name;
      }

      public static MineshaftFeature.Type byName(String name) {
         return (MineshaftFeature.Type)BY_NAME.get(name);
      }

      public static MineshaftFeature.Type byId(int id) {
         return id >= 0 && id < values().length?values()[id]:NORMAL;
      }
   }
}
