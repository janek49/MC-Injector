package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundEntityEventPacket implements Packet {
   private int entityId;
   private byte eventId;

   public ClientboundEntityEventPacket() {
   }

   public ClientboundEntityEventPacket(Entity entity, byte eventId) {
      this.entityId = entity.getId();
      this.eventId = eventId;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entityId = friendlyByteBuf.readInt();
      this.eventId = friendlyByteBuf.readByte();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeInt(this.entityId);
      friendlyByteBuf.writeByte(this.eventId);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleEntityEvent(this);
   }

   public Entity getEntity(Level level) {
      return level.getEntity(this.entityId);
   }

   public byte getEventId() {
      return this.eventId;
   }
}
