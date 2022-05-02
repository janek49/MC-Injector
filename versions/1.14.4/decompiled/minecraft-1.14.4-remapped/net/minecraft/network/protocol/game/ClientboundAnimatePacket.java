package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;

public class ClientboundAnimatePacket implements Packet {
   private int id;
   private int action;

   public ClientboundAnimatePacket() {
   }

   public ClientboundAnimatePacket(Entity entity, int action) {
      this.id = entity.getId();
      this.action = action;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.action = friendlyByteBuf.readUnsignedByte();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeByte(this.action);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAnimate(this);
   }

   public int getId() {
      return this.id;
   }

   public int getAction() {
      return this.action;
   }
}
