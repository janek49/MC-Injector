package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;

public class TimeArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"0d", "0s", "0t", "0"});
   private static final SimpleCommandExceptionType ERROR_INVALID_UNIT = new SimpleCommandExceptionType(new TranslatableComponent("argument.time.invalid_unit", new Object[0]));
   private static final DynamicCommandExceptionType ERROR_INVALID_TICK_COUNT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.time.invalid_tick_count", new Object[]{object});
   });
   private static final Object2IntMap UNITS = new Object2IntOpenHashMap();

   public static TimeArgument time() {
      return new TimeArgument();
   }

   public Integer parse(StringReader stringReader) throws CommandSyntaxException {
      float var2 = stringReader.readFloat();
      String var3 = stringReader.readUnquotedString();
      int var4 = UNITS.getOrDefault(var3, 0);
      if(var4 == 0) {
         throw ERROR_INVALID_UNIT.create();
      } else {
         int var5 = Math.round(var2 * (float)var4);
         if(var5 < 0) {
            throw ERROR_INVALID_TICK_COUNT.create(Integer.valueOf(var5));
         } else {
            return Integer.valueOf(var5);
         }
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      StringReader var3 = new StringReader(suggestionsBuilder.getRemaining());

      try {
         var3.readFloat();
      } catch (CommandSyntaxException var5) {
         return suggestionsBuilder.buildFuture();
      }

      return SharedSuggestionProvider.suggest((Iterable)UNITS.keySet(), suggestionsBuilder.createOffset(suggestionsBuilder.getStart() + var3.getCursor()));
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   static {
      UNITS.put("d", 24000);
      UNITS.put("s", 20);
      UNITS.put("t", 1);
      UNITS.put("", 1);
   }
}
