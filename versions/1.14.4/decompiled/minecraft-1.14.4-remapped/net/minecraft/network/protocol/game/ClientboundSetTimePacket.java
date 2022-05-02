package net.minecraft.network.protocol.game;

import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;

public class ClientboundSetTimePacket implements Packet {
   private long gameTime;
   private long dayTime;

   public ClientboundSetTimePacket() {
   }

   public ClientboundSetTimePacket(long gameTime, long dayTime, boolean var5) {
      this.gameTime = gameTime;
      this.dayTime = dayTime;
      if(!var5) {
         this.dayTime = -this.dayTime;
         if(this.dayTime == 0L) {
            this.dayTime = -1L;
         }
      }

   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.gameTime = friendlyByteBuf.readLong();
      this.dayTime = friendlyByteBuf.readLong();
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeLong(this.gameTime);
      friendlyByteBuf.writeLong(this.dayTime);
   }

   public void handle(ClientGamePacketListener clientGamePacketListener) {
      clientGamePacketListener.handleSetTime(this);
   }

   public long getGameTime() {
      return this.gameTime;
   }

   public long getDayTime() {
      return this.dayTime;
   }
}
