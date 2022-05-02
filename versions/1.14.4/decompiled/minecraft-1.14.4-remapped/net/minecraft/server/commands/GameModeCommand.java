package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.Collection;
import java.util.Collections;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.GameType;

public class GameModeCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> var1 = (LiteralArgumentBuilder)Commands.literal("gamemode").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      });

      for(GameType var5 : GameType.values()) {
         if(var5 != GameType.NOT_SET) {
            var1.then(((LiteralArgumentBuilder)Commands.literal(var5.getName()).executes((commandContext) -> {
               return setMode(commandContext, Collections.singleton(((CommandSourceStack)commandContext.getSource()).getPlayerOrException()), var5);
            })).then(Commands.argument("target", EntityArgument.players()).executes((commandContext) -> {
               return setMode(commandContext, EntityArgument.getPlayers(commandContext, "target"), var5);
            })));
         }
      }

      commandDispatcher.register(var1);
   }

   private static void logGamemodeChange(CommandSourceStack commandSourceStack, ServerPlayer serverPlayer, GameType gameType) {
      Component var3 = new TranslatableComponent("gameMode." + gameType.getName(), new Object[0]);
      if(commandSourceStack.getEntity() == serverPlayer) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.gamemode.success.self", new Object[]{var3}), true);
      } else {
         if(commandSourceStack.getLevel().getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK)) {
            serverPlayer.sendMessage(new TranslatableComponent("gameMode.changed", new Object[]{var3}));
         }

         commandSourceStack.sendSuccess(new TranslatableComponent("commands.gamemode.success.other", new Object[]{serverPlayer.getDisplayName(), var3}), true);
      }

   }

   private static int setMode(CommandContext commandContext, Collection collection, GameType gameType) {
      int var3 = 0;

      for(ServerPlayer var5 : collection) {
         if(var5.gameMode.getGameModeForPlayer() != gameType) {
            var5.setGameMode(gameType);
            logGamemodeChange((CommandSourceStack)commandContext.getSource(), var5, gameType);
            ++var3;
         }
      }

      return var3;
   }
}
