package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundResourcePackPacket implements Packet {
   private String url;
   private String hash;

   public ClientboundResourcePackPacket() {
   }

   public ClientboundResourcePackPacket(String url, String hash) {
      this.url = url;
      this.hash = hash;
      if(hash.length() > 40) {
         throw new IllegalArgumentException("Hash is too long (max 40, was " + hash.length() + ")");
      }
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.url = friendlyByteBuf.readUtf(32767);
      this.hash = friendlyByteBuf.readUtf(40);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(this.url);
      friendlyByteBuf.writeUtf(this.hash);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleResourcePack(this);
   }

   public String getUrl() {
      return this.url;
   }

   public String getHash() {
      return this.hash;
   }
}
