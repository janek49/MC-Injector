package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundRotateHeadPacket implements Packet {
   private int entityId;
   private byte yHeadRot;

   public ClientboundRotateHeadPacket() {
   }

   public ClientboundRotateHeadPacket(Entity entity, byte yHeadRot) {
      this.entityId = entity.getId();
      this.yHeadRot = yHeadRot;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.entityId = friendlyByteBuf.readVarInt();
      this.yHeadRot = friendlyByteBuf.readByte();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.entityId);
      friendlyByteBuf.writeByte(this.yHeadRot);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleRotateMob(this);
   }

   public Entity getEntity(Level level) {
      return level.getEntity(this.entityId);
   }

   public byte getYHeadRot() {
      return this.yHeadRot;
   }
}
