package net.minecraft.network.protocol.game;

import java.io.IOException;
import java.util.List;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.player.Player;

public class ClientboundAddPlayerPacket implements Packet {
   private int entityId;
   private UUID playerId;
   private double x;
   private double y;
   private double z;
   private byte yRot;
   private byte xRot;
   private SynchedEntityData entityData;
   private List unpack;

   public ClientboundAddPlayerPacket() {
   }

   public ClientboundAddPlayerPacket(Player player) {
      this.entityId = player.getId();
      this.playerId = player.getGameProfile().getId();
      this.x = player.x;
      this.y = player.y;
      this.z = player.z;
      this.yRot = (byte)((int)(player.yRot * 256.0F / 360.0F));
      this.xRot = (byte)((int)(player.xRot * 256.0F / 360.0F));
      this.entityData = player.getEntityData();
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entityId = friendlyByteBuf.readVarInt();
      this.playerId = friendlyByteBuf.readUUID();
      this.x = friendlyByteBuf.readDouble();
      this.y = friendlyByteBuf.readDouble();
      this.z = friendlyByteBuf.readDouble();
      this.yRot = friendlyByteBuf.readByte();
      this.xRot = friendlyByteBuf.readByte();
      this.unpack = SynchedEntityData.unpack(friendlyByteBuf);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entityId);
      friendlyByteBuf.writeUUID(this.playerId);
      friendlyByteBuf.writeDouble(this.x);
      friendlyByteBuf.writeDouble(this.y);
      friendlyByteBuf.writeDouble(this.z);
      friendlyByteBuf.writeByte(this.yRot);
      friendlyByteBuf.writeByte(this.xRot);
      this.entityData.packAll(friendlyByteBuf);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAddPlayer(this);
   }

   @Nullable
   public List getUnpackedData() {
      return this.unpack;
   }

   public int getEntityId() {
      return this.entityId;
   }

   public UUID getPlayerId() {
      return this.playerId;
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

   public byte getyRot() {
      return this.yRot;
   }

   public byte getxRot() {
      return this.xRot;
   }
}
