package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import java.util.List;
import javax.crypto.Cipher;
import net.minecraft.network.CipherBase;

public class CipherDecoder extends MessageToMessageDecoder {
   private final CipherBase cipher;

   public CipherDecoder(Cipher cipher) {
      this.cipher = new CipherBase(cipher);
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
      list.add(this.cipher.decipher(channelHandlerContext, byteBuf));
   }

   // $FF: synthetic method
   protected void decode(ChannelHandlerContext var1, Object var2, List var3) throws Exception {
      this.decode(var1, (ByteBuf)var2, var3);
   }
}
