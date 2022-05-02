package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.global.LightningBolt;

public class ClientboundAddGlobalEntityPacket implements Packet {
   private int id;
   private double x;
   private double y;
   private double z;
   private int type;

   public ClientboundAddGlobalEntityPacket() {
   }

   public ClientboundAddGlobalEntityPacket(Entity entity) {
      this.id = entity.getId();
      this.x = entity.x;
      this.y = entity.y;
      this.z = entity.z;
      if(entity instanceof LightningBolt) {
         this.type = 1;
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.type = friendlyByteBuf.readByte();
      this.x = friendlyByteBuf.readDouble();
      this.y = friendlyByteBuf.readDouble();
      this.z = friendlyByteBuf.readDouble();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeByte(this.type);
      friendlyByteBuf.writeDouble(this.x);
      friendlyByteBuf.writeDouble(this.y);
      friendlyByteBuf.writeDouble(this.z);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAddGlobalEntity(this);
   }

   public int getId() {
      return this.id;
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

   public int getType() {
      return this.type;
   }
}
