package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.Date;
import java.util.List;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.IpBanList;
import net.minecraft.server.players.IpBanListEntry;

public class BanIpCommands {
   public static final Pattern IP_ADDRESS_PATTERN = Pattern.compile("^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\.([01]?\\d\\d?|2[0-4]\\d|25[0-5])$");
   private static final SimpleCommandExceptionType ERROR_INVALID_IP = new SimpleCommandExceptionType(new TranslatableComponent("commands.banip.invalid", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_ALREADY_BANNED = new SimpleCommandExceptionType(new TranslatableComponent("commands.banip.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("ban-ip").requires((commandSourceStack) -> {
         return commandSourceStack.getServer().getPlayerList().getIpBans().isEnabled() && commandSourceStack.hasPermission(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("target", StringArgumentType.word()).executes((commandContext) -> {
         return banIpOrName((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), (Component)null);
      })).then(Commands.argument("reason", MessageArgument.message()).executes((commandContext) -> {
         return banIpOrName((CommandSourceStack)commandContext.getSource(), StringArgumentType.getString(commandContext, "target"), MessageArgument.getMessage(commandContext, "reason"));
      }))));
   }

   private static int banIpOrName(CommandSourceStack commandSourceStack, String string, @Nullable Component component) throws CommandSyntaxException {
      Matcher var3 = IP_ADDRESS_PATTERN.matcher(string);
      if(var3.matches()) {
         return banIp(commandSourceStack, string, component);
      } else {
         ServerPlayer var4 = commandSourceStack.getServer().getPlayerList().getPlayerByName(string);
         if(var4 != null) {
            return banIp(commandSourceStack, var4.getIpAddress(), component);
         } else {
            throw ERROR_INVALID_IP.create();
         }
      }
   }

   private static int banIp(CommandSourceStack commandSourceStack, String string, @Nullable Component component) throws CommandSyntaxException {
      IpBanList var3 = commandSourceStack.getServer().getPlayerList().getIpBans();
      if(var3.isBanned(string)) {
         throw ERROR_ALREADY_BANNED.create();
      } else {
         List<ServerPlayer> var4 = commandSourceStack.getServer().getPlayerList().getPlayersWithAddress(string);
         IpBanListEntry var5 = new IpBanListEntry(string, (Date)null, commandSourceStack.getTextName(), (Date)null, component == null?null:component.getString());
         var3.add(var5);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.banip.success", new Object[]{string, var5.getReason()}), true);
         if(!var4.isEmpty()) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.banip.info", new Object[]{Integer.valueOf(var4.size()), EntitySelector.joinNames(var4)}), true);
         }

         for(ServerPlayer var7 : var4) {
            var7.connection.disconnect(new TranslatableComponent("multiplayer.disconnect.ip_banned", new Object[0]));
         }

         return var4.size();
      }
   }
}
