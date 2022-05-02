package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import javax.crypto.Cipher;
import net.minecraft.network.CipherBase;

public class CipherEncoder extends MessageToByteEncoder {
   private final CipherBase cipher;

   public CipherEncoder(Cipher cipher) {
      this.cipher = new CipherBase(cipher);
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, ByteBuf var2, ByteBuf var3) throws Exception {
      this.cipher.encipher(var2, var3);
   }

   // $FF: synthetic method
   protected void encode(ChannelHandlerContext var1, Object var2, ByteBuf var3) throws Exception {
      this.encode(var1, (ByteBuf)var2, var3);
   }
}
