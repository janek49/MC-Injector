package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class ReloadCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("reload").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).executes((commandContext) -> {
         ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TranslatableComponent("commands.reload.success", new Object[0]), true);
         ((CommandSourceStack)commandContext.getSource()).getServer().reloadResources();
         return 0;
      }));
   }
}
