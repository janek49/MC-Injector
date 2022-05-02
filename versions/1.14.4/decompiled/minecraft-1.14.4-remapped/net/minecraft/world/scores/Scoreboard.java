package net.minecraft.world.scores;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Scoreboard {
   private final Map objectivesByName = Maps.newHashMap();
   private final Map objectivesByCriteria = Maps.newHashMap();
   private final Map playerScores = Maps.newHashMap();
   private final Objective[] displayObjectives = new Objective[19];
   private final Map teamsByName = Maps.newHashMap();
   private final Map teamsByPlayer = Maps.newHashMap();
   private static String[] displaySlotNames;

   public boolean hasObjective(String string) {
      return this.objectivesByName.containsKey(string);
   }

   public Objective getOrCreateObjective(String string) {
      return (Objective)this.objectivesByName.get(string);
   }

   @Nullable
   public Objective getObjective(@Nullable String string) {
      return (Objective)this.objectivesByName.get(string);
   }

   public Objective addObjective(String string, ObjectiveCriteria objectiveCriteria, Component component, ObjectiveCriteria.RenderType objectiveCriteria$RenderType) {
      if(string.length() > 16) {
         throw new IllegalArgumentException("The objective name \'" + string + "\' is too long!");
      } else if(this.objectivesByName.containsKey(string)) {
         throw new IllegalArgumentException("An objective with the name \'" + string + "\' already exists!");
      } else {
         Objective objective = new Objective(this, string, objectiveCriteria, component, objectiveCriteria$RenderType);
         ((List)this.objectivesByCriteria.computeIfAbsent(objectiveCriteria, (objectiveCriteria) -> {
            return Lists.newArrayList();
         })).add(objective);
         this.objectivesByName.put(string, objective);
         this.onObjectiveAdded(objective);
         return objective;
      }
   }

   public final void forAllObjectives(ObjectiveCriteria objectiveCriteria, String string, Consumer consumer) {
      ((List)this.objectivesByCriteria.getOrDefault(objectiveCriteria, Collections.emptyList())).forEach((objective) -> {
         consumer.accept(this.getOrCreatePlayerScore(string, objective));
      });
   }

   public boolean hasPlayerScore(String string, Objective objective) {
      Map<Objective, Score> var3 = (Map)this.playerScores.get(string);
      if(var3 == null) {
         return false;
      } else {
         Score var4 = (Score)var3.get(objective);
         return var4 != null;
      }
   }

   public Score getOrCreatePlayerScore(String string, Objective objective) {
      if(string.length() > 40) {
         throw new IllegalArgumentException("The player name \'" + string + "\' is too long!");
      } else {
         Map<Objective, Score> var3 = (Map)this.playerScores.computeIfAbsent(string, (string) -> {
            return Maps.newHashMap();
         });
         return (Score)var3.computeIfAbsent(objective, (objective) -> {
            Score score = new Score(this, objective, string);
            score.setScore(0);
            return score;
         });
      }
   }

   public Collection getPlayerScores(Objective objective) {
      List<Score> var2 = Lists.newArrayList();

      for(Map<Objective, Score> var4 : this.playerScores.values()) {
         Score var5 = (Score)var4.get(objective);
         if(var5 != null) {
            var2.add(var5);
         }
      }

      Collections.sort(var2, Score.SCORE_COMPARATOR);
      return var2;
   }

   public Collection getObjectives() {
      return this.objectivesByName.values();
   }

   public Collection getObjectiveNames() {
      return this.objectivesByName.keySet();
   }

   public Collection getTrackedPlayers() {
      return Lists.newArrayList(this.playerScores.keySet());
   }

   public void resetPlayerScore(String string, @Nullable Objective objective) {
      if(objective == null) {
         Map<Objective, Score> var3 = (Map)this.playerScores.remove(string);
         if(var3 != null) {
            this.onPlayerRemoved(string);
         }
      } else {
         Map<Objective, Score> var3 = (Map)this.playerScores.get(string);
         if(var3 != null) {
            Score var4 = (Score)var3.remove(objective);
            if(var3.size() < 1) {
               Map<Objective, Score> var5 = (Map)this.playerScores.remove(string);
               if(var5 != null) {
                  this.onPlayerRemoved(string);
               }
            } else if(var4 != null) {
               this.onPlayerScoreRemoved(string, objective);
            }
         }
      }

   }

   public Map getPlayerScores(String string) {
      Map<Objective, Score> map = (Map)this.playerScores.get(string);
      if(map == null) {
         map = Maps.newHashMap();
      }

      return map;
   }

   public void removeObjective(Objective objective) {
      this.objectivesByName.remove(objective.getName());

      for(int var2 = 0; var2 < 19; ++var2) {
         if(this.getDisplayObjective(var2) == objective) {
            this.setDisplayObjective(var2, (Objective)null);
         }
      }

      List<Objective> var2 = (List)this.objectivesByCriteria.get(objective.getCriteria());
      if(var2 != null) {
         var2.remove(objective);
      }

      for(Map<Objective, Score> var4 : this.playerScores.values()) {
         var4.remove(objective);
      }

      this.onObjectiveRemoved(objective);
   }

   public void setDisplayObjective(int var1, @Nullable Objective objective) {
      this.displayObjectives[var1] = objective;
   }

   @Nullable
   public Objective getDisplayObjective(int i) {
      return this.displayObjectives[i];
   }

   public PlayerTeam getPlayerTeam(String string) {
      return (PlayerTeam)this.teamsByName.get(string);
   }

   public PlayerTeam addPlayerTeam(String string) {
      if(string.length() > 16) {
         throw new IllegalArgumentException("The team name \'" + string + "\' is too long!");
      } else {
         PlayerTeam playerTeam = this.getPlayerTeam(string);
         if(playerTeam != null) {
            throw new IllegalArgumentException("A team with the name \'" + string + "\' already exists!");
         } else {
            playerTeam = new PlayerTeam(this, string);
            this.teamsByName.put(string, playerTeam);
            this.onTeamAdded(playerTeam);
            return playerTeam;
         }
      }
   }

   public void removePlayerTeam(PlayerTeam playerTeam) {
      this.teamsByName.remove(playerTeam.getName());

      for(String var3 : playerTeam.getPlayers()) {
         this.teamsByPlayer.remove(var3);
      }

      this.onTeamRemoved(playerTeam);
   }

   public boolean addPlayerToTeam(String string, PlayerTeam playerTeam) {
      if(string.length() > 40) {
         throw new IllegalArgumentException("The player name \'" + string + "\' is too long!");
      } else {
         if(this.getPlayersTeam(string) != null) {
            this.removePlayerFromTeam(string);
         }

         this.teamsByPlayer.put(string, playerTeam);
         return playerTeam.getPlayers().add(string);
      }
   }

   public boolean removePlayerFromTeam(String string) {
      PlayerTeam var2 = this.getPlayersTeam(string);
      if(var2 != null) {
         this.removePlayerFromTeam(string, var2);
         return true;
      } else {
         return false;
      }
   }

   public void removePlayerFromTeam(String string, PlayerTeam playerTeam) {
      if(this.getPlayersTeam(string) != playerTeam) {
         throw new IllegalStateException("Player is either on another team or not on any team. Cannot remove from team \'" + playerTeam.getName() + "\'.");
      } else {
         this.teamsByPlayer.remove(string);
         playerTeam.getPlayers().remove(string);
      }
   }

   public Collection getTeamNames() {
      return this.teamsByName.keySet();
   }

   public Collection getPlayerTeams() {
      return this.teamsByName.values();
   }

   @Nullable
   public PlayerTeam getPlayersTeam(String string) {
      return (PlayerTeam)this.teamsByPlayer.get(string);
   }

   public void onObjectiveAdded(Objective objective) {
   }

   public void onObjectiveChanged(Objective objective) {
   }

   public void onObjectiveRemoved(Objective objective) {
   }

   public void onScoreChanged(Score score) {
   }

   public void onPlayerRemoved(String string) {
   }

   public void onPlayerScoreRemoved(String string, Objective objective) {
   }

   public void onTeamAdded(PlayerTeam playerTeam) {
   }

   public void onTeamChanged(PlayerTeam playerTeam) {
   }

   public void onTeamRemoved(PlayerTeam playerTeam) {
   }

   public static String getDisplaySlotName(int i) {
      switch(i) {
      case 0:
         return "list";
      case 1:
         return "sidebar";
      case 2:
         return "belowName";
      default:
         if(i >= 3 && i <= 18) {
            ChatFormatting var1 = ChatFormatting.getById(i - 3);
            if(var1 != null && var1 != ChatFormatting.RESET) {
               return "sidebar.team." + var1.getName();
            }
         }

         return null;
      }
   }

   public static int getDisplaySlotByName(String string) {
      if("list".equalsIgnoreCase(string)) {
         return 0;
      } else if("sidebar".equalsIgnoreCase(string)) {
         return 1;
      } else if("belowName".equalsIgnoreCase(string)) {
         return 2;
      } else {
         if(string.startsWith("sidebar.team.")) {
            String string = string.substring("sidebar.team.".length());
            ChatFormatting var2 = ChatFormatting.getByName(string);
            if(var2 != null && var2.getId() >= 0) {
               return var2.getId() + 3;
            }
         }

         return -1;
      }
   }

   public static String[] getDisplaySlotNames() {
      if(displaySlotNames == null) {
         displaySlotNames = new String[19];

         for(int var0 = 0; var0 < 19; ++var0) {
            displaySlotNames[var0] = getDisplaySlotName(var0);
         }
      }

      return displaySlotNames;
   }

   public void entityRemoved(Entity entity) {
      if(entity != null && !(entity instanceof Player) && !entity.isAlive()) {
         String var2 = entity.getStringUUID();
         this.resetPlayerScore(var2, (Objective)null);
         this.removePlayerFromTeam(var2);
      }
   }

   protected ListTag savePlayerScores() {
      ListTag listTag = new ListTag();
      this.playerScores.values().stream().map(Map::values).forEach((collection) -> {
         collection.stream().filter((score) -> {
            return score.getObjective() != null;
         }).forEach((score) -> {
            CompoundTag var2 = new CompoundTag();
            var2.putString("Name", score.getOwner());
            var2.putString("Objective", score.getObjective().getName());
            var2.putInt("Score", score.getScore());
            var2.putBoolean("Locked", score.isLocked());
            listTag.add(var2);
         });
      });
      return listTag;
   }

   protected void loadPlayerScores(ListTag listTag) {
      for(int var2 = 0; var2 < listTag.size(); ++var2) {
         CompoundTag var3 = listTag.getCompound(var2);
         Objective var4 = this.getOrCreateObjective(var3.getString("Objective"));
         String var5 = var3.getString("Name");
         if(var5.length() > 40) {
            var5 = var5.substring(0, 40);
         }

         Score var6 = this.getOrCreatePlayerScore(var5, var4);
         var6.setScore(var3.getInt("Score"));
         if(var3.contains("Locked")) {
            var6.setLocked(var3.getBoolean("Locked"));
         }
      }

   }
}
