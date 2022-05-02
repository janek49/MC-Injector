package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import java.util.function.BiConsumer;
import java.util.function.BiPredicate;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;

public class ExperienceCommand {
   private static final SimpleCommandExceptionType ERROR_SET_POINTS_INVALID = new SimpleCommandExceptionType(new TranslatableComponent("commands.experience.set.points.invalid", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      LiteralCommandNode<CommandSourceStack> var1 = commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("experience").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.literal("add").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer()).executes((commandContext) -> {
         return addExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), ExperienceCommand.Type.POINTS);
      })).then(Commands.literal("points").executes((commandContext) -> {
         return addExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.literal("levels").executes((commandContext) -> {
         return addExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), ExperienceCommand.Type.LEVELS);
      })))))).then(Commands.literal("set").then(Commands.argument("targets", EntityArgument.players()).then(((RequiredArgumentBuilder)((RequiredArgumentBuilder)Commands.argument("amount", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return setExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), ExperienceCommand.Type.POINTS);
      })).then(Commands.literal("points").executes((commandContext) -> {
         return setExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.literal("levels").executes((commandContext) -> {
         return setExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), IntegerArgumentType.getInteger(commandContext, "amount"), ExperienceCommand.Type.LEVELS);
      })))))).then(Commands.literal("query").then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.player()).then(Commands.literal("points").executes((commandContext) -> {
         return queryExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayer(commandContext, "targets"), ExperienceCommand.Type.POINTS);
      }))).then(Commands.literal("levels").executes((commandContext) -> {
         return queryExperience((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayer(commandContext, "targets"), ExperienceCommand.Type.LEVELS);
      })))));
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("xp").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).redirect(var1));
   }

   private static int queryExperience(CommandSourceStack commandSourceStack, ServerPlayer serverPlayer, ExperienceCommand.Type experienceCommand$Type) {
      int var3 = experienceCommand$Type.query.applyAsInt(serverPlayer);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.query." + experienceCommand$Type.name, new Object[]{serverPlayer.getDisplayName(), Integer.valueOf(var3)}), false);
      return var3;
   }

   private static int addExperience(CommandSourceStack commandSourceStack, Collection collection, int var2, ExperienceCommand.Type experienceCommand$Type) {
      for(ServerPlayer var5 : collection) {
         experienceCommand$Type.add.accept(var5, Integer.valueOf(var2));
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.add." + experienceCommand$Type.name + ".success.single", new Object[]{Integer.valueOf(var2), ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.add." + experienceCommand$Type.name + ".success.multiple", new Object[]{Integer.valueOf(var2), Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }

   private static int setExperience(CommandSourceStack commandSourceStack, Collection collection, int var2, ExperienceCommand.Type experienceCommand$Type) throws CommandSyntaxException {
      int var4 = 0;

      for(ServerPlayer var6 : collection) {
         if(experienceCommand$Type.set.test(var6, Integer.valueOf(var2))) {
            ++var4;
         }
      }

      if(var4 == 0) {
         throw ERROR_SET_POINTS_INVALID.create();
      } else {
         if(collection.size() == 1) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.set." + experienceCommand$Type.name + ".success.single", new Object[]{Integer.valueOf(var2), ((ServerPlayer)collection.iterator().next()).getDisplayName()}), true);
         } else {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.experience.set." + experienceCommand$Type.name + ".success.multiple", new Object[]{Integer.valueOf(var2), Integer.valueOf(collection.size())}), true);
         }

         return collection.size();
      }
   }

   static enum Type {
      POINTS("points", Player::giveExperiencePoints, (serverPlayer, integer) -> {
         if(integer.intValue() >= serverPlayer.getXpNeededForNextLevel()) {
            return false;
         } else {
            serverPlayer.setExperiencePoints(integer.intValue());
            return true;
         }
      }, (serverPlayer) -> {
         return Mth.floor(serverPlayer.experienceProgress * (float)serverPlayer.getXpNeededForNextLevel());
      }),
      LEVELS("levels", ServerPlayer::giveExperienceLevels, (serverPlayer, integer) -> {
         serverPlayer.setExperienceLevels(integer.intValue());
         return true;
      }, (serverPlayer) -> {
         return serverPlayer.experienceLevel;
      });

      public final BiConsumer add;
      public final BiPredicate set;
      public final String name;
      private final ToIntFunction query;

      private Type(String name, BiConsumer add, BiPredicate set, ToIntFunction query) {
         this.add = add;
         this.name = name;
         this.set = set;
         this.query = query;
      }
   }
}
