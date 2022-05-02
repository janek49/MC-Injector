package net.minecraft.server.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import java.util.Collection;
import java.util.function.Predicate;
import net.minecraft.commands.CommandFunction;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.item.FunctionArgument;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.ServerFunctionManager;

public class FunctionCommand {
   public static final SuggestionProvider SUGGEST_FUNCTION = (commandContext, suggestionsBuilder) -> {
      ServerFunctionManager var2 = ((CommandSourceStack)commandContext.getSource()).getServer().getFunctions();
      SharedSuggestionProvider.suggestResource(var2.getTags().getAvailableTags(), suggestionsBuilder, "#");
      return SharedSuggestionProvider.suggestResource((Iterable)var2.getFunctions().keySet(), suggestionsBuilder);
   };

   public static void register(CommandDispatcher commandDispatcher) {
      commandDispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)Commands.literal("function").requires((commandSourceStack) -> {
         return commandSourceStack.hasPermission(2);
      })).then(Commands.argument("name", FunctionArgument.functions()).suggests(SUGGEST_FUNCTION).executes((commandContext) -> {
         return runFunction((CommandSourceStack)commandContext.getSource(), FunctionArgument.getFunctions(commandContext, "name"));
      })));
   }

   private static int runFunction(CommandSourceStack commandSourceStack, Collection collection) {
      int var2 = 0;

      for(CommandFunction var4 : collection) {
         var2 += commandSourceStack.getServer().getFunctions().execute(var4, commandSourceStack.withSuppressedOutput().withMaximumPermission(2));
      }

      if(collection.size() == 1) {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.function.success.single", new Object[]{Integer.valueOf(var2), ((CommandFunction)collection.iterator().next()).getId()}), true);
      } else {
         commandSourceStack.sendSuccess(new TranslatableComponent("commands.function.success.multiple", new Object[]{Integer.valueOf(var2), Integer.valueOf(collection.size())}), true);
      }

      return var2;
   }
}
