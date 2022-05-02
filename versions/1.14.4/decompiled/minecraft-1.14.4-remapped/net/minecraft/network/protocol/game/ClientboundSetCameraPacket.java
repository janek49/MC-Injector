package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ClientboundSetCameraPacket implements Packet {
   public int cameraId;

   public ClientboundSetCameraPacket() {
   }

   public ClientboundSetCameraPacket(Entity entity) {
      this.cameraId = entity.getId();
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.cameraId = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.cameraId);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetCamera(this);
   }

   @Nullable
   public Entity getEntity(Level level) {
      return level.getEntity(this.cameraId);
   }
}
