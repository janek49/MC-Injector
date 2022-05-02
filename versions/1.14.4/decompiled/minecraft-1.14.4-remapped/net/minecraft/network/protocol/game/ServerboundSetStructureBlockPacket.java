package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.StructureBlockEntity;
import net.minecraft.world.level.block.state.properties.StructureMode;

public class ServerboundSetStructureBlockPacket implements Packet {
   private BlockPos pos;
   private StructureBlockEntity.UpdateType updateType;
   private StructureMode mode;
   private String name;
   private BlockPos offset;
   private BlockPos size;
   private Mirror mirror;
   private Rotation rotation;
   private String data;
   private boolean ignoreEntities;
   private boolean showAir;
   private boolean showBoundingBox;
   private float integrity;
   private long seed;

   public ServerboundSetStructureBlockPacket() {
   }

   public ServerboundSetStructureBlockPacket(BlockPos pos, StructureBlockEntity.UpdateType updateType, StructureMode mode, String name, BlockPos offset, BlockPos size, Mirror mirror, Rotation rotation, String data, boolean ignoreEntities, boolean showAir, boolean showBoundingBox, float integrity, long seed) {
      this.pos = pos;
      this.updateType = updateType;
      this.mode = mode;
      this.name = name;
      this.offset = offset;
      this.size = size;
      this.mirror = mirror;
      this.rotation = rotation;
      this.data = data;
      this.ignoreEntities = ignoreEntities;
      this.showAir = showAir;
      this.showBoundingBox = showBoundingBox;
      this.integrity = integrity;
      this.seed = seed;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.pos = friendlyByteBuf.readBlockPos();
      this.updateType = (StructureBlockEntity.UpdateType)friendlyByteBuf.readEnum(StructureBlockEntity.UpdateType.class);
      this.mode = (StructureMode)friendlyByteBuf.readEnum(StructureMode.class);
      this.name = friendlyByteBuf.readUtf(32767);
      this.offset = new BlockPos(Mth.clamp(friendlyByteBuf.readByte(), -32, 32), Mth.clamp(friendlyByteBuf.readByte(), -32, 32), Mth.clamp(friendlyByteBuf.readByte(), -32, 32));
      this.size = new BlockPos(Mth.clamp(friendlyByteBuf.readByte(), 0, 32), Mth.clamp(friendlyByteBuf.readByte(), 0, 32), Mth.clamp(friendlyByteBuf.readByte(), 0, 32));
      this.mirror = (Mirror)friendlyByteBuf.readEnum(Mirror.class);
      this.rotation = (Rotation)friendlyByteBuf.readEnum(Rotation.class);
      this.data = friendlyByteBuf.readUtf(12);
      this.integrity = Mth.clamp(friendlyByteBuf.readFloat(), 0.0F, 1.0F);
      this.seed = friendlyByteBuf.readVarLong();
      int var2 = friendlyByteBuf.readByte();
      this.ignoreEntities = (var2 & 1) != 0;
      this.showAir = (var2 & 2) != 0;
      this.showBoundingBox = (var2 & 4) != 0;
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeBlockPos(this.pos);
      friendlyByteBuf.writeEnum(this.updateType);
      friendlyByteBuf.writeEnum(this.mode);
      friendlyByteBuf.writeUtf(this.name);
      friendlyByteBuf.writeByte(this.offset.getX());
      friendlyByteBuf.writeByte(this.offset.getY());
      friendlyByteBuf.writeByte(this.offset.getZ());
      friendlyByteBuf.writeByte(this.size.getX());
      friendlyByteBuf.writeByte(this.size.getY());
      friendlyByteBuf.writeByte(this.size.getZ());
      friendlyByteBuf.writeEnum(this.mirror);
      friendlyByteBuf.writeEnum(this.rotation);
      friendlyByteBuf.writeUtf(this.data);
      friendlyByteBuf.writeFloat(this.integrity);
      friendlyByteBuf.writeVarLong(this.seed);
      int var2 = 0;
      if(this.ignoreEntities) {
         var2 |= 1;
      }

      if(this.showAir) {
         var2 |= 2;
      }

      if(this.showBoundingBox) {
         var2 |= 4;
      }

      friendlyByteBuf.writeByte(var2);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleSetStructureBlock(this);
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public StructureBlockEntity.UpdateType getUpdateType() {
      return this.updateType;
   }

   public StructureMode getMode() {
      return this.mode;
   }

   public String getName() {
      return this.name;
   }

   public BlockPos getOffset() {
      return this.offset;
   }

   public BlockPos getSize() {
      return this.size;
   }

   public Mirror getMirror() {
      return this.mirror;
   }

   public Rotation getRotation() {
      return this.rotation;
   }

   public String getData() {
      return this.data;
   }

   public boolean isIgnoreEntities() {
      return this.ignoreEntities;
   }

   public boolean isShowAir() {
      return this.showAir;
   }

   public boolean isShowBoundingBox() {
      return this.showBoundingBox;
   }

   public float getIntegrity() {
      return this.integrity;
   }

   public long getSeed() {
      return this.seed;
   }
}
