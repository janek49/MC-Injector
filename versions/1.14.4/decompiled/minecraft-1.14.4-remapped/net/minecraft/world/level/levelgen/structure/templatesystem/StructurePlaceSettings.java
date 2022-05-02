package net.minecraft.world.level.levelgen.structure.templatesystem;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;

public class StructurePlaceSettings {
   private Mirror mirror = Mirror.NONE;
   private Rotation rotation = Rotation.NONE;
   private BlockPos rotationPivot = BlockPos.ZERO;
   private boolean ignoreEntities;
   @Nullable
   private ChunkPos chunkPos;
   @Nullable
   private BoundingBox boundingBox;
   private boolean keepLiquids = true;
   @Nullable
   private Random random;
   @Nullable
   private Integer preferredPalette;
   private int palette;
   private final List processors = Lists.newArrayList();
   private boolean knownShape;

   public StructurePlaceSettings copy() {
      StructurePlaceSettings structurePlaceSettings = new StructurePlaceSettings();
      structurePlaceSettings.mirror = this.mirror;
      structurePlaceSettings.rotation = this.rotation;
      structurePlaceSettings.rotationPivot = this.rotationPivot;
      structurePlaceSettings.ignoreEntities = this.ignoreEntities;
      structurePlaceSettings.chunkPos = this.chunkPos;
      structurePlaceSettings.boundingBox = this.boundingBox;
      structurePlaceSettings.keepLiquids = this.keepLiquids;
      structurePlaceSettings.random = this.random;
      structurePlaceSettings.preferredPalette = this.preferredPalette;
      structurePlaceSettings.palette = this.palette;
      structurePlaceSettings.processors.addAll(this.processors);
      structurePlaceSettings.knownShape = this.knownShape;
      return structurePlaceSettings;
   }

   public StructurePlaceSettings setMirror(Mirror mirror) {
      this.mirror = mirror;
      return this;
   }

   public StructurePlaceSettings setRotation(Rotation rotation) {
      this.rotation = rotation;
      return this;
   }

   public StructurePlaceSettings setRotationPivot(BlockPos rotationPivot) {
      this.rotationPivot = rotationPivot;
      return this;
   }

   public StructurePlaceSettings setIgnoreEntities(boolean ignoreEntities) {
      this.ignoreEntities = ignoreEntities;
      return this;
   }

   public StructurePlaceSettings setChunkPos(ChunkPos chunkPos) {
      this.chunkPos = chunkPos;
      return this;
   }

   public StructurePlaceSettings setBoundingBox(BoundingBox boundingBox) {
      this.boundingBox = boundingBox;
      return this;
   }

   public StructurePlaceSettings setRandom(@Nullable Random random) {
      this.random = random;
      return this;
   }

   public StructurePlaceSettings setKnownShape(boolean knownShape) {
      this.knownShape = knownShape;
      return this;
   }

   public StructurePlaceSettings clearProcessors() {
      this.processors.clear();
      return this;
   }

   public StructurePlaceSettings addProcessor(StructureProcessor structureProcessor) {
      this.processors.add(structureProcessor);
      return this;
   }

   public StructurePlaceSettings popProcessor(StructureProcessor structureProcessor) {
      this.processors.remove(structureProcessor);
      return this;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public BlockPos getRotationPivot() {
      return this.rotationPivot;
   }

   public Random getRandom(@Nullable BlockPos blockPos) {
      return this.random != null?this.random:(blockPos == null?new Random(Util.getMillis()):new Random(Mth.getSeed(blockPos)));
   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   @Nullable
   public BoundingBox getBoundingBox() {
      if(this.boundingBox == null && this.chunkPos != null) {
         this.updateBoundingBoxFromChunkPos();
      }

      return this.boundingBox;
   }

   public boolean getKnownShape() {
      return this.knownShape;
   }

   public List getProcessors() {
      return this.processors;
   }

   void updateBoundingBoxFromChunkPos() {
      if(this.chunkPos != null) {
         this.boundingBox = this.calculateBoundingBox(this.chunkPos);
      }

   }

   public boolean shouldKeepLiquids() {
      return this.keepLiquids;
   }

   public List getPalette(List var1, @Nullable BlockPos blockPos) {
      this.preferredPalette = Integer.valueOf(8);
      if(this.preferredPalette != null && this.preferredPalette.intValue() >= 0 && this.preferredPalette.intValue() < var1.size()) {
         return (List)var1.get(this.preferredPalette.intValue());
      } else {
         this.preferredPalette = Integer.valueOf(this.getRandom(blockPos).nextInt(var1.size()));
         return (List)var1.get(this.preferredPalette.intValue());
      }
   }

   @Nullable
   private BoundingBox calculateBoundingBox(@Nullable ChunkPos chunkPos) {
      if(chunkPos == null) {
         return this.boundingBox;
      } else {
         int var2 = chunkPos.x * 16;
         int var3 = chunkPos.z * 16;
         return new BoundingBox(var2, 0, var3, var2 + 16 - 1, 255, var3 + 16 - 1);
      }
   }
}
