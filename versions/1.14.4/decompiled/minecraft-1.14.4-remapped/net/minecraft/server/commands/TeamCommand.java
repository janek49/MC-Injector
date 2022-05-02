package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ColorArgument;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.TeamArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.Team;

public class TeamCommand {
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EXISTS = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.add.duplicate", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_TEAM_NAME_TOO_LONG = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.team.add.longName", new Object[]{object});
   });
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.empty.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_NAME = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.name.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_COLOR = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.color.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.friendlyfire.alreadyEnabled", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.friendlyfire.alreadyDisabled", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.seeFriendlyInvisibles.alreadyEnabled", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.seeFriendlyInvisibles.alreadyDisabled", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.nametagVisibility.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.deathMessageVisibility.unchanged", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TEAM_COLLISION_UNCHANGED = new SimpleCommandExceptionType(new TranslatableComponent("commands.team.option.collisionRule.unchanged", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("team").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((LiteralArgumentBuilder)Commands.literal("list").executes((commandContext) -> {
         return listTeams((CommandSourceStack)commandContext.getSource());
      })).then(Commands.argument("team", TeamArgument.team()).executes((commandContext) -> {
         return listMembers((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"));
      })))).then(Commands.literal("add").then(((RequiredArgumentBuilder)Commands.argument("team", StringArgumentType.word()).executes((commandContext) -> {
         return createTeam((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "team"));
      })).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((commandContext) -> {
         return createTeam((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "team"), ComponentArgument.getComponent(commandContext, "displayName"));
      }))))).then(Commands.literal("remove").then(Commands.argument("team", TeamArgument.team()).executes((commandContext) -> {
         return deleteTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"));
      })))).then(Commands.literal("empty").then(Commands.argument("team", TeamArgument.team()).executes((commandContext) -> {
         return emptyTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"));
      })))).then(Commands.literal("join").then(((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team()).executes((commandContext) -> {
         return joinTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Collections.singleton(((CommandSourceStack)commandContext.getSource()).getEntityOrException().getScoreboardName()));
      })).then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((commandContext) -> {
         return joinTeam((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "members"));
      }))))).then(Commands.literal("leave").then(Commands.argument("members", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((commandContext) -> {
         return leaveTeam((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "members"));
      })))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("team", TeamArgument.team()).then(Commands.literal("displayName").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((commandContext) -> {
         return setDisplayName((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), ComponentArgument.getComponent(commandContext, "displayName"));
      })))).then(Commands.literal("color").then(Commands.argument("value", ColorArgument.color()).executes((commandContext) -> {
         return setColor((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), ColorArgument.getColor(commandContext, "value"));
      })))).then(Commands.literal("friendlyFire").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((commandContext) -> {
         return setFriendlyFire((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed"));
      })))).then(Commands.literal("seeFriendlyInvisibles").then(Commands.argument("allowed", BoolArgumentType.bool()).executes((commandContext) -> {
         return setFriendlySight((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), BoolArgumentType.getBool(commandContext, "allowed"));
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("nametagVisibility").then(Commands.literal("never").executes((commandContext) -> {
         return setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.Visibility.NEVER);
      }))).then(Commands.literal("hideForOtherTeams").executes((commandContext) -> {
         return setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS);
      }))).then(Commands.literal("hideForOwnTeam").executes((commandContext) -> {
         return setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM);
      }))).then(Commands.literal("always").executes((commandContext) -> {
         return setNametagVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.Visibility.ALWAYS);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deathMessageVisibility").then(Commands.literal("never").executes((commandContext) -> {
         return setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.Visibility.NEVER);
      }))).then(Commands.literal("hideForOtherTeams").executes((commandContext) -> {
         return setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.Visibility.HIDE_FOR_OTHER_TEAMS);
      }))).then(Commands.literal("hideForOwnTeam").executes((commandContext) -> {
         return setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.Visibility.HIDE_FOR_OWN_TEAM);
      }))).then(Commands.literal("always").executes((commandContext) -> {
         return setDeathMessageVisibility((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.Visibility.ALWAYS);
      })))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("collisionRule").then(Commands.literal("never").executes((commandContext) -> {
         return setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.CollisionRule.NEVER);
      }))).then(Commands.literal("pushOwnTeam").executes((commandContext) -> {
         return setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.CollisionRule.PUSH_OWN_TEAM);
      }))).then(Commands.literal("pushOtherTeams").executes((commandContext) -> {
         return setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.CollisionRule.PUSH_OTHER_TEAMS);
      }))).then(Commands.literal("always").executes((commandContext) -> {
         return setCollision((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), Team.CollisionRule.ALWAYS);
      })))).then(Commands.literal("prefix").then(Commands.argument("prefix", ComponentArgument.textComponent()).executes((commandContext) -> {
         return setPrefix((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), ComponentArgument.getComponent(commandContext, "prefix"));
      })))).then(Commands.literal("suffix").then(Commands.argument("suffix", ComponentArgument.textComponent()).executes((commandContext) -> {
         return setSuffix((CommandSourceStack)commandContext.getSource(), TeamArgument.getTeam(commandContext, "team"), ComponentArgument.getComponent(commandContext, "suffix"));
      }))))));
   }

   private static int leaveTeam(CommandSourceStack commandSourceStack, Collection collection) {
      Scoreboard var2 = commandSourceStack.getServer().getScoreboard();

      for(String var4 : collection) {
         var2.removePlayerFromTeam(var4);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.leave.success.single", new Object[]{collection.iterator().next()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.leave.success.multiple", new Object[]{Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }

   private static int joinTeam(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Collection collection) {
      Scoreboard var3 = commandSourceStack.getServer().getScoreboard();

      for(String var5 : collection) {
         var3.addPlayerToTeam(var5, playerTeam);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.join.success.single", new Object[]{collection.iterator().next(), playerTeam.getFormattedDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.join.success.multiple", new Object[]{Integer.valueOf(collection.size()), playerTeam.getFormattedDisplayName()}), true);
      }

      return collection.size();
   }

   private static int setNametagVisibility(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Team.Visibility team$Visibility) throws CommandSyntaxException {
      if(playerTeam.getNameTagVisibility() == team$Visibility) {
         throw ERROR_TEAM_NAMETAG_VISIBLITY_UNCHANGED.create();
      } else {
         playerTeam.setNameTagVisibility(team$Visibility);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.nametagVisibility.success", new Object[]{playerTeam.getFormattedDisplayName(), team$Visibility.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setDeathMessageVisibility(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Team.Visibility team$Visibility) throws CommandSyntaxException {
      if(playerTeam.getDeathMessageVisibility() == team$Visibility) {
         throw ERROR_TEAM_DEATH_MESSAGE_VISIBLITY_UNCHANGED.create();
      } else {
         playerTeam.setDeathMessageVisibility(team$Visibility);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.deathMessageVisibility.success", new Object[]{playerTeam.getFormattedDisplayName(), team$Visibility.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setCollision(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Team.CollisionRule team$CollisionRule) throws CommandSyntaxException {
      if(playerTeam.getCollisionRule() == team$CollisionRule) {
         throw ERROR_TEAM_COLLISION_UNCHANGED.create();
      } else {
         playerTeam.setCollisionRule(team$CollisionRule);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.collisionRule.success", new Object[]{playerTeam.getFormattedDisplayName(), team$CollisionRule.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setFriendlySight(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, boolean var2) throws CommandSyntaxException {
      if(playerTeam.canSeeFriendlyInvisibles() == var2) {
         if(var2) {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYINVISIBLES_DISABLED.create();
         }
      } else {
         playerTeam.setSeeFriendlyInvisibles(var2);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.seeFriendlyInvisibles." + (var2?"enabled":"disabled"), new Object[]{playerTeam.getFormattedDisplayName()}), true);
         return 0;
      }
   }

   private static int setFriendlyFire(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, boolean var2) throws CommandSyntaxException {
      if(playerTeam.isAllowFriendlyFire() == var2) {
         if(var2) {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_ENABLED.create();
         } else {
            throw ERROR_TEAM_ALREADY_FRIENDLYFIRE_DISABLED.create();
         }
      } else {
         playerTeam.setAllowFriendlyFire(var2);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.friendlyfire." + (var2?"enabled":"disabled"), new Object[]{playerTeam.getFormattedDisplayName()}), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Component component) throws CommandSyntaxException {
      if(playerTeam.getDisplayName().equals(component)) {
         throw ERROR_TEAM_ALREADY_NAME.create();
      } else {
         playerTeam.setDisplayName(component);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.name.success", new Object[]{playerTeam.getFormattedDisplayName()}), true);
         return 0;
      }
   }

   private static int setColor(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, ChatFormatting chatFormatting) throws CommandSyntaxException {
      if(playerTeam.getColor() == chatFormatting) {
         throw ERROR_TEAM_ALREADY_COLOR.create();
      } else {
         playerTeam.setColor(chatFormatting);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.color.success", new Object[]{playerTeam.getFormattedDisplayName(), chatFormatting.getName()}), true);
         return 0;
      }
   }

   private static int emptyTeam(CommandSourceStack commandSourceStack, PlayerTeam playerTeam) throws CommandSyntaxException {
      Scoreboard var2 = commandSourceStack.getServer().getScoreboard();
      Collection<String> var3 = Lists.newArrayList(playerTeam.getPlayers());
      if(var3.isEmpty()) {
         throw ERROR_TEAM_ALREADY_EMPTY.create();
      } else {
         for(String var5 : var3) {
            var2.removePlayerFromTeam(var5, playerTeam);
         }

         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.empty.success", new Object[]{Integer.valueOf(var3.size()), playerTeam.getFormattedDisplayName()}), true);
         return var3.size();
      }
   }

   private static int deleteTeam(CommandSourceStack commandSourceStack, PlayerTeam playerTeam) {
      Scoreboard var2 = commandSourceStack.getServer().getScoreboard();
      var2.removePlayerTeam(playerTeam);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.remove.success", new Object[]{playerTeam.getFormattedDisplayName()}), true);
      return var2.getPlayerTeams().size();
   }

   private static int createTeam(CommandSourceStack commandSourceStack, String string) throws CommandSyntaxException {
      return createTeam(commandSourceStack, string, new TextComponent(string));
   }

   private static int createTeam(CommandSourceStack commandSourceStack, String string, Component component) throws CommandSyntaxException {
      Scoreboard var3 = commandSourceStack.getServer().getScoreboard();
      if(var3.getPlayerTeam(string) != null) {
         throw ERROR_TEAM_ALREADY_EXISTS.create();
      } else if(string.length() > 16) {
         throw ERROR_TEAM_NAME_TOO_LONG.create(Integer.valueOf(16));
      } else {
         PlayerTeam var4 = var3.addPlayerTeam(string);
         var4.setDisplayName(component);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.add.success", new Object[]{var4.getFormattedDisplayName()}), true);
         return var3.getPlayerTeams().size();
      }
   }

   private static int listMembers(CommandSourceStack commandSourceStack, PlayerTeam playerTeam) {
      Collection<String> var2 = playerTeam.getPlayers();
      if(var2.isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.list.members.empty", new Object[]{playerTeam.getFormattedDisplayName()}), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.list.members.success", new Object[]{playerTeam.getFormattedDisplayName(), Integer.valueOf(var2.size()), ComponentUtils.formatList(var2)}), false);
      }

      return var2.size();
   }

   private static int listTeams(CommandSourceStack commandSourceStack) {
      Collection<PlayerTeam> var1 = commandSourceStack.getServer().getScoreboard().getPlayerTeams();
      if(var1.isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.list.teams.empty", new Object[0]), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.list.teams.success", new Object[]{Integer.valueOf(var1.size()), ComponentUtils.formatList(var1, PlayerTeam::getFormattedDisplayName)}), false);
      }

      return var1.size();
   }

   private static int setPrefix(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Component component) {
      playerTeam.setPlayerPrefix(component);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.prefix.success", new Object[]{component}), false);
      return 1;
   }

   private static int setSuffix(CommandSourceStack commandSourceStack, PlayerTeam playerTeam, Component component) {
      playerTeam.setPlayerSuffix(component);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.team.option.suffix.success", new Object[]{component}), false);
      return 1;
   }
}
