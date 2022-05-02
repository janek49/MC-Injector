package net.minecraft.world.scores;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class PlayerTeam extends Team {
   private final Scoreboard scoreboard;
   private final String name;
   private final Set players = Sets.newHashSet();
   private Component displayName;
   private Component playerPrefix = new TextComponent("");
   private Component playerSuffix = new TextComponent("");
   private boolean allowFriendlyFire = true;
   private boolean seeFriendlyInvisibles = true;
   private Team.Visibility nameTagVisibility = Team.Visibility.ALWAYS;
   private Team.Visibility deathMessageVisibility = Team.Visibility.ALWAYS;
   private ChatFormatting color = ChatFormatting.RESET;
   private Team.CollisionRule collisionRule = Team.CollisionRule.ALWAYS;

   public PlayerTeam(Scoreboard scoreboard, String name) {
      this.scoreboard = scoreboard;
      this.name = name;
      this.displayName = new TextComponent(name);
   }

   public String getName() {
      return this.name;
   }

   public Component getDisplayName() {
      return this.displayName;
   }

   public Component getFormattedDisplayName() {
      Component component = ComponentUtils.wrapInSquareBrackets(this.displayName.deepCopy().withStyle((style) -> {
         style.setInsertion(this.name).setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new TextComponent(this.name)));
      }));
      ChatFormatting var2 = this.getColor();
      if(var2 != ChatFormatting.RESET) {
         component.withStyle(var2);
      }

      return component;
   }

   public void setDisplayName(Component displayName) {
      if(displayName == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = displayName;
         this.scoreboard.onTeamChanged(this);
      }
   }

   public void setPlayerPrefix(@Nullable Component playerPrefix) {
      this.playerPrefix = (Component)(playerPrefix == null?new TextComponent(""):playerPrefix.deepCopy());
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerPrefix() {
      return this.playerPrefix;
   }

   public void setPlayerSuffix(@Nullable Component playerSuffix) {
      this.playerSuffix = (Component)(playerSuffix == null?new TextComponent(""):playerSuffix.deepCopy());
      this.scoreboard.onTeamChanged(this);
   }

   public Component getPlayerSuffix() {
      return this.playerSuffix;
   }

   public Collection getPlayers() {
      return this.players;
   }

   public Component getFormattedName(Component component) {
      Component var2 = (new TextComponent("")).append(this.playerPrefix).append(component).append(this.playerSuffix);
      ChatFormatting var3 = this.getColor();
      if(var3 != ChatFormatting.RESET) {
         var2.withStyle(var3);
      }

      return var2;
   }

   public static Component formatNameForTeam(@Nullable Team team, Component var1) {
      return team == null?var1.deepCopy():team.getFormattedName(var1);
   }

   public boolean isAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean allowFriendlyFire) {
      this.allowFriendlyFire = allowFriendlyFire;
      this.scoreboard.onTeamChanged(this);
   }

   public boolean canSeeFriendlyInvisibles() {
      return this.seeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisibles(boolean seeFriendlyInvisibles) {
      this.seeFriendlyInvisibles = seeFriendlyInvisibles;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.Visibility getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public Team.Visibility getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(Team.Visibility nameTagVisibility) {
      this.nameTagVisibility = nameTagVisibility;
      this.scoreboard.onTeamChanged(this);
   }

   public void setDeathMessageVisibility(Team.Visibility deathMessageVisibility) {
      this.deathMessageVisibility = deathMessageVisibility;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   public void setCollisionRule(Team.CollisionRule collisionRule) {
      this.collisionRule = collisionRule;
      this.scoreboard.onTeamChanged(this);
   }

   public int packOptions() {
      int var1 = 0;
      if(this.isAllowFriendlyFire()) {
         var1 |= 1;
      }

      if(this.canSeeFriendlyInvisibles()) {
         var1 |= 2;
      }

      return var1;
   }

   public void unpackOptions(int i) {
      this.setAllowFriendlyFire((i & 1) > 0);
      this.setSeeFriendlyInvisibles((i & 2) > 0);
   }

   public void setColor(ChatFormatting color) {
      this.color = color;
      this.scoreboard.onTeamChanged(this);
   }

   public ChatFormatting getColor() {
      return this.color;
   }
}
