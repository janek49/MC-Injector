package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;

public class SetPlayerIdleTimeoutCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("setidletimeout").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(3);
      })).then(Commands.argument("minutes", IntegerArgumentType.integer(0)).executes((commandContext) -> {
         return setIdleTimeout((CommandSourceStack)commandContext.getSource(), IntegerArgumentType.getInteger(commandContext, "minutes"));
      })));
   }

   private static int setIdleTimeout(CommandSourceStack commandSourceStack, int var1) {
      commandSourceStack.getServer().setPlayerIdleTimeout(var1);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.setidletimeout.success", new Object[]{Integer.valueOf(var1)}), true);
      return var1;
   }
}
