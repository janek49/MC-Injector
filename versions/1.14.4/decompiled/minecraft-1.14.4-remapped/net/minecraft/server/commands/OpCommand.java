package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.players.PlayerList;

public class OpCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_OP = new SimpleCommandExceptionType(new TranslatableComponent("commands.op.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("op").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(3);
      })).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
         PlayerList var2 = ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList();
         return SharedSuggestionProvider.suggest(var2.getPlayers().stream().filter((serverPlayer) -> {
            return !var2.isOp(serverPlayer.getGameProfile());
         }).map((serverPlayer) -> {
            return serverPlayer.getGameProfile().getName();
         }), suggestionsBuilder);
      }).executes((commandContext) -> {
         return opPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles(commandContext, "targets"));
      })));
   }

   private static int opPlayers(CommandSourceStack commandSourceStack, Collection collection) throws CommandSyntaxException {
      PlayerList var2 = commandSourceStack.getServer().getPlayerList();
      int var3 = 0;

      for(GameProfile var5 : collection) {
         if(!var2.isOp(var5)) {
            var2.op(var5);
            ++var3;
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.op.success", new Object[]{((GameProfile)collection.iterator().next()).getName()}), true);
         }
      }

      if(var3 == 0) {
         throw ERROR_ALREADY_OP.create();
      } else {
         return var3;
      }
   }
}
