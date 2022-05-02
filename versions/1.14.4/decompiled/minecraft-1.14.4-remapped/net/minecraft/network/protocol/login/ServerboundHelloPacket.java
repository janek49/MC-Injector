package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ServerLoginPacketListener;

public class ServerboundHelloPacket implements Packet {
   private GameProfile gameProfile;

   public ServerboundHelloPacket() {
   }

   public ServerboundHelloPacket(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      this.gameProfile = new GameProfile((UUID)null, friendlyByteBuf.readUtf(16));
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      friendlyByteBuf.writeUtf(this.gameProfile.getName());
   }

   public void handle(ServerLoginPacketListener serverLoginPacketListener) {
      serverLoginPacketListener.handleHello(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
