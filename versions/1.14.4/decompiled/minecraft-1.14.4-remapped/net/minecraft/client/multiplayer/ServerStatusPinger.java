package net.minecraft.client.multiplayer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.authlib.GameProfile;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.multiplayer.ServerAddress;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.Connection;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.status.ClientStatusPacketListener;
import net.minecraft.network.protocol.status.ClientboundPongResponsePacket;
import net.minecraft.network.protocol.status.ClientboundStatusResponsePacket;
import net.minecraft.network.protocol.status.ServerStatus;
import net.minecraft.network.protocol.status.ServerboundPingRequestPacket;
import net.minecraft.network.protocol.status.ServerboundStatusRequestPacket;
import net.minecraft.util.Mth;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public class ServerStatusPinger {
   private static final Splitter SPLITTER = Splitter.on('\u0000').limit(6);
   private static final Logger LOGGER = LogManager.getLogger();
   private final List connections = Collections.synchronizedList(Lists.newArrayList());

   public void pingServer(final ServerData serverData) throws UnknownHostException {
      ServerAddress var2 = ServerAddress.parseString(serverData.ip);
      final Connection var3 = Connection.connectToServer(InetAddress.getByName(var2.getHost()), var2.getPort(), false);
      this.connections.add(var3);
      serverData.motd = I18n.get("multiplayer.status.pinging", new Object[0]);
      serverData.ping = -1L;
      serverData.playerList = null;
      var3.setListener(new ClientStatusPacketListener() {
         private boolean success;
         private boolean receivedPing;
         private long pingStart;

         public void handleStatusResponse(ClientboundStatusResponsePacket clientboundStatusResponsePacket) {
            if(this.receivedPing) {
               var3.disconnect(new TranslatableComponent("multiplayer.status.unrequested", new Object[0]));
            } else {
               this.receivedPing = true;
               ServerStatus var2 = clientboundStatusResponsePacket.getStatus();
               if(var2.getDescription() != null) {
                  serverData.motd = var2.getDescription().getColoredString();
               } else {
                  serverData.motd = "";
               }

               if(var2.getVersion() != null) {
                  serverData.version = var2.getVersion().getName();
                  serverData.protocol = var2.getVersion().getProtocol();
               } else {
                  serverData.version = I18n.get("multiplayer.status.old", new Object[0]);
                  serverData.protocol = 0;
               }

               if(var2.getPlayers() != null) {
                  serverData.status = ChatFormatting.GRAY + "" + var2.getPlayers().getNumPlayers() + "" + ChatFormatting.DARK_GRAY + "/" + ChatFormatting.GRAY + var2.getPlayers().getMaxPlayers();
                  if(ArrayUtils.isNotEmpty(var2.getPlayers().getSample())) {
                     StringBuilder var3 = new StringBuilder();

                     for(GameProfile var7 : var2.getPlayers().getSample()) {
                        if(var3.length() > 0) {
                           var3.append("\n");
                        }

                        var3.append(var7.getName());
                     }

                     if(var2.getPlayers().getSample().length < var2.getPlayers().getNumPlayers()) {
                        if(var3.length() > 0) {
                           var3.append("\n");
                        }

                        var3.append(I18n.get("multiplayer.status.and_more", new Object[]{Integer.valueOf(var2.getPlayers().getNumPlayers() - var2.getPlayers().getSample().length)}));
                     }

                     serverData.playerList = var3.toString();
                  }
               } else {
                  serverData.status = ChatFormatting.DARK_GRAY + I18n.get("multiplayer.status.unknown", new Object[0]);
               }

               if(var2.getFavicon() != null) {
                  String var3 = var2.getFavicon();
                  if(var3.startsWith("data:image/png;base64,")) {
                     serverData.setIconB64(var3.substring("data:image/png;base64,".length()));
                  } else {
                     ServerStatusPinger.LOGGER.error("Invalid server icon (unknown format)");
                  }
               } else {
                  serverData.setIconB64((String)null);
               }

               this.pingStart = Util.getMillis();
               var3.send(new ServerboundPingRequestPacket(this.pingStart));
               this.success = true;
            }
         }

         public void handlePongResponse(ClientboundPongResponsePacket clientboundPongResponsePacket) {
            long var2 = this.pingStart;
            long var4 = Util.getMillis();
            serverData.ping = var4 - var2;
            var3.disconnect(new TranslatableComponent("multiplayer.status.finished", new Object[0]));
         }

         public void onDisconnect(Component component) {
            if(!this.success) {
               ServerStatusPinger.LOGGER.error("Can\'t ping {}: {}", serverData.ip, component.getString());
               serverData.motd = ChatFormatting.DARK_RED + I18n.get("multiplayer.status.cannot_connect", new Object[0]);
               serverData.status = "";
               ServerStatusPinger.this.pingLegacyServer(serverData);
            }

         }

         public Connection getConnection() {
            return var3;
         }
      });

      try {
         var3.send(new ClientIntentionPacket(var2.getHost(), var2.getPort(), ConnectionProtocol.STATUS));
         var3.send(new ServerboundStatusRequestPacket());
      } catch (Throwable var5) {
         LOGGER.error(var5);
      }

   }

   private void pingLegacyServer(final ServerData serverData) {
      final ServerAddress var2 = ServerAddress.parseString(serverData.ip);
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)Connection.NETWORK_WORKER_GROUP.get())).handler(new ChannelInitializer() {
         protected void initChannel(Channel channel) throws Exception {
            try {
               channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
            } catch (ChannelException var3) {
               ;
            }

            channel.pipeline().addLast(new ChannelHandler[]{new SimpleChannelInboundHandler() {
               public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
                  super.channelActive(channelHandlerContext);
                  ByteBuf var2 = Unpooled.buffer();

                  try {
                     var2.writeByte(254);
                     var2.writeByte(1);
                     var2.writeByte(250);
                     char[] vars3 = "MC|PingHost".toCharArray();
                     var2.writeShort(vars3.length);

                     for(char var7 : vars3) {
                        var2.writeChar(var7);
                     }

                     var2.writeShort(7 + 2 * var2.getHost().length());
                     var2.writeByte(127);
                     vars3 = var2.getHost().toCharArray();
                     var2.writeShort(vars3.length);

                     for(char var7 : vars3) {
                        var2.writeChar(var7);
                     }

                     var2.writeInt(var2.getPort());
                     channelHandlerContext.channel().writeAndFlush(var2).addListener(ChannelFutureListener.CLOSE_ON_FAILURE);
                  } finally {
                     var2.release();
                  }

               }

               protected void channelRead0(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf) throws Exception {
                  short var3 = byteBuf.readUnsignedByte();
                  if(var3 == 255) {
                     String var4 = new String(byteBuf.readBytes(byteBuf.readShort() * 2).array(), StandardCharsets.UTF_16BE);
                     String[] vars5 = (String[])Iterables.toArray(ServerStatusPinger.SPLITTER.split(var4), String.class);
                     if("§1".equals(vars5[0])) {
                        int var6 = Mth.getInt(vars5[1], 0);
                        String var7 = vars5[2];
                        String var8 = vars5[3];
                        int var9 = Mth.getInt(vars5[4], -1);
                        int var10 = Mth.getInt(vars5[5], -1);
                        serverData.protocol = -1;
                        serverData.version = var7;
                        serverData.motd = var8;
                        serverData.status = ChatFormatting.GRAY + "" + var9 + "" + ChatFormatting.DARK_GRAY + "/" + ChatFormatting.GRAY + var10;
                     }
                  }

                  channelHandlerContext.close();
               }

               public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) throws Exception {
                  channelHandlerContext.close();
               }

               // $FF: synthetic method
               protected void channelRead0(ChannelHandlerContext var1, Object var2x) throws Exception {
                  this.channelRead0(var1, (ByteBuf)var2x);
               }
            }});
         }
      })).channel(NioSocketChannel.class)).connect(var2.getHost(), var2.getPort());
   }

   public void tick() {
      synchronized(this.connections) {
         Iterator<Connection> var2 = this.connections.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if(var3.isConnected()) {
               var3.tick();
            } else {
               var2.remove();
               var3.handleDisconnection();
            }
         }

      }
   }

   public void removeAll() {
      synchronized(this.connections) {
         Iterator<Connection> var2 = this.connections.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if(var3.isConnected()) {
               var2.remove();
               var3.disconnect(new TranslatableComponent("multiplayer.status.cancelled", new Object[0]));
            }
         }

      }
   }
}
