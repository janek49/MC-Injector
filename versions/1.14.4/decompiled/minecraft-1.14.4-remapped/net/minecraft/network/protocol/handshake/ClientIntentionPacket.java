package net.minecraft.network.protocol.handshake;

import java.io.IOException;
import net.minecraft.SharedConstants;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.handshake.ServerHandshakePacketListener;

public class ClientIntentionPacket implements Packet {
   private int protocolVersion;
   private String hostName;
   private int port;
   private ConnectionProtocol intention;

   public ClientIntentionPacket() {
   }

   public ClientIntentionPacket(String hostName, int port, ConnectionProtocol intention) {
      this.protocolVersion = SharedConstants.getCurrentVersion().getProtocolVersion();
      this.hostName = hostName;
      this.port = port;
      this.intention = intention;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.protocolVersion = friendlyByteBuf.readVarInt();
      this.hostName = friendlyByteBuf.readUtf(255);
      this.port = friendlyByteBuf.readUnsignedShort();
      this.intention = ConnectionProtocol.getById(friendlyByteBuf.readVarInt());
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeVarInt(this.protocolVersion);
      friendlyByteBuf.writeUtf(this.hostName);
      friendlyByteBuf.writeShort(this.port);
      friendlyByteBuf.writeVarInt(this.intention.getId());
   }

   public void handle(ServerHandshakePacketListener serverHandshakePacketListener) {
      serverHandshakePacketListener.handleIntention(this);
   }

   public ConnectionProtocol getIntention() {
      return this.intention;
   }

   public int getProtocolVersion() {
      return this.protocolVersion;
   }
}
