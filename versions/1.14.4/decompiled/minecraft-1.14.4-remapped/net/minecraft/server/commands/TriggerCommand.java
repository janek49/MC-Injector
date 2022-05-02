package net.minecraft.server.commands;

import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.ObjectiveArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Score;
import net.minecraft.world.scores.Scoreboard;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class TriggerCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_PRIMED = new SimpleCommandExceptionType(new TranslatableComponent("commands.trigger.failed.unprimed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_INVALID_OBJECTIVE = new SimpleCommandExceptionType(new TranslatableComponent("commands.trigger.failed.invalid", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("trigger").then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("objective", ObjectiveArgument.objective()).suggests((commandContext, suggestionsBuilder) -> {
         return suggestObjectives((CommandSourceStack)commandContext.getSource(), suggestionsBuilder);
      }).executes((commandContext) -> {
         return simpleTrigger((CommandSourceStack)commandContext.getSource(), getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(commandContext, "objective")));
      })).then(Commands.literal("add").then(Commands.argument("value", IntegerArgumentType.integer()).executes((commandContext) -> {
         return addValue((CommandSourceStack)commandContext.getSource(), getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(commandContext, "objective")), IntegerArgumentType.getInteger(commandContext, "value"));
      })))).then(Commands.literal("set").then(Commands.argument("value", IntegerArgumentType.integer()).executes((commandContext) -> {
         return setValue((CommandSourceStack)commandContext.getSource(), getScore(((CommandSourceStack)commandContext.getSource()).getPlayerOrException(), ObjectiveArgument.getObjective(commandContext, "objective")), IntegerArgumentType.getInteger(commandContext, "value"));
      })))));
   }

   public static CompletableFuture suggestObjectives(CommandSourceStack commandSourceStack, SuggestionsBuilder suggestionsBuilder) {
      Entity var2 = commandSourceStack.getEntity();
      List<String> var3 = Lists.newArrayList();
      if(var2 != null) {
         Scoreboard var4 = commandSourceStack.getServer().getScoreboard();
         String var5 = var2.getScoreboardName();

         for(Objective var7 : var4.getObjectives()) {
            if(var7.getCriteria() == ObjectiveCriteria.TRIGGER && var4.hasPlayerScore(var5, var7)) {
               Score var8 = var4.getOrCreatePlayerScore(var5, var7);
               if(!var8.isLocked()) {
                  var3.add(var7.getName());
               }
            }
         }
      }

      return SharedSuggestionProvider.suggest((Iterable)var3, suggestionsBuilder);
   }

   private static int addValue(CommandSourceStack commandSourceStack, Score score, int var2) {
      score.add(var2);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.trigger.add.success", new Object[]{score.getObjective().getFormattedDisplayName(), Integer.valueOf(var2)}), true);
      return score.getScore();
   }

   private static int setValue(CommandSourceStack commandSourceStack, Score score, int var2) {
      score.setScore(var2);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.trigger.set.success", new Object[]{score.getObjective().getFormattedDisplayName(), Integer.valueOf(var2)}), true);
      return var2;
   }

   private static int simpleTrigger(CommandSourceStack commandSourceStack, Score score) {
      score.add(1);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.trigger.simple.success", new Object[]{score.getObjective().getFormattedDisplayName()}), true);
      return score.getScore();
   }

   private static Score getScore(ServerPlayer serverPlayer, Objective objective) throws CommandSyntaxException {
      if(objective.getCriteria() != ObjectiveCriteria.TRIGGER) {
         throw ERROR_INVALID_OBJECTIVE.create();
      } else {
         Scoreboard var2 = serverPlayer.getScoreboard();
         String var3 = serverPlayer.getScoreboardName();
         if(!var2.hasPlayerScore(var3, objective)) {
            throw ERROR_NOT_PRIMED.create();
         } else {
            Score var4 = var2.getOrCreatePlayerScore(var3, objective);
            if(var4.isLocked()) {
               throw ERROR_NOT_PRIMED.create();
            } else {
               var4.setLocked(true);
               return var4;
            }
         }
      }
   }
}
