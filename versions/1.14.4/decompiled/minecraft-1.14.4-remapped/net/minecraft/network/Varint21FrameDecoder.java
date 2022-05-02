package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.CorruptedFrameException;
import java.util.List;
import net.minecraft.network.FriendlyByteBuf;

public class Varint21FrameDecoder extends ByteToMessageDecoder {
   protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List list) throws Exception {
      byteBuf.markReaderIndex();
      byte[] vars4 = new byte[3];

      for(int var5 = 0; var5 < vars4.length; ++var5) {
         if(!byteBuf.isReadable()) {
            byteBuf.resetReaderIndex();
            return;
         }

         vars4[var5] = byteBuf.readByte();
         if(vars4[var5] >= 0) {
            FriendlyByteBuf var6 = new FriendlyByteBuf(Unpooled.wrappedBuffer(vars4));

            try {
               int var7 = var6.readVarInt();
               if(byteBuf.readableBytes() >= var7) {
                  list.add(byteBuf.readBytes(var7));
                  return;
               }

               byteBuf.resetReaderIndex();
            } finally {
               var6.release();
            }

            return;
         }
      }

      throw new CorruptedFrameException("length wider than 21-bit");
   }
}
