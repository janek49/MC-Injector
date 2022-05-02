package net.minecraft.client.server;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.authlib.GameProfile;
import java.net.SocketAddress;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

@ClientJarOnly
public class IntegratedPlayerList extends PlayerList {
   private CompoundTag playerData;

   public IntegratedPlayerList(IntegratedServer integratedServer) {
      super(integratedServer, 8);
      this.setViewDistance(10);
   }

   protected void save(ServerPlayer serverPlayer) {
      if(serverPlayer.getName().getString().equals(this.getServer().getSingleplayerName())) {
         this.playerData = serverPlayer.saveWithoutId(new CompoundTag());
      }

      super.save(serverPlayer);
   }

   public Component canPlayerLogin(SocketAddress socketAddress, GameProfile gameProfile) {
      return (Component)(gameProfile.getName().equalsIgnoreCase(this.getServer().getSingleplayerName()) && this.getPlayerByName(gameProfile.getName()) != null?new TranslatableComponent("multiplayer.disconnect.name_taken", new Object[0]):super.canPlayerLogin(socketAddress, gameProfile));
   }

   public IntegratedServer getServer() {
      return (IntegratedServer)super.getServer();
   }

   public CompoundTag getSingleplayerData() {
      return this.playerData;
   }

   // $FF: synthetic method
   public MinecraftServer getServer() {
      return this.getServer();
   }
}
