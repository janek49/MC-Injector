package net.minecraft.network;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import java.io.IOException;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class PacketEncoder extends MessageToByteEncoder {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final Marker MARKER = MarkerManager.getMarker("PACKET_SENT", Connection.PACKET_MARKER);
   private final PacketFlow flow;

   public PacketEncoder(PacketFlow flow) {
      this.flow = flow;
   }

   protected void encode(ChannelHandlerContext channelHandlerContext, Packet packet, ByteBuf byteBuf) throws Exception {
      ConnectionProtocol var4 = (ConnectionProtocol)channelHandlerContext.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get();
      if(var4 == null) {
         throw new RuntimeException("ConnectionProtocol unknown: " + packet);
      } else {
         Integer var5 = var4.getPacketId(this.flow, packet);
         if(LOGGER.isDebugEnabled()) {
            LOGGER.debug(MARKER, "OUT: [{}:{}] {}", channelHandlerContext.channel().attr(Connection.ATTRIBUTE_PROTOCOL).get(), var5, packet.getClass().getName());
         }

         if(var5 == null) {
            throw new IOException("Can\'t serialize unregistered packet");
         } else {
            FriendlyByteBuf var6 = new FriendlyByteBuf(byteBuf);
            var6.writeVarInt(var5.intValue());

            try {
               packet.write(var6);
            } catch (Throwable var8) {
               LOGGER.error(var8);
               if(packet.isSkippable()) {
                  throw new SkipPacketException(var8);
               } else {
                  throw var8;
               }
            }
         }
      }
   }

   // $FF: synthetic method
   protected void encode(ChannelHandlerContext var1, Object var2, ByteBuf var3) throws Exception {
      this.encode(var1, (Packet)var2, var3);
   }
}
