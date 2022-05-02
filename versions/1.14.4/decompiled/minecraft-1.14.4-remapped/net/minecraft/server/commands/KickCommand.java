package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class KickCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kick").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(3);
      })).then(((RequiredArgumentBuilder)Commands.argument("targets", EntityArgument.players()).executes((commandContext) -> {
         return kickPlayers((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), new TranslatableComponent("multiplayer.disconnect.kicked", new Object[0]));
      })).then(Commands.argument("reason", MessageArgument.message()).executes((commandContext) -> {
         return kickPlayers((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), MessageArgument.getMessage(commandContext, "reason"));
      }))));
   }

   private static int kickPlayers(CommandSourceStack commandSourceStack, Collection collection, Component component) {
      for(ServerPlayer var4 : collection) {
         var4.connection.disconnect(component);
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.kick.success", new Object[]{var4.getDisplayName(), component}), true);
      }

      return collection.size();
   }
}
