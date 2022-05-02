package net.minecraft.network.protocol.handshake;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.handshake.ClientIntentionPacket;

public interface ServerHandshakePacketListener extends PacketListener {
   void handleIntention(ClientIntentionPacket var1);
}
