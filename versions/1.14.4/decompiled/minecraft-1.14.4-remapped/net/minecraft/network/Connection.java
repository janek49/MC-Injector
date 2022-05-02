package net.minecraft.network;

import com.google.common.collect.Queues;
import com.google.common.util.concurrent.ThreadFactoryBuilder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.epoll.Epoll;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollSocketChannel;
import io.netty.channel.local.LocalChannel;
import io.netty.channel.local.LocalServerChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.TimeoutException;
import io.netty.util.AttributeKey;
import io.netty.util.concurrent.GenericFutureListener;
import java.net.InetAddress;
import java.net.SocketAddress;
import java.util.Queue;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import javax.crypto.SecretKey;
import net.minecraft.network.CipherDecoder;
import net.minecraft.network.CipherEncoder;
import net.minecraft.network.CompressionDecoder;
import net.minecraft.network.CompressionEncoder;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.PacketDecoder;
import net.minecraft.network.PacketEncoder;
import net.minecraft.network.PacketListener;
import net.minecraft.network.SkipPacketException;
import net.minecraft.network.Varint21FrameDecoder;
import net.minecraft.network.Varint21LengthFieldPrepender;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundDisconnectPacket;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;
import net.minecraft.util.Crypt;
import net.minecraft.util.LazyLoadedValue;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

public class Connection extends SimpleChannelInboundHandler {
   private static final Logger LOGGER = LogManager.getLogger();
   public static final Marker ROOT_MARKER = MarkerManager.getMarker("NETWORK");
   public static final Marker PACKET_MARKER = MarkerManager.getMarker("NETWORK_PACKETS", ROOT_MARKER);
   public static final AttributeKey ATTRIBUTE_PROTOCOL = AttributeKey.valueOf("protocol");
   public static final LazyLoadedValue NETWORK_WORKER_GROUP = new LazyLoadedValue(() -> {
      return new NioEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Client IO #%d").setDaemon(true).build());
   });
   public static final LazyLoadedValue NETWORK_EPOLL_WORKER_GROUP = new LazyLoadedValue(() -> {
      return new EpollEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Epoll Client IO #%d").setDaemon(true).build());
   });
   public static final LazyLoadedValue LOCAL_WORKER_GROUP = new LazyLoadedValue(() -> {
      return new DefaultEventLoopGroup(0, (new ThreadFactoryBuilder()).setNameFormat("Netty Local Client IO #%d").setDaemon(true).build());
   });
   private final PacketFlow receiving;
   private final Queue queue = Queues.newConcurrentLinkedQueue();
   private Channel channel;
   private SocketAddress address;
   private PacketListener packetListener;
   private Component disconnectedReason;
   private boolean encrypted;
   private boolean disconnectionHandled;
   private int receivedPackets;
   private int sentPackets;
   private float averageReceivedPackets;
   private float averageSentPackets;
   private int tickCount;
   private boolean handlingFault;

   public Connection(PacketFlow receiving) {
      this.receiving = receiving;
   }

   public void channelActive(ChannelHandlerContext channelHandlerContext) throws Exception {
      super.channelActive(channelHandlerContext);
      this.channel = channelHandlerContext.channel();
      this.address = this.channel.remoteAddress();

      try {
         this.setProtocol(ConnectionProtocol.HANDSHAKING);
      } catch (Throwable var3) {
         LOGGER.fatal(var3);
      }

   }

   public void setProtocol(ConnectionProtocol protocol) {
      this.channel.attr(ATTRIBUTE_PROTOCOL).set(protocol);
      this.channel.config().setAutoRead(true);
      LOGGER.debug("Enabled auto read");
   }

   public void channelInactive(ChannelHandlerContext channelHandlerContext) throws Exception {
      this.disconnect(new TranslatableComponent("disconnect.endOfStream", new Object[0]));
   }

   public void exceptionCaught(ChannelHandlerContext channelHandlerContext, Throwable throwable) {
      if(throwable instanceof SkipPacketException) {
         LOGGER.debug("Skipping packet due to errors", throwable.getCause());
      } else {
         boolean var3 = !this.handlingFault;
         this.handlingFault = true;
         if(this.channel.isOpen()) {
            if(throwable instanceof TimeoutException) {
               LOGGER.debug("Timeout", throwable);
               this.disconnect(new TranslatableComponent("disconnect.timeout", new Object[0]));
            } else {
               Component var4 = new TranslatableComponent("disconnect.genericReason", new Object[]{"Internal Exception: " + throwable});
               if(var3) {
                  LOGGER.debug("Failed to sent packet", throwable);
                  this.send(new ClientboundDisconnectPacket(var4), (future) -> {
                     this.disconnect(var4);
                  });
                  this.setReadOnly();
               } else {
                  LOGGER.debug("Double fault", throwable);
                  this.disconnect(var4);
               }
            }

         }
      }
   }

   protected void channelRead0(ChannelHandlerContext channelHandlerContext, Packet packet) throws Exception {
      if(this.channel.isOpen()) {
         try {
            genericsFtw(packet, this.packetListener);
         } catch (RunningOnDifferentThreadException var4) {
            ;
         }

         ++this.receivedPackets;
      }

   }

   private static void genericsFtw(Packet packet, PacketListener packetListener) {
      packet.handle(packetListener);
   }

   public void setListener(PacketListener listener) {
      Validate.notNull(listener, "packetListener", new Object[0]);
      LOGGER.debug("Set listener of {} to {}", this, listener);
      this.packetListener = listener;
   }

   public void send(Packet packet) {
      this.send(packet, (GenericFutureListener)null);
   }

   public void send(Packet packet, @Nullable GenericFutureListener genericFutureListener) {
      if(this.isConnected()) {
         this.flushQueue();
         this.sendPacket(packet, genericFutureListener);
      } else {
         this.queue.add(new Connection.PacketHolder(packet, genericFutureListener));
      }

   }

   private void sendPacket(Packet packet, @Nullable GenericFutureListener genericFutureListener) {
      ConnectionProtocol var3 = ConnectionProtocol.getProtocolForPacket(packet);
      ConnectionProtocol var4 = (ConnectionProtocol)this.channel.attr(ATTRIBUTE_PROTOCOL).get();
      ++this.sentPackets;
      if(var4 != var3) {
         LOGGER.debug("Disabled auto read");
         this.channel.config().setAutoRead(false);
      }

      if(this.channel.eventLoop().inEventLoop()) {
         if(var3 != var4) {
            this.setProtocol(var3);
         }

         ChannelFuture var5 = this.channel.writeAndFlush(packet);
         if(genericFutureListener != null) {
            var5.addListener(genericFutureListener);
         }

         var5.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
      } else {
         this.channel.eventLoop().execute(() -> {
            if(var3 != var4) {
               this.setProtocol(var3);
            }

            ChannelFuture var5 = this.channel.writeAndFlush(packet);
            if(genericFutureListener != null) {
               var5.addListener(genericFutureListener);
            }

            var5.addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
         });
      }

   }

   private void flushQueue() {
      if(this.channel != null && this.channel.isOpen()) {
         synchronized(this.queue) {
            Connection.PacketHolder var2;
            while((var2 = (Connection.PacketHolder)this.queue.poll()) != null) {
               this.sendPacket(var2.packet, var2.listener);
            }

         }
      }
   }

   public void tick() {
      this.flushQueue();
      if(this.packetListener instanceof ServerLoginPacketListenerImpl) {
         ((ServerLoginPacketListenerImpl)this.packetListener).tick();
      }

      if(this.packetListener instanceof ServerGamePacketListenerImpl) {
         ((ServerGamePacketListenerImpl)this.packetListener).tick();
      }

      if(this.channel != null) {
         this.channel.flush();
      }

      if(this.tickCount++ % 20 == 0) {
         this.averageSentPackets = this.averageSentPackets * 0.75F + (float)this.sentPackets * 0.25F;
         this.averageReceivedPackets = this.averageReceivedPackets * 0.75F + (float)this.receivedPackets * 0.25F;
         this.sentPackets = 0;
         this.receivedPackets = 0;
      }

   }

   public SocketAddress getRemoteAddress() {
      return this.address;
   }

   public void disconnect(Component disconnectedReason) {
      if(this.channel.isOpen()) {
         this.channel.close().awaitUninterruptibly();
         this.disconnectedReason = disconnectedReason;
      }

   }

   public boolean isMemoryConnection() {
      return this.channel instanceof LocalChannel || this.channel instanceof LocalServerChannel;
   }

   public static Connection connectToServer(InetAddress inetAddress, int var1, boolean var2) {
      final Connection connection = new Connection(PacketFlow.CLIENTBOUND);
      Class<? extends SocketChannel> var4;
      LazyLoadedValue<? extends EventLoopGroup> var5;
      if(Epoll.isAvailable() && var2) {
         var4 = EpollSocketChannel.class;
         var5 = NETWORK_EPOLL_WORKER_GROUP;
      } else {
         var4 = NioSocketChannel.class;
         var5 = NETWORK_WORKER_GROUP;
      }

      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)var5.get())).handler(new ChannelInitializer() {
         protected void initChannel(Channel channel) throws Exception {
            try {
               channel.config().setOption(ChannelOption.TCP_NODELAY, Boolean.valueOf(true));
            } catch (ChannelException var3) {
               ;
            }

            channel.pipeline().addLast("timeout", new ReadTimeoutHandler(30)).addLast("splitter", new Varint21FrameDecoder()).addLast("decoder", new PacketDecoder(PacketFlow.CLIENTBOUND)).addLast("prepender", new Varint21LengthFieldPrepender()).addLast("encoder", new PacketEncoder(PacketFlow.SERVERBOUND)).addLast("packet_handler", connection);
         }
      })).channel(var4)).connect(inetAddress, var1).syncUninterruptibly();
      return connection;
   }

   public static Connection connectToLocalServer(SocketAddress socketAddress) {
      final Connection connection = new Connection(PacketFlow.CLIENTBOUND);
      ((Bootstrap)((Bootstrap)((Bootstrap)(new Bootstrap()).group((EventLoopGroup)LOCAL_WORKER_GROUP.get())).handler(new ChannelInitializer() {
         protected void initChannel(Channel channel) throws Exception {
            channel.pipeline().addLast("packet_handler", connection);
         }
      })).channel(LocalChannel.class)).connect(socketAddress).syncUninterruptibly();
      return connection;
   }

   public void setEncryptionKey(SecretKey encryptionKey) {
      this.encrypted = true;
      this.channel.pipeline().addBefore("splitter", "decrypt", new CipherDecoder(Crypt.getCipher(2, encryptionKey)));
      this.channel.pipeline().addBefore("prepender", "encrypt", new CipherEncoder(Crypt.getCipher(1, encryptionKey)));
   }

   public boolean isEncrypted() {
      return this.encrypted;
   }

   public boolean isConnected() {
      return this.channel != null && this.channel.isOpen();
   }

   public boolean isConnecting() {
      return this.channel == null;
   }

   public PacketListener getPacketListener() {
      return this.packetListener;
   }

   @Nullable
   public Component getDisconnectedReason() {
      return this.disconnectedReason;
   }

   public void setReadOnly() {
      this.channel.config().setAutoRead(false);
   }

   public void setupCompression(int i) {
      if(i >= 0) {
         if(this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            ((CompressionDecoder)this.channel.pipeline().get("decompress")).setThreshold(i);
         } else {
            this.channel.pipeline().addBefore("decoder", "decompress", new CompressionDecoder(i));
         }

         if(this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            ((CompressionEncoder)this.channel.pipeline().get("compress")).setThreshold(i);
         } else {
            this.channel.pipeline().addBefore("encoder", "compress", new CompressionEncoder(i));
         }
      } else {
         if(this.channel.pipeline().get("decompress") instanceof CompressionDecoder) {
            this.channel.pipeline().remove("decompress");
         }

         if(this.channel.pipeline().get("compress") instanceof CompressionEncoder) {
            this.channel.pipeline().remove("compress");
         }
      }

   }

   public void handleDisconnection() {
      if(this.channel != null && !this.channel.isOpen()) {
         if(this.disconnectionHandled) {
            LOGGER.warn("handleDisconnection() called twice");
         } else {
            this.disconnectionHandled = true;
            if(this.getDisconnectedReason() != null) {
               this.getPacketListener().onDisconnect(this.getDisconnectedReason());
            } else if(this.getPacketListener() != null) {
               this.getPacketListener().onDisconnect(new TranslatableComponent("multiplayer.disconnect.generic", new Object[0]));
            }
         }

      }
   }

   public float getAverageReceivedPackets() {
      return this.averageReceivedPackets;
   }

   public float getAverageSentPackets() {
      return this.averageSentPackets;
   }

   // $FF: synthetic method
   protected void channelRead0(ChannelHandlerContext var1, Object var2) throws Exception {
      this.channelRead0(var1, (Packet)var2);
   }

   static class PacketHolder {
      private final Packet packet;
      @Nullable
      private final GenericFutureListener listener;

      public PacketHolder(Packet packet, @Nullable GenericFutureListener listener) {
         this.packet = packet;
         this.listener = listener;
      }
   }
}
