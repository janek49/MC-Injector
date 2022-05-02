package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundGameEventPacket implements Packet {
   public static final String[] EVENT_LANGUAGE_ID = new String[]{"block.minecraft.bed.not_valid"};
   private int event;
   private float param;

   public ClientboundGameEventPacket() {
   }

   public ClientboundGameEventPacket(int event, float param) {
      this.event = event;
      this.param = param;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.event = friendlyByteBuf.readUnsignedByte();
      this.param = friendlyByteBuf.readFloat();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeByte(this.event);
      friendlyByteBuf.writeFloat(this.param);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleGameEvent(this);
   }

   public int getEvent() {
      return this.event;
   }

   public float getParam() {
      return this.param;
   }
}
