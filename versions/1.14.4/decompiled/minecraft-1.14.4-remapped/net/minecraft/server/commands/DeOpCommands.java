package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.players.PlayerList;

public class DeOpCommands {
   private static final SimpleCommandExceptionType ERROR_NOT_OP = new SimpleCommandExceptionType(new TranslatableComponent("commands.deop.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("deop").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(3);
      })).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
         return SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getOpNames(), suggestionsBuilder);
      }).executes((commandContext) -> {
         return deopPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles(commandContext, "targets"));
      })));
   }

   private static int deopPlayers(CommandSourceStack commandSourceStack, Collection collection) throws CommandSyntaxException {
      PlayerList var2 = commandSourceStack.getServer().getPlayerList();
      int var3 = 0;

      for(GameProfile var5 : collection) {
         if(var2.isOp(var5)) {
            var2.deop(var5);
            ++var3;
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.deop.success", new Object[]{((GameProfile)collection.iterator().next()).getName()}), true);
         }
      }

      if(var3 == 0) {
         throw ERROR_NOT_OP.create();
      } else {
         commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
         return var3;
      }
   }
}
