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
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.players.PlayerList;
import net.minecraft.server.players.UserWhiteList;
import net.minecraft.server.players.UserWhiteListEntry;

public class WhitelistCommand {
   private static final SimpleCommandExceptionType ERROR_ALREADY_ENABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.whitelist.alreadyOn", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_ALREADY_DISABLED = new SimpleCommandExceptionType(new TranslatableComponent("commands.whitelist.alreadyOff", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_ALREADY_WHITELISTED = new SimpleCommandExceptionType(new TranslatableComponent("commands.whitelist.add.failed", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_NOT_WHITELISTED = new SimpleCommandExceptionType(new TranslatableComponent("commands.whitelist.remove.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("whitelist").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(3);
      })).then(Commands.literal("on").executes((commandContext) -> {
         return enableWhitelist((CommandSourceStack)commandContext.getSource());
      }))).then(Commands.literal("off").executes((commandContext) -> {
         return disableWhitelist((CommandSourceStack)commandContext.getSource());
      }))).then(Commands.literal("list").executes((commandContext) -> {
         return showList((CommandSourceStack)commandContext.getSource());
      }))).then(Commands.literal("add").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
         PlayerList var2 = ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList();
         return SharedSuggestionProvider.suggest(var2.getPlayers().stream().filter((serverPlayer) -> {
            return !var2.getWhiteList().isWhiteListed(serverPlayer.getGameProfile());
         }).map((serverPlayer) -> {
            return serverPlayer.getGameProfile().getName();
         }), suggestionsBuilder);
      }).executes((commandContext) -> {
         return addPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles(commandContext, "targets"));
      })))).then(Commands.literal("remove").then(Commands.argument("targets", GameProfileArgument.gameProfile()).suggests((commandContext, suggestionsBuilder) -> {
         return SharedSuggestionProvider.suggest(((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getWhiteListNames(), suggestionsBuilder);
      }).executes((commandContext) -> {
         return removePlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles(commandContext, "targets"));
      })))).then(Commands.literal("reload").executes((commandContext) -> {
         return reload((CommandSourceStack)commandContext.getSource());
      })));
   }

   private static int reload(CommandSourceStack commandSourceStack) {
      commandSourceStack.getServer().getPlayerList().reloadWhiteList();
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.reloaded", new Object[0]), true);
      commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
      return 1;
   }

   private static int addPlayers(CommandSourceStack commandSourceStack, Collection collection) throws CommandSyntaxException {
      UserWhiteList var2 = commandSourceStack.getServer().getPlayerList().getWhiteList();
      int var3 = 0;

      for(GameProfile var5 : collection) {
         if(!var2.isWhiteListed(var5)) {
            UserWhiteListEntry var6 = new UserWhiteListEntry(var5);
            var2.add(var6);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.add.success", new Object[]{ComponentUtils.getDisplayName(var5)}), true);
            ++var3;
         }
      }

      if(var3 == 0) {
         throw ERROR_ALREADY_WHITELISTED.create();
      } else {
         return var3;
      }
   }

   private static int removePlayers(CommandSourceStack commandSourceStack, Collection collection) throws CommandSyntaxException {
      UserWhiteList var2 = commandSourceStack.getServer().getPlayerList().getWhiteList();
      int var3 = 0;

      for(GameProfile var5 : collection) {
         if(var2.isWhiteListed(var5)) {
            UserWhiteListEntry var6 = new UserWhiteListEntry(var5);
            var2.remove(var6);
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.remove.success", new Object[]{ComponentUtils.getDisplayName(var5)}), true);
            ++var3;
         }
      }

      if(var3 == 0) {
         throw ERROR_NOT_WHITELISTED.create();
      } else {
         commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
         return var3;
      }
   }

   private static int enableWhitelist(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      PlayerList var1 = commandSourceStack.getServer().getPlayerList();
      if(var1.isUsingWhitelist()) {
         throw ERROR_ALREADY_ENABLED.create();
      } else {
         var1.setUsingWhiteList(true);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.enabled", new Object[0]), true);
         commandSourceStack.getServer().kickUnlistedPlayers(commandSourceStack);
         return 1;
      }
   }

   private static int disableWhitelist(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
      PlayerList var1 = commandSourceStack.getServer().getPlayerList();
      if(!var1.isUsingWhitelist()) {
         throw ERROR_ALREADY_DISABLED.create();
      } else {
         var1.setUsingWhiteList(false);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.disabled", new Object[0]), true);
         return 1;
      }
   }

   private static int showList(CommandSourceStack commandSourceStack) {
      String[] vars1 = commandSourceStack.getServer().getPlayerList().getWhiteListNames();
      if(vars1.length == 0) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.none", new Object[0]), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.whitelist.list", new Object[]{Integer.valueOf(vars1.length), String.join(", ", vars1)}), false);
      }

      return vars1.length;
   }
}
