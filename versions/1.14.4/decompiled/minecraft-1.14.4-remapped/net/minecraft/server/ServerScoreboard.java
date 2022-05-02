package net.minecraft.server;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetDisplayObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetObjectivePacket;
import net.minecraft.network.protocol.game.ClientboundSetPlayerTeamPacket;
import net.minecraft.network.protocol.game.ClientboundSetScorePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;

public class ServerScoreboard extends Scoreboard {
   private final MinecraftServer server;
   private final Set trackedObjectives = Sets.newHashSet();
   private Runnable[] dirtyListeners = new Runnable[0];

   public ServerScoreboard(MinecraftServer server) {
      this.server = server;
   }

   public void onScoreChanged(Score score) {
      super.onScoreChanged(score);
      if(this.trackedObjectives.contains(score.getObjective())) {
         this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, score.getObjective().getName(), score.getOwner(), score.getScore()));
      }

      this.setDirty();
   }

   public void onPlayerRemoved(String string) {
      super.onPlayerRemoved(string);
      this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, (String)null, string, 0));
      this.setDirty();
   }

   public void onPlayerScoreRemoved(String string, Objective objective) {
      super.onPlayerScoreRemoved(string, objective);
      if(this.trackedObjectives.contains(objective)) {
         this.server.getPlayerList().broadcastAll(new ClientboundSetScorePacket(ServerScoreboard.Method.REMOVE, objective.getName(), string, 0));
      }

      this.setDirty();
   }

   public void setDisplayObjective(int var1, @Nullable Objective objective) {
      Objective objective = this.getDisplayObjective(var1);
      super.setDisplayObjective(var1, objective);
      if(objective != objective && objective != null) {
         if(this.getObjectiveDisplaySlotCount(objective) > 0) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(var1, objective));
         } else {
            this.stopTrackingObjective(objective);
         }
      }

      if(objective != null) {
         if(this.trackedObjectives.contains(objective)) {
            this.server.getPlayerList().broadcastAll(new ClientboundSetDisplayObjectivePacket(var1, objective));
         } else {
            this.startTrackingObjective(objective);
         }
      }

      this.setDirty();
   }

   public boolean addPlayerToTeam(String string, PlayerTeam playerTeam) {
      if(super.addPlayerToTeam(string, playerTeam)) {
         this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, Arrays.asList(new String[]{string}), 3));
         this.setDirty();
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String string, PlayerTeam playerTeam) {
      super.removePlayerFromTeam(string, playerTeam);
      this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, Arrays.asList(new String[]{string}), 4));
      this.setDirty();
   }

   public void onObjectiveAdded(Objective objective) {
      super.onObjectiveAdded(objective);
      this.setDirty();
   }

   public void onObjectiveChanged(Objective objective) {
      super.onObjectiveChanged(objective);
      if(this.trackedObjectives.contains(objective)) {
         this.server.getPlayerList().broadcastAll(new ClientboundSetObjectivePacket(objective, 2));
      }

      this.setDirty();
   }

   public void onObjectiveRemoved(Objective objective) {
      super.onObjectiveRemoved(objective);
      if(this.trackedObjectives.contains(objective)) {
         this.stopTrackingObjective(objective);
      }

      this.setDirty();
   }

   public void onTeamAdded(PlayerTeam playerTeam) {
      super.onTeamAdded(playerTeam);
      this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, 0));
      this.setDirty();
   }

   public void onTeamChanged(PlayerTeam playerTeam) {
      super.onTeamChanged(playerTeam);
      this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, 2));
      this.setDirty();
   }

   public void onTeamRemoved(PlayerTeam playerTeam) {
      super.onTeamRemoved(playerTeam);
      this.server.getPlayerList().broadcastAll(new ClientboundSetPlayerTeamPacket(playerTeam, 1));
      this.setDirty();
   }

   public void addDirtyListener(Runnable runnable) {
      this.dirtyListeners = (Runnable[])Arrays.copyOf(this.dirtyListeners, this.dirtyListeners.length + 1);
      this.dirtyListeners[this.dirtyListeners.length - 1] = runnable;
   }

   protected void setDirty() {
      for(Runnable var4 : this.dirtyListeners) {
         var4.run();
      }

   }

   public List getStartTrackingPackets(Objective objective) {
      List<Packet<?>> list = Lists.newArrayList();
      list.add(new ClientboundSetObjectivePacket(objective, 0));

      for(int var3 = 0; var3 < 19; ++var3) {
         if(this.getDisplayObjective(var3) == objective) {
            list.add(new ClientboundSetDisplayObjectivePacket(var3, objective));
         }
      }

      for(Score var4 : this.getPlayerScores(objective)) {
         list.add(new ClientboundSetScorePacket(ServerScoreboard.Method.CHANGE, var4.getObjective().getName(), var4.getOwner(), var4.getScore()));
      }

      return list;
   }

   public void startTrackingObjective(Objective objective) {
      List<Packet<?>> var2 = this.getStartTrackingPackets(objective);

      for(ServerPlayer var4 : this.server.getPlayerList().getPlayers()) {
         for(Packet<?> var6 : var2) {
            var4.connection.send(var6);
         }
      }

      this.trackedObjectives.add(objective);
   }

   public List getStopTrackingPackets(Objective objective) {
      List<Packet<?>> list = Lists.newArrayList();
      list.add(new ClientboundSetObjectivePacket(objective, 1));

      for(int var3 = 0; var3 < 19; ++var3) {
         if(this.getDisplayObjective(var3) == objective) {
            list.add(new ClientboundSetDisplayObjectivePacket(var3, objective));
         }
      }

      return list;
   }

   public void stopTrackingObjective(Objective objective) {
      List<Packet<?>> var2 = this.getStopTrackingPackets(objective);

      for(ServerPlayer var4 : this.server.getPlayerList().getPlayers()) {
         for(Packet<?> var6 : var2) {
            var4.connection.send(var6);
         }
      }

      this.trackedObjectives.remove(objective);
   }

   public int getObjectiveDisplaySlotCount(Objective objective) {
      int var2 = 0;

      for(int var3 = 0; var3 < 19; ++var3) {
         if(this.getDisplayObjective(var3) == objective) {
            ++var2;
         }
      }

      return var2;
   }

   public static enum Method {
      CHANGE,
      REMOVE;
   }
}
