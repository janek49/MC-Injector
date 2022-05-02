package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.Objective;
import net.minecraft.world.scores.Scoreboard;

public class ObjectiveArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"foo", "*", "012"});
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("arguments.objective.notFound", new Object[]{object});
   });
   private static final DynamicCommandExceptionType ERROR_OBJECTIVE_READ_ONLY = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("arguments.objective.readonly", new Object[]{object});
   });
   public static final DynamicCommandExceptionType ERROR_OBJECTIVE_NAME_TOO_LONG = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("commands.scoreboard.objectives.add.longName", new Object[]{object});
   });

   public static ObjectiveArgument objective() {
      return new ObjectiveArgument();
   }

   public static Objective getObjective(CommandContext commandContext, String string) throws CommandSyntaxException {
      String string = (String)commandContext.getArgument(string, String.class);
      Scoreboard var3 = ((CommandSourceStack)commandContext.getSource()).getServer().getScoreboard();
      Objective var4 = var3.getObjective(string);
      if(var4 == null) {
         throw ERROR_OBJECTIVE_NOT_FOUND.create(string);
      } else {
         return var4;
      }
   }

   public static Objective getWritableObjective(CommandContext commandContext, String string) throws CommandSyntaxException {
      Objective objective = getObjective(commandContext, string);
      if(objective.getCriteria().isReadOnly()) {
         throw ERROR_OBJECTIVE_READ_ONLY.create(objective.getName());
      } else {
         return objective;
      }
   }

   public String parse(StringReader stringReader) throws CommandSyntaxException {
      String string = stringReader.readUnquotedString();
      if(string.length() > 16) {
         throw ERROR_OBJECTIVE_NAME_TOO_LONG.create(Integer.valueOf(16));
      } else {
         return string;
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      if(commandContext.getSource() instanceof CommandSourceStack) {
         return SharedSuggestionProvider.suggest((Iterable)((CommandSourceStack)commandContext.getSource()).getServer().getScoreboard().getObjectiveNames(), suggestionsBuilder);
      } else if(commandContext.getSource() instanceof SharedSuggestionProvider) {
         SharedSuggestionProvider var3 = (SharedSuggestionProvider)commandContext.getSource();
         return var3.customSuggestion(commandContext, suggestionsBuilder);
      } else {
         return Suggestions.empty();
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
