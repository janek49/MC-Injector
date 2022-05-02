package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.commands.BanIpCommands;
import net.minecraft.server.players.IpBanList;

public class PardonIpCommand {
   private static final SimpleCommandExceptionType ERROR_INVALID = new SimpleCommandExceptionType(new TranslatableComponent("commands.pardonip.invalid", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NOT_BANNED = new SimpleCommandExceptionType(new TranslatableComponent("commands.pardonip.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("pardon-ip").requires((commandSourceStack) -> {
         return commandSourceStack.getServer().getPlayerList().getIpBans().isEnabled() && commandSourceStack.hasPermission(3);
      })).then(Commands.argument("target", StringArgumentType.word()).suggests((commandContext, suggestionsBuilder) -> {
         return SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getIpBans().getUserList(), suggestionsBuilder);
      }).executes((commandContext) -> {
         return unban((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"));
      })));
   }

   private static int unban(CommandSourceStack commandSourceStack, String string) throws CommandSyntaxException {
      Matcher var2 = BanIpCommands.IP_ADDRESS_PATTERN.matcher(string);
      if(!var2.matches()) {
         throw ERROR_INVALID.create();
      } else {
         IpBanList var3 = commandSourceStack.getServer().getPlayerList().getIpBans();
         if(!var3.isBanned(string)) {
            throw ERROR_NOT_BANNED.create();
         } else {
            var3.remove(string);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.pardonip.success", new Object[]{string}), true);
            return 1;
         }
      }
   }
}
