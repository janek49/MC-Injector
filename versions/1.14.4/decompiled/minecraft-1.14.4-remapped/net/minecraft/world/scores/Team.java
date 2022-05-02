package net.minecraft.world.scores;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public abstract class Team {
   public boolean isAlliedTo(@Nullable Team team) {
      return team == null?false:this == team;
   }

   public abstract String getName();

   public abstract Component getFormattedName(Component var1);

   public abstract boolean canSeeFriendlyInvisibles();

   public abstract boolean isAllowFriendlyFire();

   public abstract Team.Visibility getNameTagVisibility();

   public abstract ChatFormatting getColor();

   public abstract Collection getPlayers();

   public abstract Team.Visibility getDeathMessageVisibility();

   public abstract Team.CollisionRule getCollisionRule();

   public static enum CollisionRule {
      ALWAYS("always", 0),
      NEVER("never", 1),
      PUSH_OTHER_TEAMS("pushOtherTeams", 2),
      PUSH_OWN_TEAM("pushOwnTeam", 3);

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((team$CollisionRule) -> {
         return team$CollisionRule.name;
      }, (team$CollisionRule) -> {
         return team$CollisionRule;
      }));
      public final String name;
      public final int id;

      @Nullable
      public static Team.CollisionRule byName(String name) {
         return (Team.CollisionRule)BY_NAME.get(name);
      }

      private CollisionRule(String name, int id) {
         this.name = name;
         this.id = id;
      }

      public Component getDisplayName() {
         return new TranslatableComponent("team.collision." + this.name, new Object[0]);
      }
   }

   public static enum Visibility {
      ALWAYS("always", 0),
      NEVER("never", 1),
      HIDE_FOR_OTHER_TEAMS("hideForOtherTeams", 2),
      HIDE_FOR_OWN_TEAM("hideForOwnTeam", 3);

      private static final Map BY_NAME = (Map)Arrays.stream(values()).collect(Collectors.toMap((team$Visibility) -> {
         return team$Visibility.name;
      }, (team$Visibility) -> {
         return team$Visibility;
      }));
      public final String name;
      public final int id;

      @Nullable
      public static Team.Visibility byName(String name) {
         return (Team.Visibility)BY_NAME.get(name);
      }

      private Visibility(String name, int id) {
         this.name = name;
         this.id = id;
      }

      public Component getDisplayName() {
         return new TranslatableComponent("team.visibility." + this.name, new Object[0]);
      }
   }
}
