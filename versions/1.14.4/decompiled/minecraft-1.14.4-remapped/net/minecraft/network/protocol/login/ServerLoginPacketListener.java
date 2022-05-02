package net.minecraft.network.protocol.login;

import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.login.ServerboundCustomQueryPacket;
import net.minecraft.network.protocol.login.ServerboundHelloPacket;
import net.minecraft.network.protocol.login.ServerboundKeyPacket;

public interface ServerLoginPacketListener extends PacketListener {
   void handleHello(ServerboundHelloPacket var1);

   void handleKey(ServerboundKeyPacket var1);

   void handleCustomQueryPacket(ServerboundCustomQueryPacket var1);
}
