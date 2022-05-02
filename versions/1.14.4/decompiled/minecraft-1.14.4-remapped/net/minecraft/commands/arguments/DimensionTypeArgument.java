package net.minecraft.commands.arguments;

import com.google.common.collect.Streams;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.dimension.DimensionType;

public class DimensionTypeArgument implements ArgumentType {
   private static final Collection EXAMPLES = (Collection)Stream.of(new DimensionType[]{DimensionType.OVERWORLD, DimensionType.NETHER}).map((dimensionType) -> {
      return DimensionType.getName(dimensionType).toString();
   }).collect(Collectors.toList());
   public static final DynamicCommandExceptionType ERROR_INVALID_VALUE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("argument.dimension.invalid", new Object[]{object});
   });

   public DimensionType parse(StringReader stringReader) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(stringReader);
      return (DimensionType)Registry.DIMENSION_TYPE.getOptional(var2).orElseThrow(() -> {
         return ERROR_INVALID_VALUE.create(var2);
      });
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggestResource(Streams.stream(DimensionType.getAllTypes()).map(DimensionType::getName), suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static DimensionTypeArgument dimension() {
      return new DimensionTypeArgument();
   }

   public static DimensionType getDimension(CommandContext commandContext, String string) {
      return (DimensionType)commandContext.getArgument(string, DimensionType.class);
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
