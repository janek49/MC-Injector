package net.minecraft.commands.arguments.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.commands.arguments.item.ItemParser;

public class ItemArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"stick", "minecraft:stick", "stick{foo=bar}"});

   public static ItemArgument item() {
      return new ItemArgument();
   }

   public ItemInput parse(StringReader stringReader) throws CommandSyntaxException {
      ItemParser var2 = (new ItemParser(stringReader, false)).parse();
      return new ItemInput(var2.getItem(), var2.getNbt());
   }

   public static ItemInput getItem(CommandContext commandContext, String string) {
      return (ItemInput)commandContext.getArgument(string, ItemInput.class);
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      StringReader var3 = new StringReader(suggestionsBuilder.getInput());
      var3.setCursor(suggestionsBuilder.getStart());
      ItemParser var4 = new ItemParser(var3, false);

      try {
         var4.parse();
      } catch (CommandSyntaxException var6) {
         ;
      }

      return var4.fillSuggestions(suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
