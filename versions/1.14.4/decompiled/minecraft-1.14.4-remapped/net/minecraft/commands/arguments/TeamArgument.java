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
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

public class TeamArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"foo", "123"});
   private static final DynamicCommandExceptionType ERROR_TEAM_NOT_FOUND = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("team.notFound", new Object[]{object});
   });

   public static TeamArgument team() {
      return new TeamArgument();
   }

   public static PlayerTeam getTeam(CommandContext commandContext, String string) throws CommandSyntaxException {
      String string = (String)commandContext.getArgument(string, String.class);
      Scoreboard var3 = ((CommandSourceStack)commandContext.getSource()).getServer().getScoreboard();
      PlayerTeam var4 = var3.getPlayerTeam(string);
      if(var4 == null) {
         throw ERROR_TEAM_NOT_FOUND.create(string);
      } else {
         return var4;
      }
   }

   public String parse(StringReader stringReader) throws CommandSyntaxException {
      return stringReader.readUnquotedString();
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return commandContext.getSource() instanceof SharedSuggestionProvider?SharedSuggestionProvider.suggest((Iterable)((SharedSuggestionProvider)commandContext.getSource()).getAllTeams(), suggestionsBuilder):Suggestions.empty();
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
