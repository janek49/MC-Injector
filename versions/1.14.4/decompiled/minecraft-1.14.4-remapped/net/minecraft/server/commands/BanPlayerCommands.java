package net.minecraft.server.commands;

import com.mojang.authlib.GameProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Collection;
import java.util.Date;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.GameProfileArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.UserBanList;
import net.minecraft.server.players.UserBanListEntry;

public class BanPlayerCommands {
   private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(new TranslatableComponent("commands.ban.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban").requires((commandSourceStack) -> {
         return commandSourceStack.getServer().getPlayerList().getBans().isEnabled() && commandSourceStack.hasPermission(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", GameProfileArgument.gameProfile()).executes((commandContext) -> {
         return banPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles(commandContext, "targets"), (Component)null);
      })).then(Commands.argument("reason", MessageArgument.message()).executes((commandContext) -> {
         return banPlayers((CommandSourceStack)commandContext.getSource(), GameProfileArgument.getGameProfiles(commandContext, "targets"), MessageArgument.getMessage(commandContext, "reason"));
      }))));
   }

   private static int banPlayers(CommandSourceStack commandSourceStack, Collection collection, @Nullable Component component) throws CommandSyntaxException {
      UserBanList var3 = commandSourceStack.getServer().getPlayerList().getBans();
      int var4 = 0;

      for(GameProfile var6 : collection) {
         if(!var3.isBanned(var6)) {
            UserBanListEntry var7 = new UserBanListEntry(var6, (Date)null, commandSourceStack.getTextName(), (Date)null, component == null?null:component.getString());
            var3.add(var7);
            ++var4;
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.ban.success", new Object[]{ComponentUtils.getDisplayName(var6), var7.getReason()}), true);
            ServerPlayer var8 = commandSourceStack.getServer().getPlayerList().getPlayer(var6.getId());
            if(var8 != null) {
               var8.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.banned", new Object[0]));
            }
         }
      }

      if(var4 == 0) {
         throw ERROR_ALREADY_BANNED.create();
      } else {
         return var4;
      }
   }
}
