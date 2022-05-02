package net.minecraft.server.network;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerConnectionListener;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LegacyQueryHandler extends ChannelInboundHandlerAdapter {
   private static final Logger LOGGER = LogManager.getLogger();
   private final ServerConnectionListener serverConnectionListener;

   public LegacyQueryHandler(ServerConnectionListener serverConnectionListener) {
      this.serverConnectionListener = serverConnectionListener;
   }

   public void channelRead(ChannelHandlerContext channelHandlerContext, Object object) throws Exception {
      ByteBuf var3 = (ByteBuf)object;
      var3.markReaderIndex();
      boolean var4 = true;

      try {
         if(var3.readUnsignedByte() == 254) {
            InetSocketAddress var5 = (InetSocketAddress)channelHandlerContext.channel().remoteAddress();
            MinecraftServer var6 = this.serverConnectionListener.getServer();
            int var7 = var3.readableBytes();
            switch(var7) {
            case 0:
               LOGGER.debug("Ping: (<1.3.x) from {}:{}", var5.getAddress(), Integer.valueOf(var5.getPort()));
               String var8 = String.format("%s§%d§%d", new Object[]{var6.getMotd(), Integer.valueOf(var6.getPlayerCount()), Integer.valueOf(var6.getMaxPlayers())});
               this.sendFlushAndClose(channelHandlerContext, this.createReply(var8));
               break;
            case 1:
               if(var3.readUnsignedByte() != 1) {
                  return;
               }

               LOGGER.debug("Ping: (1.4-1.5.x) from {}:{}", var5.getAddress(), Integer.valueOf(var5.getPort()));
               String var8 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[]{Integer.valueOf(127), var6.getServerVersion(), var6.getMotd(), Integer.valueOf(var6.getPlayerCount()), Integer.valueOf(var6.getMaxPlayers())});
               this.sendFlushAndClose(channelHandlerContext, this.createReply(var8));
               break;
            default:
               boolean var8 = var3.readUnsignedByte() == 1;
               var8 = var8 & var3.readUnsignedByte() == 250;
               var8 = var8 & "MC|PingHost".equals(new String(var3.readBytes(var3.readShort() * 2).array(), StandardCharsets.UTF_16BE));
               int var9 = var3.readUnsignedShort();
               var8 = var8 & var3.readUnsignedByte() >= 73;
               var8 = var8 & 3 + var3.readBytes(var3.readShort() * 2).array().length + 4 == var9;
               var8 = var8 & var3.readInt() <= '\uffff';
               var8 = var8 & var3.readableBytes() == 0;
               if(!var8) {
                  return;
               }

               LOGGER.debug("Ping: (1.6) from {}:{}", var5.getAddress(), Integer.valueOf(var5.getPort()));
               String var10 = String.format("§1\u0000%d\u0000%s\u0000%s\u0000%d\u0000%d", new Object[]{Integer.valueOf(127), var6.getServerVersion(), var6.getMotd(), Integer.valueOf(var6.getPlayerCount()), Integer.valueOf(var6.getMaxPlayers())});
               ByteBuf var11 = this.createReply(var10);

               try {
                  this.sendFlushAndClose(channelHandlerContext, var11);
               } finally {
                  var11.release();
               }
            }

            var3.release();
            var4 = false;
            return;
         }
      } catch (RuntimeException var21) {
         return;
      } finally {
         if(var4) {
            var3.resetReaderIndex();
            channelHandlerContext.channel().pipeline().remove("legacy_query");
            channelHandlerContext.fireChannelRead(object);
         }

      }

   }

   private void sendFlushAndClose(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) {
      channelHandlerContext.pipeline().firstContext().writeAndFlush(byteBuf).addListener(ChannelFutureListener.CLOSE);
   }

   private ByteBuf createReply(String string) {
      ByteBuf byteBuf = Unpooled.buffer();
      byteBuf.writeByte(255);
      char[] vars3 = string.toCharArray();
      byteBuf.writeShort(vars3.length);

      for(char var7 : vars3) {
         byteBuf.writeChar(var7);
      }

      return byteBuf;
   }
}
