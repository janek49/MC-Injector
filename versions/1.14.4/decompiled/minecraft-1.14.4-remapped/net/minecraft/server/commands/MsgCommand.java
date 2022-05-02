package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import java.util.Collection;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.MessageArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class MsgCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      LiteralCommandNode<CommandSourceStack> var1 = commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("msg").then(Commands.argument("targets", EntityArgument.players()).then(Commands.argument("message", MessageArgument.message()).executes((commandContext) -> {
         return sendMessage((CommandSourceStack)commandContext.getSource(), EntityArgument.getPlayers(commandContext, "targets"), MessageArgument.getMessage(commandContext, "message"));
      }))));
      commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("tell").redirect(var1));
      commandDispatcher.register((LiteralArgumentBuilder)Commands.literal("w").redirect(var1));
   }

   private static int sendMessage(CommandSourceStack commandSourceStack, Collection collection, Component component) {
      for(ServerPlayer var4 : collection) {
         var4.sendMessage((new TranslatableComponent("commands.message.display.incoming", new Object[]{commandSourceStack.getDisplayName(), component.deepCopy()})).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}));
         commandSourceStack.sendSuccess((new TranslatableComponent("commands.message.display.outgoing", new Object[]{var4.getDisplayName(), component.deepCopy()})).withStyle(new ChatFormatting[]{ChatFormatting.GRAY, ChatFormatting.ITALIC}), false);
      }

      return collection.size();
   }
}
