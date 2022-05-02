package net.minecraft.commands.arguments;

import com.google.common.collect.Lists;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ObjectiveCriteriaArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"foo", "foo.bar.baz", "minecraft:foo"});
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.criteria.invalid", new Object[]{object});
   });

   public static ObjectiveCriteriaArgument criteria() {
      return new ObjectiveCriteriaArgument();
   }

   public static ObjectiveCriteria getCriteria(CommandContext commandContext, String string) {
      return (ObjectiveCriteria)commandContext.getArgument(string, ObjectiveCriteria.class);
   }

   public ObjectiveCriteria parse(StringReader stringReader) throws CommandSyntaxException {
      int var2 = stringReader.getCursor();

      while(stringReader.canRead() && stringReader.peek() != 32) {
         stringReader.skip();
      }

      String var3 = stringReader.getString().substring(var2, stringReader.getCursor());
      return (ObjectiveCriteria)ObjectiveCriteria.byName(var3).orElseThrow(() -> {
         stringReader.setCursor(var2);
         return ERROR_INVALID_VALUE.create(var3);
      });
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      List<String> var3 = Lists.newArrayList(ObjectiveCriteria.CRITERIA_BY_NAME.keySet());

      for(StatType<?> var5 : Registry.STAT_TYPE) {
         for(Object var7 : var5.getRegistry()) {
            String var8 = this.getName(var5, var7);
            var3.add(var8);
         }
      }

      return SharedSuggestionProvider.suggest((Iterable)var3, suggestionsBuilder);
   }

   public String getName(StatType statType, Object object) {
      return Stat.buildName(statType, object);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
