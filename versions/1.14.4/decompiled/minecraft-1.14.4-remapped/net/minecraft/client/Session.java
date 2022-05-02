package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.bridge.game.GameSession;
import java.util.UUID;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.player.LocalPlayer;

@ClientJarOnly
public class Session implements GameSession {
   private final int players;
   private final boolean isRemoteServer;
   private final String difficulty;
   private final String gameMode;
   private final UUID id;

   public Session(MultiPlayerLevel multiPlayerLevel, LocalPlayer localPlayer, ClientPacketListener clientPacketListener) {
      this.players = clientPacketListener.getOnlinePlayers().size();
      this.isRemoteServer = !clientPacketListener.getConnection().isMemoryConnection();
      this.difficulty = multiPlayerLevel.getDifficulty().getKey();
      PlayerInfo var4 = clientPacketListener.getPlayerInfo(localPlayer.getUUID());
      if(var4 != null) {
         this.gameMode = var4.getGameMode().getName();
      } else {
         this.gameMode = "unknown";
      }

      this.id = clientPacketListener.getId();
   }

   public int getPlayerCount() {
      return this.players;
   }

   public boolean isRemoteServer() {
      return this.isRemoteServer;
   }

   public String getDifficulty() {
      return this.difficulty;
   }

   public String getGameMode() {
      return this.gameMode;
   }

   public UUID getSessionId() {
      return this.id;
   }
}
