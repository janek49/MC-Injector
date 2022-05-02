package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.login.ClientboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ClientboundGameProfilePacket;
import net.minecraft.network.protocol.login.ClientboundHelloPacket;
import net.minecraft.network.protocol.login.ClientboundLoginCompressionPacket;
import net.minecraft.network.protocol.login.ClientboundLoginDisconnectPacket;

public interface ClientLoginPacketListener extends PacketListener {
   void handleHello(ClientboundHelloPacket var1);

   void handleGameProfile(ClientboundGameProfilePacket var1);

   void handleDisconnect(ClientboundLoginDisconnectPacket var1);

   void handleCompression(ClientboundLoginCompressionPacket var1);

   void handleCustomQuery(ClientboundCustomQueryPacket var1);
}
