package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class StructureStart {
   public static final StructureStart INVALID_START = new StructureStart(Feature.MINESHAFT, 0, 0, Biomes.PLAINS, BoundingBox.getUnknownBox(), 0, 0L) {
      public void generatePieces(ChunkGenerator chunkGenerator, StructureManager structureManager, int var3, int var4, Biome biome) {
      }
   };
   private final StructureFeature feature;
   protected final List pieces = Lists.newArrayList();
   protected BoundingBox boundingBox;
   private final int chunkX;
   private final int chunkZ;
   private final Biome biome;
   private int references;
   protected final WorldgenRandom random;

   public StructureStart(StructureFeature feature, int chunkX, int chunkZ, Biome biome, BoundingBox boundingBox, int references, long var7) {
      this.feature = feature;
      this.chunkX = chunkX;
      this.chunkZ = chunkZ;
      this.references = references;
      this.biome = biome;
      this.random = new WorldgenRandom();
      this.random.setLargeFeatureSeed(var7, chunkX, chunkZ);
      this.boundingBox = boundingBox;
   }

   public abstract void generatePieces(ChunkGenerator var1, StructureManager var2, int var3, int var4, Biome var5);

   public BoundingBox getBoundingBox() {
      return this.boundingBox;
   }

   public List getPieces() {
      return this.pieces;
   }

   public void postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
      synchronized(this.pieces) {
         Iterator<StructurePiece> var6 = this.pieces.iterator();

         while(var6.hasNext()) {
            StructurePiece var7 = (StructurePiece)var6.next();
            if(var7.getBoundingBox().intersects(boundingBox) && !var7.postProcess(levelAccessor, random, boundingBox, chunkPos)) {
               var6.remove();
            }
         }

         this.calculateBoundingBox();
      }
   }

   protected void calculateBoundingBox() {
      this.boundingBox = BoundingBox.getUnknownBox();

      for(StructurePiece var2 : this.pieces) {
         this.boundingBox.expand(var2.getBoundingBox());
      }

   }

   public CompoundTag createTag(int var1, int var2) {
      CompoundTag compoundTag = new CompoundTag();
      if(this.isValid()) {
         compoundTag.putString("id", Registry.STRUCTURE_FEATURE.getKey(this.getFeature()).toString());
         compoundTag.putString("biome", Registry.BIOME.getKey(this.biome).toString());
         compoundTag.putInt("ChunkX", var1);
         compoundTag.putInt("ChunkZ", var2);
         compoundTag.putInt("references", this.references);
         compoundTag.put("BB", this.boundingBox.createTag());
         ListTag var4 = new ListTag();
         synchronized(this.pieces) {
            for(StructurePiece var7 : this.pieces) {
               var4.add(var7.createTag());
            }
         }

         compoundTag.put("Children", var4);
         return compoundTag;
      } else {
         compoundTag.putString("id", "INVALID");
         return compoundTag;
      }
   }

   protected void moveBelowSeaLevel(int var1, Random random, int var3) {
      int var4 = var1 - var3;
      int var5 = this.boundingBox.getYSpan() + 1;
      if(var5 < var4) {
         var5 += random.nextInt(var4 - var5);
      }

      int var6 = var5 - this.boundingBox.y1;
      this.boundingBox.move(0, var6, 0);

      for(StructurePiece var8 : this.pieces) {
         var8.move(0, var6, 0);
      }

   }

   protected void moveInsideHeights(Random random, int var2, int var3) {
      int var4 = var3 - var2 + 1 - this.boundingBox.getYSpan();
      int var5;
      if(var4 > 1) {
         var5 = var2 + random.nextInt(var4);
      } else {
         var5 = var2;
      }

      int var6 = var5 - this.boundingBox.y0;
      this.boundingBox.move(0, var6, 0);

      for(StructurePiece var8 : this.pieces) {
         var8.move(0, var6, 0);
      }

   }

   public boolean isValid() {
      return !this.pieces.isEmpty();
   }

   public int getChunkX() {
      return this.chunkX;
   }

   public int getChunkZ() {
      return this.chunkZ;
   }

   public BlockPos getLocatePos() {
      return new BlockPos(this.chunkX << 4, 0, this.chunkZ << 4);
   }

   public boolean canBeReferenced() {
      return this.references < this.getMaxReferences();
   }

   public void addReference() {
      ++this.references;
   }

   protected int getMaxReferences() {
      return 1;
   }

   public StructureFeature getFeature() {
      return this.feature;
   }
}
