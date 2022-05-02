package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetExperiencePacket implements Packet {
   private float experienceProgress;
   private int totalExperience;
   private int experienceLevel;

   public ClientboundSetExperiencePacket() {
   }

   public ClientboundSetExperiencePacket(float experienceProgress, int totalExperience, int experienceLevel) {
      this.experienceProgress = experienceProgress;
      this.totalExperience = totalExperience;
      this.experienceLevel = experienceLevel;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.experienceProgress = friendlyByteBuf.readFloat();
      this.experienceLevel = friendlyByteBuf.readVarInt();
      this.totalExperience = friendlyByteBuf.readVarInt();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeFloat(this.experienceProgress);
      friendlyByteBuf.writeVarInt(this.experienceLevel);
      friendlyByteBuf.writeVarInt(this.totalExperience);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetExperience(this);
   }

   public float getExperienceProgress() {
      return this.experienceProgress;
   }

   public int getTotalExperience() {
      return this.totalExperience;
   }

   public int getExperienceLevel() {
      return this.experienceLevel;
   }
}
