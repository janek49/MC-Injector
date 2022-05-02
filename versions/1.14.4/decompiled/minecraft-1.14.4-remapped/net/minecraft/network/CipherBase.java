package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import javax.crypto.Cipher;
import javax.crypto.ShortBufferException;

public class CipherBase {
   private final Cipher cipher;
   private byte[] heapIn = new byte[0];
   private byte[] heapOut = new byte[0];

   protected CipherBase(Cipher cipher) {
      this.cipher = cipher;
   }

   private byte[] bufToByte(ByteBuf byteBuf) {
      int var2 = byteBuf.readableBytes();
      if(this.heapIn.length < var2) {
         this.heapIn = new byte[var2];
      }

      byteBuf.readBytes(this.heapIn, 0, var2);
      return this.heapIn;
   }

   protected ByteBuf decipher(ChannelHandlerContext channelHandlerContext, ByteBuf var2) throws ShortBufferException {
      int var3 = var2.readableBytes();
      byte[] vars4 = this.bufToByte(var2);
      ByteBuf var5 = channelHandlerContext.alloc().heapBuffer(this.cipher.getOutputSize(var3));
      var5.writerIndex(this.cipher.update(vars4, 0, var3, var5.array(), var5.arrayOffset()));
      return var5;
   }

   protected void encipher(ByteBuf var1, ByteBuf var2) throws ShortBufferException {
      int var3 = var1.readableBytes();
      byte[] vars4 = this.bufToByte(var1);
      int var5 = this.cipher.getOutputSize(var3);
      if(this.heapOut.length < var5) {
         this.heapOut = new byte[var5];
      }

      var2.writeBytes(this.heapOut, 0, this.cipher.update(vars4, 0, var3, this.heapOut));
   }
}
