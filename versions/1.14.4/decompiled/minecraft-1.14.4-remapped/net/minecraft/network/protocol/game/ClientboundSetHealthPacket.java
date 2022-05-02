package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetHealthPacket implements Packet {
   private float health;
   private int food;
   private float saturation;

   public ClientboundSetHealthPacket() {
   }

   public ClientboundSetHealthPacket(float health, int food, float saturation) {
      this.health = health;
      this.food = food;
      this.saturation = saturation;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.health = friendlyByteBuf.readFloat();
      this.food = friendlyByteBuf.readVarInt();
      this.saturation = friendlyByteBuf.readFloat();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeFloat(this.health);
      friendlyByteBuf.writeVarInt(this.food);
      friendlyByteBuf.writeFloat(this.saturation);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetHealth(this);
   }

   public float getHealth() {
      return this.health;
   }

   public int getFood() {
      return this.food;
   }

   public float getSaturation() {
      return this.saturation;
   }
}
