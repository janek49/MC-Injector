package net.minecraft.server.network;

import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerLoginPacketListenerImpl;

public class MemoryServerHandshakePacketListenerImpl implements ServerHandshakePacketListener {
   private final MinecraftServer server;
   private final Connection connection;

   public MemoryServerHandshakePacketListenerImpl(MinecraftServer server, Connection connection) {
      this.server = server;
      this.connection = connection;
   }

   public void handleIntention(ClientIntentionPacket clientIntentionPacket) {
      this.connection.setProtocol(clientIntentionPacket.getIntention());
      this.connection.setListener(new ServerLoginPacketListenerImpl(this.server, this.connection));
   }

   public void onDisconnect(Component component) {
   }

   public Connection getConnection() {
      return this.connection;
   }
}
