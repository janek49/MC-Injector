package net.minecraft.network.protocol.login;

import com.mojang.authlib.GameProfile;
import java.io.IOException;
import java.util.UUID;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.login.ClientLoginPacketListener;

public class ClientboundGameProfilePacket implements Packet {
   private GameProfile gameProfile;

   public ClientboundGameProfilePacket() {
   }

   public ClientboundGameProfilePacket(GameProfile gameProfile) {
      this.gameProfile = gameProfile;
   }

   public void read(FriendlyByteBuf friendlyByteBuf) throws IOException {
      String var2 = friendlyByteBuf.readUtf(36);
      String var3 = friendlyByteBuf.readUtf(16);
      UUID var4 = UUID.fromString(var2);
      this.gameProfile = new GameProfile(var4, var3);
   }

   public void write(FriendlyByteBuf friendlyByteBuf) throws IOException {
      UUID var2 = this.gameProfile.getId();
      friendlyByteBuf.writeUtf(var2 == null?"":var2.toString());
      friendlyByteBuf.writeUtf(this.gameProfile.getName());
   }

   public void handle(ClientLoginPacketListener clientLoginPacketListener) {
      clientLoginPacketListener.handleGameProfile(this);
   }

   public GameProfile getGameProfile() {
      return this.gameProfile;
   }
}
