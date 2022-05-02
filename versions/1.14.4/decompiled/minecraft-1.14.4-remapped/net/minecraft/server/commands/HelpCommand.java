package net.minecraft.server.commands;

import com.google.common.collect.Iterables;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.ParseResults;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.ParsedCommandNode;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.tree.CommandNode;
import java.util.Map;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

public class HelpCommand {
   private static final SimpleCommandExceptionType ERROR_FAILED = new SimpleCommandExceptionType(new TranslatableComponent("commands.help.failed", new Object[0]));

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("help").executes((commandContext) -> {
         Map<CommandNode<CommandSourceStack>, String> var2 = commandDispatcher.getSmartUsage(commandDispatcher.getRoot(), commandContext.getSource());

         for(String var4 : var2.values()) {
            ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TextComponent("/" + var4), false);
         }

         return var2.size();
      })).then(Commands.argument("command", StringArgumentType.greedyString()).executes((commandContext) -> {
         ParseResults<CommandSourceStack> var2 = commandDispatcher.parse(StringArgumentType.getString(commandContext, "command"), commandContext.getSource());
         if(var2.getContext().getNodes().isEmpty()) {
            throw ERROR_FAILED.create();
         } else {
            Map<CommandNode<CommandSourceStack>, String> var3 = commandDispatcher.getSmartUsage(((ParsedCommandNode)Iterables.getLast(var2.getContext().getNodes())).getNode(), commandContext.getSource());

            for(String var5 : var3.values()) {
               ((CommandSourceStack)commandContext.getSource()).sendSuccess(new TextComponent("/" + var2.getReader().getString() + " " + var5), false);
            }

            return var3.size();
         }
      })));
   }
}
