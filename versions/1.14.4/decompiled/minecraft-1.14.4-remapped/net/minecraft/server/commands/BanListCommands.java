package net.minecraft.server.commands;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.players.BanListEntry;
import net.minecraft.server.players.PlayerList;

public class BanListCommands {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("banlist").requires((commandSourceStack) -> {
         return (commandSourceStack.getServer().getPlayerList().getBans().isEnabled() || commandSourceStack.getServer().getPlayerList().getIpBans().isEnabled()) && commandSourceStack.hasPermission(3);
      })).executes((commandContext) -> {
         PlayerList var1 = ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList();
         return showList((CommandSourceStack)commandContext.getSource(), Lists.newArrayList(Iterables.concat(var1.getBans().getEntries(), var1.getIpBans().getEntries())));
      })).then(Commands.literal("ips").executes((commandContext) -> {
         return showList((CommandSourceStack)commandContext.getSource(), ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getIpBans().getEntries());
      }))).then(Commands.literal("players").executes((commandContext) -> {
         return showList((CommandSourceStack)commandContext.getSource(), ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().getBans().getEntries());
      })));
   }

   private static int showList(CommandSourceStack commandSourceStack, Collection collection) {
      if(collection.isEmpty()) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.banlist.none", new Object[0]), false);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.banlist.list", new Object[]{Integer.valueOf(collection.size())}), false);

         for(BanListEntry<?> var3 : collection) {
            commandSourceStack.sendSuccess(new TranslatableComponent("commands.banlist.entry", new Object[]{var3.getDisplayName(), var3.getSource(), var3.getReason()}), false);
         }
      }

      return collection.size();
   }
}
