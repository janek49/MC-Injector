package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.exceptions.Dynamic2CommandExceptionType.Function;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.commands.arguments.ObjectiveCriteriaArgument;
import net.minecraft.commands.arguments.OperationArgument;
import net.minecraft.commands.arguments.ScoreHolderArgument;
import net.minecraft.commands.arguments.ScoreboardSlotArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ScoreboardCommand {
   private static final SimpleCommandExceptionType ERROR_OBJECTIVE_ALREADY_EXISTS = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.add.duplicate", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_EMPTY = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.display.alreadyEmpty", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_DISPLAY_SLOT_ALREADY_SET = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.objectives.display.alreadySet", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_TRIGGER_ALREADY_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.players.enable.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NOT_TRIGGER = new SimpleCommandExceptionType(new TranslatableComponent("commands.scoreboard.players.enable.invalid", new Object[0]));
   private static final Dynamic2CommandExceptionType ERROR_NO_VALUE = new Dynamic2CommandExceptionType((var0, var1) -> {
      return new TranslatableComponent("commands.scoreboard.players.get.null", new Object[]{var0, var1});
   });

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("scoreboard").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("objectives").then(Commands.literal("list").executes((commandContext) -> {
         return listObjectives((CommandSourceStack)commandContext.getSource());
      }))).then(Commands.literal("add").then(Commands.argument("objective", StringArgumentType.word()).then(((RequiredArgumentBuilder)Commands.argument("criteria", ObjectiveCriteriaArgument.criteria()).executes((commandContext) -> {
         return addObjective((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "objective"), ObjectiveCriteriaArgument.getCriteria(commandContext, "criteria"), new TextComponent(StringArgumentType.getString(commandContext, "objective")));
      })).then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((commandContext) -> {
         return addObjective((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "objective"), ObjectiveCriteriaArgument.getCriteria(commandContext, "criteria"), ComponentArgument.getComponent(commandContext, "displayName"));
      })))))).then(Commands.literal("modify").then(((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.literal("displayname").then(Commands.argument("displayName", ComponentArgument.textComponent()).executes((commandContext) -> {
         return setDisplayName((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective(commandContext, "objective"), ComponentArgument.getComponent(commandContext, "displayName"));
      })))).then(createRenderTypeModify())))).then(Commands.literal("remove").then(Commands.argument("objective", ObjectiveArgument.objective()).executes((commandContext) -> {
         return removeObjective((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective(commandContext, "objective"));
      })))).then(Commands.literal("setdisplay").then(((RequiredArgumentBuilder)Commands.argument("slot", ScoreboardSlotArgument.displaySlot()).executes((commandContext) -> {
         return clearDisplaySlot((CommandSourceStack)commandContext.getSource(), ScoreboardSlotArgument.getDisplaySlot(commandContext, "slot"));
      })).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((commandContext) -> {
         return setDisplaySlot((CommandSourceStack)commandContext.getSource(), ScoreboardSlotArgument.getDisplaySlot(commandContext, "slot"), ObjectiveArgument.getObjective(commandContext, "objective"));
      })))))).then(((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("players").then(((LiteralArgumentBuilder)Commands.literal("list").executes((commandContext) -> {
         return listTrackedPlayers((CommandSourceStack)commandContext.getSource());
      })).then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((commandContext) -> {
         return listTrackedPlayerScores((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getName(commandContext, "target"));
      })))).then(Commands.literal("set").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer()).executes((commandContext) -> {
         return setScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"), ObjectiveArgument.getWritableObjective(commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score"));
      })))))).then(Commands.literal("get").then(Commands.argument("target", ScoreHolderArgument.scoreHolder()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((commandContext) -> {
         return getScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getName(commandContext, "target"), ObjectiveArgument.getObjective(commandContext, "objective"));
      }))))).then(Commands.literal("add").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return addScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"), ObjectiveArgument.getWritableObjective(commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score"));
      })))))).then(Commands.literal("remove").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).then(Commands.argument("score", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return removeScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"), ObjectiveArgument.getWritableObjective(commandContext, "objective"), IntegerArgumentType.getInteger(commandContext, "score"));
      })))))).then(Commands.literal("reset").then(((RequiredArgumentBuilder)Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).executes((commandContext) -> {
         return resetScores((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"));
      })).then(Commands.argument("objective", ObjectiveArgument.objective()).executes((commandContext) -> {
         return resetScore((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"), ObjectiveArgument.getObjective(commandContext, "objective"));
      }))))).then(Commands.literal("enable").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("objective", ObjectiveArgument.objective()).suggests((commandContext, suggestionsBuilder) -> {
         return suggestTriggers((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"), suggestionsBuilder);
      }).executes((commandContext) -> {
         return enableTrigger((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"), ObjectiveArgument.getObjective(commandContext, "objective"));
      }))))).then(Commands.literal("operation").then(Commands.argument("targets", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("targetObjective", ObjectiveArgument.objective()).then(Commands.argument("operation", OperationArgument.operation()).then(Commands.argument("source", ScoreHolderArgument.scoreHolders()).suggests(ScoreHolderArgument.SUGGEST_SCORE_HOLDERS).then(Commands.argument("sourceObjective", ObjectiveArgument.objective()).executes((commandContext) -> {
         return performOperation((CommandSourceStack)commandContext.getSource(), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "targets"), ObjectiveArgument.getWritableObjective(commandContext, "targetObjective"), OperationArgument.getOperation(commandContext, "operation"), ScoreHolderArgument.getNamesWithDefaultWildcard(commandContext, "source"), ObjectiveArgument.getObjective(commandContext, "sourceObjective"));
      })))))))));
   }

   private static LiteralArgumentBuilder createRenderTypeModify() {
      LiteralArgumentBuilder<CommandSourceStack> literalArgumentBuilder = Commands.literal("rendertype");

      for(ObjectiveCriteria.RenderType var4 : ObjectiveCriteria.RenderType.values()) {
         literalArgumentBuilder.then(Commands.literal(var4.getId()).executes((commandContext) -> {
            return setRenderType((CommandSourceStack)commandContext.getSource(), ObjectiveArgument.getObjective(commandContext, "objective"), var4);
         }));
      }

      return literalArgumentBuilder;
   }

   private static CompletableFuture suggestTriggers(CommandSourceStack commandSourceStack, Collection collection, SuggestionsBuilder suggestionsBuilder) {
      List<String> var3 = Lists.newArrayList();
      Scoreboard var4 = commandSourceStack.getServer().getScoreboard();

      for(Objective var6 : var4.getObjectives()) {
         if(var6.getCriteria() == ObjectiveCriteria.TRIGGER) {
            boolean var7 = false;

            for(String var9 : collection) {
               if(!var4.hasPlayerScore(var9, var6) || var4.getOrCreatePlayerScore(var9, var6).isLocked()) {
                  var7 = true;
                  break;
               }
            }

            if(var7) {
               var3.add(var6.getName());
            }
         }
      }

      return SharedSuggestionProvider.suggest((Iterable)var3, suggestionsBuilder);
   }

   private static int getScore(CommandSourceStack commandSourceStack, String string, Objective objective) throws CommandSyntaxException {
      Scoreboard var3 = commandSourceStack.getServer().getScoreboard();
      if(!var3.hasPlayerScore(string, objective)) {
         throw ERROR_NO_VALUE.create(objective.getName(), string);
      } else {
         Score var4 = var3.getOrCreatePlayerScore(string, objective);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.get.success", new Object[]{string, Integer.valueOf(var4.getScore()), objective.getFormattedDisplayName()}), false);
         return var4.getScore();
      }
   }

   private static int performOperation(CommandSourceStack commandSourceStack, Collection var1, Objective var2, OperationArgument.Operation operationArgument$Operation, Collection var4, Objective var5) throws CommandSyntaxException {
      Scoreboard var6 = commandSourceStack.getServer().getScoreboard();
      int var7 = 0;

      for(String var9 : var1) {
         Score var10 = var6.getOrCreatePlayerScore(var9, var2);

         for(String var12 : var4) {
            Score var13 = var6.getOrCreatePlayerScore(var12, var5);
            operationArgument$Operation.apply(var10, var13);
         }

         var7 += var10.getScore();
      }

      if(var1.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.single", new Object[]{var2.getFormattedDisplayName(), var1.iterator().next(), Integer.valueOf(var7)}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.operation.success.multiple", new Object[]{var2.getFormattedDisplayName(), Integer.valueOf(var1.size())}), true);
      }

      return var7;
   }

   private static int enableTrigger(CommandSourceStack commandSourceStack, Collection collection, Objective objective) throws CommandSyntaxException {
      if(objective.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_NOT_TRIGGER.create();
      } else {
         Scoreboard var3 = commandSourceStack.getServer().getScoreboard();
         int var4 = 0;

         for(String var6 : collection) {
            Score var7 = var3.getOrCreatePlayerScore(var6, objective);
            if(var7.isLocked()) {
               var7.setLocked(false);
               ++var4;
            }
         }

         if(var4 == 0) {
            throw ERROR_TRIGGER_ALREADY_ENABLED.create();
         } else {
            if(collection.size() == 1) {
               commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.single", new Object[]{objective.getFormattedDisplayName(), collection.iterator().next()}), true);
            } else {
               commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.enable.success.multiple", new Object[]{objective.getFormattedDisplayName(), Integer.valueOf(collection.size())}), true);
            }

            return var4;
         }
      }
   }

   private static int resetScores(CommandSourceStack commandSourceStack, Collection collection) {
      Scoreboard var2 = commandSourceStack.getServer().getScoreboard();

      for(String var4 : collection) {
         var2.resetPlayerScore(var4, (Objective)null);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.single", new Object[]{collection.iterator().next()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.all.multiple", new Object[]{Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }

   private static int resetScore(CommandSourceStack commandSourceStack, Collection collection, Objective objective) {
      Scoreboard var3 = commandSourceStack.getServer().getScoreboard();

      for(String var5 : collection) {
         var3.resetPlayerScore(var5, objective);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.single", new Object[]{objective.getFormattedDisplayName(), collection.iterator().next()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.reset.specific.multiple", new Object[]{objective.getFormattedDisplayName(), Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }

   private static int setScore(CommandSourceStack commandSourceStack, Collection collection, Objective objective, int var3) {
      Scoreboard var4 = commandSourceStack.getServer().getScoreboard();

      for(String var6 : collection) {
         Score var7 = var4.getOrCreatePlayerScore(var6, objective);
         var7.setScore(var3);
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.single", new Object[]{objective.getFormattedDisplayName(), collection.iterator().next(), Integer.valueOf(var3)}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.set.success.multiple", new Object[]{objective.getFormattedDisplayName(), Integer.valueOf(collection.size()), Integer.valueOf(var3)}), true);
      }

      return var3 * collection.size();
   }

   private static int addScore(CommandSourceStack commandSourceStack, Collection collection, Objective objective, int var3) {
      Scoreboard var4 = commandSourceStack.getServer().getScoreboard();
      int var5 = 0;

      for(String var7 : collection) {
         Score var8 = var4.getOrCreatePlayerScore(var7, objective);
         var8.setScore(var8.getScore() + var3);
         var5 += var8.getScore();
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.single", new Object[]{Integer.valueOf(var3), objective.getFormattedDisplayName(), collection.iterator().next(), Integer.valueOf(var5)}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.add.success.multiple", new Object[]{Integer.valueOf(var3), objective.getFormattedDisplayName(), Integer.valueOf(collection.size())}), true);
      }

      return var5;
   }

   private static int removeScore(CommandSourceStack commandSourceStack, Collection collection, Objective objective, int var3) {
      Scoreboard var4 = commandSourceStack.getServer().getScoreboard();
      int var5 = 0;

      for(String var7 : collection) {
         Score var8 = var4.getOrCreatePlayerScore(var7, objective);
         var8.setScore(var8.getScore() - var3);
         var5 += var8.getScore();
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.single", new Object[]{Integer.valueOf(var3), objective.getFormattedDisplayName(), collection.iterator().next(), Integer.valueOf(var5)}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.remove.success.multiple", new Object[]{Integer.valueOf(var3), objective.getFormattedDisplayName(), Integer.valueOf(collection.size())}), true);
      }

      return var5;
   }

   private static int listTrackedPlayers(CommandSourceStack commandSourceStack) {
      Collection<String> var1 = commandSourceStack.getServer().getScoreboard().getTrackedPlayers();
      if(var1.isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.empty", new Object[0]), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.success", new Object[]{Integer.valueOf(var1.size()), ComponentUtils.formatList(var1)}), false);
      }

      return var1.size();
   }

   private static int listTrackedPlayerScores(CommandSourceStack commandSourceStack, String string) {
      Map<Objective, Score> var2 = commandSourceStack.getServer().getScoreboard().getPlayerScores(string);
      if(var2.isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.empty", new Object[]{string}), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.success", new Object[]{string, Integer.valueOf(var2.size())}), false);

         for(Entry<Objective, Score> var4 : var2.entrySet()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.players.list.entity.entry", new Object[]{((Objective)var4.getKey()).getFormattedDisplayName(), Integer.valueOf(((Score)var4.getValue()).getScore())}), false);
         }
      }

      return var2.size();
   }

   private static int clearDisplaySlot(CommandSourceStack commandSourceStack, int var1) throws CommandSyntaxException {
      Scoreboard var2 = commandSourceStack.getServer().getScoreboard();
      if(var2.getDisplayObjective(var1) == null) {
         throw ERROR_DISPLAY_SLOT_ALREADY_EMPTY.create();
      } else {
         var2.setDisplayObjective(var1, (Objective)null);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.cleared", new Object[]{Scoreboard.getDisplaySlotNames()[var1]}), true);
         return 0;
      }
   }

   private static int setDisplaySlot(CommandSourceStack commandSourceStack, int var1, Objective objective) throws CommandSyntaxException {
      Scoreboard var3 = commandSourceStack.getServer().getScoreboard();
      if(var3.getDisplayObjective(var1) == objective) {
         throw ERROR_DISPLAY_SLOT_ALREADY_SET.create();
      } else {
         var3.setDisplayObjective(var1, objective);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.display.set", new Object[]{Scoreboard.getDisplaySlotNames()[var1], objective.getDisplayName()}), true);
         return 0;
      }
   }

   private static int setDisplayName(CommandSourceStack commandSourceStack, Objective objective, Component component) {
      if(!objective.getDisplayName().equals(component)) {
         objective.setDisplayName(component);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.displayname", new Object[]{objective.getName(), objective.getFormattedDisplayName()}), true);
      }

      return 0;
   }

   private static int setRenderType(CommandSourceStack commandSourceStack, Objective objective, ObjectiveCriteria.RenderType objectiveCriteria$RenderType) {
      if(objective.getRenderType() != objectiveCriteria$RenderType) {
         objective.setRenderType(objectiveCriteria$RenderType);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.modify.rendertype", new Object[]{objective.getFormattedDisplayName()}), true);
      }

      return 0;
   }

   private static int removeObjective(CommandSourceStack commandSourceStack, Objective objective) {
      Scoreboard var2 = commandSourceStack.getServer().getScoreboard();
      var2.removeObjective(objective);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.remove.success", new Object[]{objective.getFormattedDisplayName()}), true);
      return var2.getObjectives().size();
   }

   private static int addObjective(CommandSourceStack commandSourceStack, String string, ObjectiveCriteria objectiveCriteria, Component component) throws CommandSyntaxException {
      Scoreboard var4 = commandSourceStack.getServer().getScoreboard();
      if(var4.getObjective(string) != null) {
         throw ERROR_OBJECTIVE_ALREADY_EXISTS.create();
      } else if(string.length() > 16) {
         throw ObjectiveArgument.ERROR_OBJECTIVE_NAME_TOO_LONG.create(Integer.valueOf(16));
      } else {
         var4.addObjective(string, objectiveCriteria, component, objectiveCriteria.getDefaultRenderType());
         Objective var5 = var4.getObjective(string);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.add.success", new Object[]{var5.getFormattedDisplayName()}), true);
         return var4.getObjectives().size();
      }
   }

   private static int listObjectives(CommandSourceStack commandSourceStack) {
      Collection<Objective> var1 = commandSourceStack.getServer().getScoreboard().getObjectives();
      if(var1.isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.empty", new Object[0]), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.scoreboard.objectives.list.success", new Object[]{Integer.valueOf(var1.size()), ComponentUtils.formatList(var1, Objective::getFormattedDisplayName)}), false);
      }

      return var1.size();
   }
}
