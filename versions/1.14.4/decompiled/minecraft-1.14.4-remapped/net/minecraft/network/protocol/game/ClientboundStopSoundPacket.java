package net.minecraft.network.protocol.game;

import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundSource;

public class ClientboundStopSoundPacket implements Packet {
   private ResourceLocation name;
   private SoundSource source;

   public ClientboundStopSoundPacket() {
   }

   public ClientboundStopSoundPacket(@Nullable ResourceLocation name, @Nullable SoundSource source) {
      this.name = name;
      this.source = source;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      int var2 = friendlyByteBuf.readByte();
      if((var2 & 1) > 0) {
         this.source = (SoundSource)friendlyByteBuf.readEnum(SoundSource.class);
      }

      if((var2 & 2) > 0) {
         this.name = friendlyByteBuf.readResourceLocation();
      }

   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      if(this.source != null) {
         if(this.name != null) {
            friendlyByteBuf.writeByte(3);
            friendlyByteBuf.writeEnum(this.source);
            friendlyByteBuf.writeResourceLocation(this.name);
         } else {
            friendlyByteBuf.writeByte(1);
            friendlyByteBuf.writeEnum(this.source);
         }
      } else if(this.name != null) {
         friendlyByteBuf.writeByte(2);
         friendlyByteBuf.writeResourceLocation(this.name);
      } else {
         friendlyByteBuf.writeByte(0);
      }

   }

   @Nullable
   public ResourceLocation getName() {
      return this.name;
   }

   @Nullable
   public SoundSource getSource() {
      return this.source;
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleStopSoundEvent(this);
   }
}
