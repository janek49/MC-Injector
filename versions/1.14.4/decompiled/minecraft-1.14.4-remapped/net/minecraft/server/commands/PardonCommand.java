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
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.players.UserBanList;

public class PardonCommand {
   private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType(new TranslatableComponent("commands.pardon.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pardon").requires((commandSourceStack) -> {
         return commandSourceStack.getServer().getPlayerList().getIpBans().isEnabled() && commandSourceStack.hasPermission(3);
      })).then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
         return SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getBans().getUserList(), suggestionsBuilder);
      }).executes((commandContext) -> {
         return pardonPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles(commandContext, "targets"));
      })));
   }

   private static int pardonPlayers(CommandSourceStack commandSourceStack, Collection collection) throws CommandSyntaxException {
      UserBanList var2 = commandSourceStack.getServer().getPlayerList().getBans();
      int var3 = 0;

      for(GameProfile var5 : collection) {
         if(var2.isBanned(var5)) {
            var2.remove(var5);
            ++var3;
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.pardon.success", new Object[]{ComponentUtils.getDisplayName(var5)}), true);
         }
      }

      if(var3 == 0) {
         throw ERROR_NOT_BANNED.create();
      } else {
         return var3;
      }
   }
}
