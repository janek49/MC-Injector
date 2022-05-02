package net.minecraft.world.scores;

import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ScoreboardSaveData extends SavedData {
   private static final Logger LOGGER = LogManager.getLogger();
   private Scoreboard scoreboard;
   private CompoundTag delayLoad;

   public ScoreboardSaveData() {
      super("scoreboard");
   }

   public void setScoreboard(Scoreboard scoreboard) {
      this.scoreboard = scoreboard;
      if(this.delayLoad != null) {
         this.load(this.delayLoad);
      }

   }

   public void load(CompoundTag delayLoad) {
      if(this.scoreboard == null) {
         this.delayLoad = delayLoad;
      } else {
         this.loadObjectives(delayLoad.getList("Objectives", 10));
         this.scoreboard.loadPlayerScores(delayLoad.getList("PlayerScores", 10));
         if(delayLoad.contains("DisplaySlots", 10)) {
            this.loadDisplaySlots(delayLoad.getCompound("DisplaySlots"));
         }

         if(delayLoad.contains("Teams", 9)) {
            this.loadTeams(delayLoad.getList("Teams", 10));
         }

      }
   }

   protected void loadTeams(ListTag listTag) {
      for(int var2 = 0; var2 < listTag.size(); ++var2) {
         CompoundTag var3 = listTag.getCompound(var2);
         String var4 = var3.getString("Name");
         if(var4.length() > 16) {
            var4 = var4.substring(0, 16);
         }

         PlayerTeam var5 = this.scoreboard.addPlayerTeam(var4);
         Component var6 = Component.Serializer.fromJson(var3.getString("DisplayName"));
         if(var6 != null) {
            var5.setDisplayName(var6);
         }

         if(var3.contains("TeamColor", 8)) {
            var5.setColor(ChatFormatting.getByName(var3.getString("TeamColor")));
         }

         if(var3.contains("AllowFriendlyFire", 99)) {
            var5.setAllowFriendlyFire(var3.getBoolean("AllowFriendlyFire"));
         }

         if(var3.contains("SeeFriendlyInvisibles", 99)) {
            var5.setSeeFriendlyInvisibles(var3.getBoolean("SeeFriendlyInvisibles"));
         }

         if(var3.contains("MemberNamePrefix", 8)) {
            Component var7 = Component.Serializer.fromJson(var3.getString("MemberNamePrefix"));
            if(var7 != null) {
               var5.setPlayerPrefix(var7);
            }
         }

         if(var3.contains("MemberNameSuffix", 8)) {
            Component var7 = Component.Serializer.fromJson(var3.getString("MemberNameSuffix"));
            if(var7 != null) {
               var5.setPlayerSuffix(var7);
            }
         }

         if(var3.contains("NameTagVisibility", 8)) {
            Team.Visibility var7 = Team.Visibility.byName(var3.getString("NameTagVisibility"));
            if(var7 != null) {
               var5.setNameTagVisibility(var7);
            }
         }

         if(var3.contains("DeathMessageVisibility", 8)) {
            Team.Visibility var7 = Team.Visibility.byName(var3.getString("DeathMessageVisibility"));
            if(var7 != null) {
               var5.setDeathMessageVisibility(var7);
            }
         }

         if(var3.contains("CollisionRule", 8)) {
            Team.CollisionRule var7 = Team.CollisionRule.byName(var3.getString("CollisionRule"));
            if(var7 != null) {
               var5.setCollisionRule(var7);
            }
         }

         this.loadTeamPlayers(var5, var3.getList("Players", 8));
      }

   }

   protected void loadTeamPlayers(PlayerTeam playerTeam, ListTag listTag) {
      for(int var3 = 0; var3 < listTag.size(); ++var3) {
         this.scoreboard.addPlayerToTeam(listTag.getString(var3), playerTeam);
      }

   }

   protected void loadDisplaySlots(CompoundTag compoundTag) {
      for(int var2 = 0; var2 < 19; ++var2) {
         if(compoundTag.contains("slot_" + var2, 8)) {
            String var3 = compoundTag.getString("slot_" + var2);
            Objective var4 = this.scoreboard.getObjective(var3);
            this.scoreboard.setDisplayObjective(var2, var4);
         }
      }

   }

   protected void loadObjectives(ListTag listTag) {
      for(int var2 = 0; var2 < listTag.size(); ++var2) {
         CompoundTag var3 = listTag.getCompound(var2);
         ObjectiveCriteria.byName(var3.getString("CriteriaName")).ifPresent((objectiveCriteria) -> {
            String var3 = var3.getString("Name");
            if(var3.length() > 16) {
               var3 = var3.substring(0, 16);
            }

            Component var4 = Component.Serializer.fromJson(var3.getString("DisplayName"));
            ObjectiveCriteria.RenderType var5 = ObjectiveCriteria.RenderType.byId(var3.getString("RenderType"));
            this.scoreboard.addObjective(var3, objectiveCriteria, var4, var5);
         });
      }

   }

   public CompoundTag save(CompoundTag compoundTag) {
      if(this.scoreboard == null) {
         LOGGER.warn("Tried to save scoreboard without having a scoreboard...");
         return compoundTag;
      } else {
         compoundTag.put("Objectives", this.saveObjectives());
         compoundTag.put("PlayerScores", this.scoreboard.savePlayerScores());
         compoundTag.put("Teams", this.saveTeams());
         this.saveDisplaySlots(compoundTag);
         return compoundTag;
      }
   }

   protected ListTag saveTeams() {
      ListTag listTag = new ListTag();

      for(PlayerTeam var4 : this.scoreboard.getPlayerTeams()) {
         CompoundTag var5 = new CompoundTag();
         var5.putString("Name", var4.getName());
         var5.putString("DisplayName", Component.Serializer.toJson(var4.getDisplayName()));
         if(var4.getColor().getId() >= 0) {
            var5.putString("TeamColor", var4.getColor().getName());
         }

         var5.putBoolean("AllowFriendlyFire", var4.isAllowFriendlyFire());
         var5.putBoolean("SeeFriendlyInvisibles", var4.canSeeFriendlyInvisibles());
         var5.putString("MemberNamePrefix", Component.Serializer.toJson(var4.getPlayerPrefix()));
         var5.putString("MemberNameSuffix", Component.Serializer.toJson(var4.getPlayerSuffix()));
         var5.putString("NameTagVisibility", var4.getNameTagVisibility().name);
         var5.putString("DeathMessageVisibility", var4.getDeathMessageVisibility().name);
         var5.putString("CollisionRule", var4.getCollisionRule().name);
         ListTag var6 = new ListTag();

         for(String var8 : var4.getPlayers()) {
            var6.add(new StringTag(var8));
         }

         var5.put("Players", var6);
         listTag.add(var5);
      }

      return listTag;
   }

   protected void saveDisplaySlots(CompoundTag compoundTag) {
      CompoundTag compoundTag = new CompoundTag();
      boolean var3 = false;

      for(int var4 = 0; var4 < 19; ++var4) {
         Objective var5 = this.scoreboard.getDisplayObjective(var4);
         if(var5 != null) {
            compoundTag.putString("slot_" + var4, var5.getName());
            var3 = true;
         }
      }

      if(var3) {
         compoundTag.put("DisplaySlots", compoundTag);
      }

   }

   protected ListTag saveObjectives() {
      ListTag listTag = new ListTag();

      for(Objective var4 : this.scoreboard.getObjectives()) {
         if(var4.getCriteria() != null) {
            CompoundTag var5 = new CompoundTag();
            var5.putString("Name", var4.getName());
            var5.putString("CriteriaName", var4.getCriteria().getName());
            var5.putString("DisplayName", Component.Serializer.toJson(var4.getDisplayName()));
            var5.putString("RenderType", var4.getRenderType().getId());
            listTag.add(var5);
         }
      }

      return listTag;
   }
}
