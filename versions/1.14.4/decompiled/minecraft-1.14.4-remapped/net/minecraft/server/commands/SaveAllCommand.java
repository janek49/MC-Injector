package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;

public class SaveAllCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.save.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("save-all").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(4);
      })).executes((commandContext) -> {
         return saveAll((CommandSourceStack)commandContext.getSource(), false);
      })).then(Commands.literal("flush").executes((commandContext) -> {
         return saveAll((CommandSourceStack)commandContext.getSource(), true);
      })));
   }

   private static int saveAll(CommandSourceStack commandSourceStack, boolean var1) throws CommandSyntaxException {
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.save.saving", new Object[0]), false);
      MinecraftServer var2 = commandSourceStack.getServer();
      var2.getPlayerList().saveAll();
      boolean var3 = var2.saveAllChunks(true, var1, true);
      if(!var3) {
         throw ERROR_FAILED.create();
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.save.success", new Object[0]), true);
         return 1;
      }
   }
}
