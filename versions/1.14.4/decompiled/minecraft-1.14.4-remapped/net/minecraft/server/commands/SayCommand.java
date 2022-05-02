package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class SayCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("say").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("message", MessageArgument.message()).executes((commandContext) -> {
         Component var1 = MessageArgument.getMessage(commandContext, "message");
         ((CommandSourceStack)commandContext.getSource()).getServer().getPlayerList().broadcastMessage(new TranslatableComponent("chat.type.announcement", new Object[]{((CommandSourceStack)commandContext.getSource()).getDisplayName(), var1}));
         return 1;
      })));
   }
}
