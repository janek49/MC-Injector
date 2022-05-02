package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.UUID;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

public class ClientboundAddEntityPacket implements Packet {
   private int id;
   private UUID uuid;
   private double x;
   private double y;
   private double z;
   private int xa;
   private int ya;
   private int za;
   private int xRot;
   private int yRot;
   private EntityType type;
   private int data;

   public ClientboundAddEntityPacket() {
   }

   public ClientboundAddEntityPacket(int id, UUID uuid, double x, double y, double z, float var9, float var10, EntityType type, int data, Vec3 vec3) {
      this.id = id;
      this.uuid = uuid;
      this.x = x;
      this.y = y;
      this.z = z;
      this.xRot = Mth.floor(var9 * 256.0F / 360.0F);
      this.yRot = Mth.floor(var10 * 256.0F / 360.0F);
      this.type = type;
      this.data = data;
      this.xa = (int)(Mth.clamp(vec3.x, -3.9D, 3.9D) * 8000.0D);
      this.ya = (int)(Mth.clamp(vec3.y, -3.9D, 3.9D) * 8000.0D);
      this.za = (int)(Mth.clamp(vec3.z, -3.9D, 3.9D) * 8000.0D);
   }

   public ClientboundAddEntityPacket(Entity entity) {
      this(entity, 0);
   }

   public ClientboundAddEntityPacket(Entity entity, int var2) {
      this(entity.getId(), entity.getUUID(), entity.x, entity.y, entity.z, entity.xRot, entity.yRot, entity.getType(), var2, entity.getDeltaMovement());
   }

   public ClientboundAddEntityPacket(Entity entity, EntityType entityType, int var3, BlockPos blockPos) {
      this(entity.getId(), entity.getUUID(), (double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), entity.xRot, entity.yRot, entityType, var3, entity.getDeltaMovement());
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.uuid = friendlyByteBuf.readUUID();
      this.type = (EntityType)Registry.ENTITY_TYPE.byId(friendlyByteBuf.readVarInt());
      this.x = friendlyByteBuf.readDouble();
      this.y = friendlyByteBuf.readDouble();
      this.z = friendlyByteBuf.readDouble();
      this.xRot = friendlyByteBuf.readByte();
      this.yRot = friendlyByteBuf.readByte();
      this.data = friendlyByteBuf.readInt();
      this.xa = friendlyByteBuf.readShort();
      this.ya = friendlyByteBuf.readShort();
      this.za = friendlyByteBuf.readShort();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeUUID(this.uuid);
      friendlyByteBuf.writeVarInt(Registry.ENTITY_TYPE.getId(this.type));
      friendlyByteBuf.writeDouble(this.x);
      friendlyByteBuf.writeDouble(this.y);
      friendlyByteBuf.writeDouble(this.z);
      friendlyByteBuf.writeByte(this.xRot);
      friendlyByteBuf.writeByte(this.yRot);
      friendlyByteBuf.writeInt(this.data);
      friendlyByteBuf.writeShort(this.xa);
      friendlyByteBuf.writeShort(this.ya);
      friendlyByteBuf.writeShort(this.za);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAddEntity(this);
   }

   public int getId() {
      return this.id;
   }

   public UUID getUUID() {
      return this.uuid;
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

   public double getXa() {
      return (double)this.xa / 8000.0D;
   }

   public double getYa() {
      return (double)this.ya / 8000.0D;
   }

   public double getZa() {
      return (double)this.za / 8000.0D;
   }

   public int getxRot() {
      return this.xRot;
   }

   public int getyRot() {
      return this.yRot;
   }

   public EntityType getType() {
      return this.type;
   }

   public int getData() {
      return this.data;
   }
}
