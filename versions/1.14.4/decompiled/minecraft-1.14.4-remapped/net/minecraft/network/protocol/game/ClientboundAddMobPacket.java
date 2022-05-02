package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddMobPacket implements Packet {
   private int id;
   private UUID uuid;
   private int type;
   private double x;
   private double y;
   private double z;
   private int xd;
   private int yd;
   private int zd;
   private byte yRot;
   private byte xRot;
   private byte yHeadRot;
   private SynchedEntityData entityData;
   private List unpack;

   public ClientboundAddMobPacket() {
   }

   public ClientboundAddMobPacket(LivingEntity livingEntity) {
      this.id = livingEntity.getId();
      this.uuid = livingEntity.getUUID();
      this.type = Registry.ENTITY_TYPE.getId(livingEntity.getType());
      this.x = livingEntity.x;
      this.y = livingEntity.y;
      this.z = livingEntity.z;
      this.yRot = (byte)((int)(livingEntity.yRot * 256.0F / 360.0F));
      this.xRot = (byte)((int)(livingEntity.xRot * 256.0F / 360.0F));
      this.yHeadRot = (byte)((int)(livingEntity.yHeadRot * 256.0F / 360.0F));
      double var2 = 3.9D;
      Vec3 var4 = livingEntity.getDeltaMovement();
      double var5 = Mth.clamp(var4.x, -3.9D, 3.9D);
      double var7 = Mth.clamp(var4.y, -3.9D, 3.9D);
      double var9 = Mth.clamp(var4.z, -3.9D, 3.9D);
      this.xd = (int)(var5 * 8000.0D);
      this.yd = (int)(var7 * 8000.0D);
      this.zd = (int)(var9 * 8000.0D);
      this.entityData = livingEntity.getEntityData();
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.uuid = friendlyByteBuf.readUUID();
      this.type = friendlyByteBuf.readVarInt();
      this.x = friendlyByteBuf.readDouble();
      this.y = friendlyByteBuf.readDouble();
      this.z = friendlyByteBuf.readDouble();
      this.yRot = friendlyByteBuf.readByte();
      this.xRot = friendlyByteBuf.readByte();
      this.yHeadRot = friendlyByteBuf.readByte();
      this.xd = friendlyByteBuf.readShort();
      this.yd = friendlyByteBuf.readShort();
      this.zd = friendlyByteBuf.readShort();
      this.unpack = SynchedEntityData.unpack(friendlyByteBuf);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeUUID(this.uuid);
      friendlyByteBuf.writeVarInt(this.type);
      friendlyByteBuf.writeDouble(this.x);
      friendlyByteBuf.writeDouble(this.y);
      friendlyByteBuf.writeDouble(this.z);
      friendlyByteBuf.writeByte(this.yRot);
      friendlyByteBuf.writeByte(this.xRot);
      friendlyByteBuf.writeByte(this.yHeadRot);
      friendlyByteBuf.writeShort(this.xd);
      friendlyByteBuf.writeShort(this.yd);
      friendlyByteBuf.writeShort(this.zd);
      this.entityData.packAll(friendlyByteBuf);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAddMob(this);
   }

   @Nullable
   public List getUnpackedData() {
      return this.unpack;
   }

   public int getId() {
      return this.id;
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public int getType() {
      return this.type;
   }

   public double getX() {
      return this.x;
   }

   public double getY() {
      return this.y;
   }

   public double getZ() {
      return this.z;
   }

   public int getXd() {
      return this.xd;
   }

   public int getYd() {
      return this.yd;
   }

   public int getZd() {
      return this.zd;
   }

   public byte getyRot() {
      return this.yRot;
   }

   public byte getxRot() {
      return this.xRot;
   }

   public byte getyHeadRot() {
      return this.yHeadRot;
   }
}
