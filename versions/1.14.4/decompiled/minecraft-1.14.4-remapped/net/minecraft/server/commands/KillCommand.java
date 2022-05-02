package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;

public class KillCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("kill").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("targets", EntityArgument.entities()).executes((commandContext) -> {
         return kill((CommandSourceStack)commandContext.getSource(), EntityArgument.getEntities(commandContext, "targets"));
      })));
   }

   private static int kill(CommandSourceStack commandSourceStack, Collection collection) {
      for(Entity var3 : collection) {
         var3.kill();
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.kill.success.single", new Object[]{((Entity)collection.iterator().next()).getDisplayName()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.kill.success.multiple", new Object[]{Integer.valueOf(collection.size())}), true);
      }

      return collection.size();
   }
}
