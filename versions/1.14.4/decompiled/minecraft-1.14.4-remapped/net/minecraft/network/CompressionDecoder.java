package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.DecoderException;
import java.util.List;
import java.util.zip.Inflater;
import net.minecraft.network.FriendlyByteBuf;

public class CompressionDecoder extends ByteToMessageDecoder {
   private final Inflater inflater;
   private int threshold;

   public CompressionDecoder(int threshold) {
      this.threshold = threshold;
      this.inflater = new Inflater();
   }

   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
      if(byteBuf.readableBytes() != 0) {
         FriendlyByteBuf var4 = new FriendlyByteBuf(byteBuf);
         int var5 = var4.readVarInt();
         if(var5 == 0) {
            list.add(var4.readBytes(var4.readableBytes()));
         } else {
            if(var5 < this.threshold) {
               throw new DecoderException("Badly compressed packet - size of " + var5 + " is below server threshold of " + this.threshold);
            }

            if(var5 > 2097152) {
               throw new DecoderException("Badly compressed packet - size of " + var5 + " is larger than protocol maximum of " + 2097152);
            }

            byte[] vars6 = new byte[var4.readableBytes()];
            var4.readBytes(vars6);
            this.inflater.setInput(vars6);
            byte[] vars7 = new byte[var5];
            this.inflater.inflate(vars7);
            list.add(Unpooled.wrappedBuffer(vars7));
            this.inflater.reset();
         }

      }
   }

   public void setThreshold(int threshold) {
      this.threshold = threshold;
   }
}
