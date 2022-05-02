package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.List;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;

public class ListPlayersCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("list").executes((commandContext) -> {
         return listPlayers((CommandSourceStack)commandContext.getSource());
      })).then(Commands.literal("uuids").executes((commandContext) -> {
         return listPlayersWithUuids((CommandSourceStack)commandContext.getSource());
      })));
   }

   private static int listPlayers(CommandSourceStack commandSourceStack) {
      return format(commandSourceStack, Player::getDisplayName);
   }

   private static int listPlayersWithUuids(CommandSourceStack commandSourceStack) {
      return format(commandSourceStack, Player::getDisplayNameWithUuid);
   }

   private static int format(CommandSourceStack commandSourceStack, Function function) {
      PlayerList var2 = commandSourceStack.getServer().getPlayerList();
      List<ServerPlayer> var3 = var2.getPlayers();
      Component var4 = ComponentUtils.formatList(var3, function);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.list.players", new Object[]{Integer.valueOf(var3.size()), Integer.valueOf(var2.getMaxPlayers()), var4}), false);
      return var3.size();
   }
}
