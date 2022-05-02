package net.minecraft.network.protocol.game;

import io.netty.buffer.ByteBuf;
import java.io.IOException;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;

public class ServerboundCustomPayloadPacket implements Packet {
   public static final ResourceLocation BRAND = new ResourceLocation("brand");
   private ResourceLocation identifier;
   private FriendlyByteBuf data;

   public ServerboundCustomPayloadPacket() {
   }

   public ServerboundCustomPayloadPacket(ResourceLocation identifier, FriendlyByteBuf data) {
      this.identifier = identifier;
      this.data = data;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.identifier = friendlyByteBuf.readResourceLocation();
      int var2 = friendlyByteBuf.readableBytes();
      if(var2 >= 0 && var2 <= 32767) {
         this.data = new FriendlyByteBuf(friendlyByteBuf.readBytes(var2));
      } else {
         throw new IOException("Payload may not be larger than 32767 bytes");
      }
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeResourceLocation(this.identifier);
      friendlyByteBuf.writeBytes((ByteBuf)this.data);
   }

   public void handle(ServerGamePacketListener serverGamePacketListener) {
      serverGamePacketListener.handleCustomPayload(this);
      if(this.data != null) {
         this.data.release();
      }

   }
}
