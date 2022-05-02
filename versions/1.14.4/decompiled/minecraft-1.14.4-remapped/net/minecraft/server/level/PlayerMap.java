package net.minecraft.server.level;

import it.unimi.dsi.fastutil.objects.Object2BooleanMap;
import it.unimi.dsi.fastutil.objects.Object2BooleanOpenHashMap;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerPlayer;

public final class PlayerMap {
   private final Object2BooleanMap players = new Object2BooleanOpenHashMap();

   public Stream getPlayers(long l) {
      return this.players.keySet().stream();
   }

   public void addPlayer(long var1, ServerPlayer serverPlayer, boolean var4) {
      this.players.put(serverPlayer, var4);
   }

   public void removePlayer(long var1, ServerPlayer serverPlayer) {
      this.players.removeBoolean(serverPlayer);
   }

   public void ignorePlayer(ServerPlayer serverPlayer) {
      this.players.replace(serverPlayer, true);
   }

   public void unIgnorePlayer(ServerPlayer serverPlayer) {
      this.players.replace(serverPlayer, false);
   }

   public boolean ignoredOrUnknown(ServerPlayer serverPlayer) {
      return this.players.getOrDefault(serverPlayer, true);
   }

   public boolean ignored(ServerPlayer serverPlayer) {
      return this.players.getBoolean(serverPlayer);
   }

   public void updatePlayer(long var1, long var3, ServerPlayer serverPlayer) {
   }
}
