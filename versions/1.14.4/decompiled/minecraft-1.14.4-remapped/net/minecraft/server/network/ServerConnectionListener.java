package net.minecraft.server.network;

import com.google.common.collect.Lists;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.local.LocalAddress;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.ServerSocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.GenericFutureListener;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.CrashReportDetail;
import net.minecraft.ReportedException;
import net.minecraft.network.Connection;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.LegacyQueryHandler;
import net.minecraft.server.network.MemoryServerHandshakePacketListenerImpl;
import net.minecraft.server.network.ServerHandshakePacketListenerImpl;
import net.minecraft.util.LazyLoadedValue;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ServerConnectionListener {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final LazyLoadedValue SERVER_EVENT_GROUP = new LazyLoadedValue(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Server IO #%d").setDaemon(true).build());
   });
   public static final LazyLoadedValue SERVER_EPOLL_EVENT_GROUP = new LazyLoadedValue(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Server IO #%d").setDaemon(true).build());
   });
   private final MinecraftServer server;
   public volatile boolean running;
   private final List channels = Collections.synchronizedList(Lists.newArrayList());
   private final List connections = Collections.synchronizedList(Lists.newArrayList());

   public ServerConnectionListener(MinecraftServer server) {
      this.server = server;
      this.running = true;
   }

   public void startTcpServerListener(@Nullable InetAddress inetAddress, int var2) throws IOException {
      synchronized(this.channels) {
         Class<? extends ServerSocketChannel> var4;
         LazyLoadedValue<? extends EventLoopGroup> var5;
         if(Epoll.isAvailable() && this.server.isEpollEnabled()) {
            var4 = EpollServerSocketChannel.class;
            var5 = SERVER_EPOLL_EVENT_GROUP;
            LOGGER.info("Using epoll channel type");
         } else {
            var4 = NioServerSocketChannel.class;
            var5 = SERVER_EVENT_GROUP;
            LOGGER.info("Using default channel type");
         }

         this.channels.add(((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(var4)).childHandler(new ChannelInitializer() {
            protected void initChannel(Channel channel) throws Exception {
               try {
                  channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
               } catch (ChannelException var3) {
                  ;
               }

               channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("legacy_query", new LegacyQueryHandler(ServerConnectionListener.this)).addLast("splitter", new Varint21FrameDecoder()).addLast("decoder", new PacketDecoder(PacketFlow.SERVERBOUND)).addLast("prepender", new Varint21LengthFieldPrepender()).addLast("encoder", new PacketEncoder(PacketFlow.CLIENTBOUND));
               Connection var2 = new Connection(PacketFlow.SERVERBOUND);
               ServerConnectionListener.this.connections.add(var2);
               channel.pipeline().addLast("packet_handler", var2);
               var2.setListener(new ServerHandshakePacketListenerImpl(ServerConnectionListener.this.server, var2));
            }
         }).group((EventLoopGroup)var5.get()).localAddress(inetAddress, var2)).bind().syncUninterruptibly());
      }
   }

   public SocketAddress startMemoryChannel() {
      ChannelFuture var1;
      synchronized(this.channels) {
         var1 = ((ServerBootstrap)((ServerBootstrap)(new ServerBootstrap()).channel(LocalServerChannel.class)).childHandler(new ChannelInitializer() {
            protected void initChannel(Channel channel) throws Exception {
               Connection var2 = new Connection(PacketFlow.SERVERBOUND);
               var2.setListener(new MemoryServerHandshakePacketListenerImpl(ServerConnectionListener.this.server, var2));
               ServerConnectionListener.this.connections.add(var2);
               channel.pipeline().addLast("packet_handler", var2);
            }
         }).group((EventLoopGroup)SERVER_EVENT_GROUP.get()).localAddress(LocalAddress.ANY)).bind().syncUninterruptibly();
         this.channels.add(var1);
      }

      return var1.channel().localAddress();
   }

   public void stop() {
      this.running = false;

      for(ChannelFuture var2 : this.channels) {
         try {
            var2.channel().close().sync();
         } catch (InterruptedException var4) {
            LOGGER.error("Interrupted whilst closing channel");
         }
      }

   }

   public void tick() {
      synchronized(this.connections) {
         Iterator<Connection> var2 = this.connections.iterator();

         while(var2.hasNext()) {
            Connection var3 = (Connection)var2.next();
            if(!var3.isConnecting()) {
               if(var3.isConnected()) {
                  try {
                     var3.tick();
                  } catch (Exception var8) {
                     if(var3.isMemoryConnection()) {
                        CrashReport var5 = CrashReport.forThrowable(var8, "Ticking memory connection");
                        CrashReportCategory var6 = var5.addCategory("Ticking connection");
                        var6.setDetail("Connection", var3::toString);
                        throw new ReportedException(var5);
                     }

                     LOGGER.warn("Failed to handle packet for {}", var3.getRemoteAddress(), var8);
                     Component var5 = new TextComponent("Internal server error");
                     var3.send(new ClientboundDisconnectPacket(var5), (future) -> {
                        var3.disconnect(var5);
                     });
                     var3.setReadOnly();
                  }
               } else {
                  var2.remove();
                  var3.handleDisconnection();
               }
            }
         }

      }
   }

   public MinecraftServer getServer() {
      return this.server;
   }
}
