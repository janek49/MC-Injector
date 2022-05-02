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
import net.minecraft.ChatFormatting;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

public class ColorArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"red", "green"});
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.color.invalid", new Object[]{object});
   });

   public static ColorArgument color() {
      return new ColorArgument();
   }

   public static ChatFormatting getColor(CommandContext commandContext, String string) {
      return (ChatFormatting)commandContext.getArgument(string, ChatFormatting.class);
   }

   public ChatFormatting parse(StringReader stringReader) throws CommandSyntaxException {
      String var2 = stringReader.readUnquotedString();
      ChatFormatting var3 = ChatFormatting.getByName(var2);
      if(var3 != null && !var3.isFormat()) {
         return var3;
      } else {
         throw ERROR_INVALID_VALUE.create(var2);
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggest((Iterable)ChatFormatting.getNames(true, false), suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
