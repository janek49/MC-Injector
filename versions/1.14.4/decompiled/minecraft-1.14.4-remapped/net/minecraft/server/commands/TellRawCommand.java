package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.ComponentArgument;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerPlayer;

public class TellRawCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("tellraw").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", ComponentArgument.textComponent()).executes((commandContext) -> {
         int var1 = 0;

         for(ServerPlayer var3 : EntityArgument.getPlayers(commandContext, "targets")) {
            var3.sendMessage(ComponentUtils.updateForEntity((CommandSourceStack)commandContext.getSource(), ComponentArgument.getComponent(commandContext, "message"), var3, 0));
            ++var1;
         }

         return var1;
      }))));
   }
}
