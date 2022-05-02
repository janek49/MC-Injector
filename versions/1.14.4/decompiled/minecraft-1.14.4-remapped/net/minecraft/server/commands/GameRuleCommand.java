package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import java.util.function.Predicate;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.GameRules;

public class GameRuleCommand {
   public static void register(CommandDispatcher commandDispatcher) {
      final LiteralArgumentBuilder<CommandSourceStack> var1 = (LiteralArgumentBuilder)Commands.literal("gamerule").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      });
      GameRules.visitGameRuleTypes(new GameRules.GameRuleTypeVisitor() {
         public void visit(GameRules.Key gameRules$Key, GameRules.Type gameRules$Type) {
            var1.then(((LiteralArgumentBuilder)Commands.literal(gameRules$Key.getId()).executes((commandContext) -> {
               return GameRuleCommand.queryRule((CommandSourceStack)commandContext.getSource(), gameRules$Key);
            })).then(gameRules$Type.createArgument("value").executes((commandContext) -> {
               return GameRuleCommand.setRule(commandContext, gameRules$Key);
            })));
         }
      });
      commandDispatcher.register(var1);
   }

   private static int setRule(CommandContext commandContext, GameRules.Key gameRules$Key) {
      CommandSourceStack var2 = (CommandSourceStack)commandContext.getSource();
      T var3 = var2.getServer().getGameRules().getRule(gameRules$Key);
      var3.setFromArgument(commandContext, "value");
      var2.sendSuccess(new TranslatableComponent("commands.gamerule.set", new Object[]{gameRules$Key.getId(), var3.toString()}), true);
      return var3.getCommandResult();
   }

   private static int queryRule(CommandSourceStack commandSourceStack, GameRules.Key gameRules$Key) {
      T var2 = commandSourceStack.getServer().getGameRules().getRule(gameRules$Key);
      commandSourceStack.sendSuccess(new TranslatableComponent("commands.gamerule.query", new Object[]{gameRules$Key.getId(), var2.toString()}), false);
      return var2.getCommandResult();
   }
}
