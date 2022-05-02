package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.scores.Scoreboard;

public class ScoreboardSlotArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"sidebar", "foo.bar"});
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.scoreboardDisplaySlot.invalid", new Object[]{object});
   });

   public static ScoreboardSlotArgument displaySlot() {
      return new ScoreboardSlotArgument();
   }

   public static int getDisplaySlot(CommandContext commandContext, String string) {
      return ((Integer)commandContext.getArgument(string, Integer.class)).intValue();
   }

   public Integer parse(StringReader stringReader) throws CommandSyntaxException {
      String var2 = stringReader.readUnquotedString();
      int var3 = Scoreboard.getDisplaySlotByName(var2);
      if(var3 == -1) {
         throw ERROR_INVALID_VALUE.create(var2);
      } else {
         return Integer.valueOf(var3);
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggest(Scoreboard.getDisplaySlotNames(), suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
