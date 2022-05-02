package net.minecraft.world.level.saveddata.maps;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;

public class MapFrame {
   private final BlockPos pos;
   private final int rotation;
   private final int entityId;

   public MapFrame(BlockPos pos, int rotation, int entityId) {
      this.pos = pos;
      this.rotation = rotation;
      this.entityId = entityId;
   }

   public static MapFrame load(CompoundTag compoundTag) {
      BlockPos var1 = NbtUtils.readBlockPos(compoundTag.getCompound("Pos"));
      int var2 = compoundTag.getInt("Rotation");
      int var3 = compoundTag.getInt("EntityId");
      return new MapFrame(var1, var2, var3);
   }

   public CompoundTag save() {
      CompoundTag compoundTag = new CompoundTag();
      compoundTag.put("Pos", NbtUtils.writeBlockPos(this.pos));
      compoundTag.putInt("Rotation", this.rotation);
      compoundTag.putInt("EntityId", this.entityId);
      return compoundTag;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public int getRotation() {
      return this.rotation;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public String getId() {
      return frameId(this.pos);
   }

   public static String frameId(BlockPos blockPos) {
      return "frame-" + blockPos.getX() + "," + blockPos.getY() + "," + blockPos.getZ();
   }
}
