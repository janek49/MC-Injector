package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.world.entity.ExperienceOrb;

public class ClientboundAddExperienceOrbPacket implements Packet {
   private int id;
   private double x;
   private double y;
   private double z;
   private int value;

   public ClientboundAddExperienceOrbPacket() {
   }

   public ClientboundAddExperienceOrbPacket(ExperienceOrb experienceOrb) {
      this.id = experienceOrb.getId();
      this.x = experienceOrb.x;
      this.y = experienceOrb.y;
      this.z = experienceOrb.z;
      this.value = experienceOrb.getValue();
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.id = friendlyByteBuf.readVarInt();
      this.x = friendlyByteBuf.readDouble();
      this.y = friendlyByteBuf.readDouble();
      this.z = friendlyByteBuf.readDouble();
      this.value = friendlyByteBuf.readShort();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.id);
      friendlyByteBuf.writeDouble(this.x);
      friendlyByteBuf.writeDouble(this.y);
      friendlyByteBuf.writeDouble(this.z);
      friendlyByteBuf.writeShort(this.value);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleAddExperienceOrb(this);
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

   public int getValue() {
      return this.value;
   }
}
