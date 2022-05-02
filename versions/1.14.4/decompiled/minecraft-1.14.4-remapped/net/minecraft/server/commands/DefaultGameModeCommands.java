package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.GameType;

public class DefaultGameModeCommands {
   public static void register(CommandDispatcher commandDispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> var1 = (LiteralArgumentBuilder)Commands.literal("defaultgamemode").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      });

      for(GameType var5 : GameType.values()) {
         if(var5 != GameType.NOT_SET) {
            var1.then(Commands.literal(var5.getName()).executes((commandContext) -> {
               return setMode((CommandSourceStack)commandContext.getSource(), var5);
            }));
         }
      }

      commandDispatcher.register(var1);
   }

   private static int setMode(CommandSourceStack commandSourceStack, GameType gameType) {
      int var2 = 0;
      MinecraftServer var3 = commandSourceStack.getServer();
      var3.setDefaultGameMode(gameType);
      if(var3.getForceGameType()) {
         for(ServerPlayer var5 : var3.getPlayerList().getPlayers()) {
            if(var5.gameMode.getGameModeForPlayer() != gameType) {
               var5.setGameMode(gameType);
               ++var2;
            }
         }
      }

      commandSourceStack.sendSuccess(new TranslatableComponent("commands.defaultgamemode.success", new Object[]{gameType.getDisplayName()}), true);
      return var2;
   }
}
