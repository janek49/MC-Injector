package net.minecraft.world.level.levelgen.structure;

import com.google.common.collect.Lists;
import com.mojang.datafixers.Dynamic;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.util.Deserializer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.feature.StructurePieceType;
import net.minecraft.world.level.levelgen.feature.structures.EmptyPoolElement;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public abstract class PoolElementStructurePiece extends StructurePiece {
   protected final StructurePoolElement element;
   protected BlockPos position;
   private final int groundLevelDelta;
   protected final Rotation rotation;
   private final List junctions = Lists.newArrayList();
   private final StructureManager structureManager;

   public PoolElementStructurePiece(StructurePieceType structurePieceType, StructureManager structureManager, StructurePoolElement element, BlockPos position, int groundLevelDelta, Rotation rotation, BoundingBox boundingBox) {
      super(structurePieceType, 0);
      this.structureManager = structureManager;
      this.element = element;
      this.position = position;
      this.groundLevelDelta = groundLevelDelta;
      this.rotation = rotation;
      this.boundingBox = boundingBox;
   }

   public PoolElementStructurePiece(StructureManager structureManager, CompoundTag compoundTag, StructurePieceType structurePieceType) {
      super(structurePieceType, compoundTag);
      this.structureManager = structureManager;
      this.position = new BlockPos(compoundTag.getInt("PosX"), compoundTag.getInt("PosY"), compoundTag.getInt("PosZ"));
      this.groundLevelDelta = compoundTag.getInt("ground_level_delta");
      this.element = (StructurePoolElement)Deserializer.deserialize(new Dynamic(NbtOps.INSTANCE, compoundTag.getCompound("pool_element")), Registry.STRUCTURE_POOL_ELEMENT, "element_type", EmptyPoolElement.INSTANCE);
      this.rotation = Rotation.valueOf(compoundTag.getString("rotation"));
      this.boundingBox = this.element.getBoundingBox(structureManager, this.position, this.rotation);
      ListTag var4 = compoundTag.getList("junctions", 10);
      this.junctions.clear();
      var4.forEach((tag) -> {
         this.junctions.add(JigsawJunction.deserialize(new Dynamic(NbtOps.INSTANCE, tag)));
      });
   }

   protected void addAdditionalSaveData(CompoundTag compoundTag) {
      compoundTag.putInt("PosX", this.position.getX());
      compoundTag.putInt("PosY", this.position.getY());
      compoundTag.putInt("PosZ", this.position.getZ());
      compoundTag.putInt("ground_level_delta", this.groundLevelDelta);
      compoundTag.put("pool_element", (Tag)this.element.serialize(NbtOps.INSTANCE).getValue());
      compoundTag.putString("rotation", this.rotation.name());
      ListTag var2 = new ListTag();

      for(JigsawJunction var4 : this.junctions) {
         var2.add(var4.serialize(NbtOps.INSTANCE).getValue());
      }

      compoundTag.put("junctions", var2);
   }

   public boolean postProcess(LevelAccessor levelAccessor, Random random, BoundingBox boundingBox, ChunkPos chunkPos) {
      return this.element.place(this.structureManager, levelAccessor, this.position, this.rotation, boundingBox, random);
   }

   public void move(int var1, int var2, int var3) {
      super.move(var1, var2, var3);
      this.position = this.position.offset(var1, var2, var3);
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String toString() {
      return String.format("<%s | %s | %s | %s>", new Object[]{this.getClass().getSimpleName(), this.position, this.rotation, this.element});
   }

   public StructurePoolElement getElement() {
      return this.element;
   }

   public BlockPos getPosition() {
      return this.position;
   }

   public int getGroundLevelDelta() {
      return this.groundLevelDelta;
   }

   public void addJunction(JigsawJunction jigsawJunction) {
      this.junctions.add(jigsawJunction);
   }

   public List getJunctions() {
      return this.junctions;
   }
}
