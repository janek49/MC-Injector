package net.minecraft.commands.arguments.blocks;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.arguments.blocks.BlockInput;
import net.minecraft.commands.arguments.blocks.BlockStateParser;

public class BlockStateArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"stone", "minecraft:stone", "stone[foo=bar]", "foo{bar=baz}"});

   public static BlockStateArgument block() {
      return new BlockStateArgument();
   }

   public BlockInput parse(StringReader stringReader) throws CommandSyntaxException {
      BlockStateParser var2 = (new BlockStateParser(stringReader, false)).parse(true);
      return new BlockInput(var2.getState(), var2.getProperties().keySet(), var2.getNbt());
   }

   public static BlockInput getBlock(CommandContext commandContext, String string) {
      return (BlockInput)commandContext.getArgument(string, BlockInput.class);
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      StringReader var3 = new StringReader(suggestionsBuilder.getInput());
      var3.setCursor(suggestionsBuilder.getStart());
      BlockStateParser var4 = new BlockStateParser(var3, false);

      try {
         var4.parse(true);
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
