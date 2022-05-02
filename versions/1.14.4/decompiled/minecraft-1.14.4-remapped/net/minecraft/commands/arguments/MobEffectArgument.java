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
import java.util.function.Supplier;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

public class MobEffectArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"spooky", "effect"});
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_EFFECT = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("effect.effectNotFound", new Object[]{object});
   });

   public static MobEffectArgument effect() {
      return new MobEffectArgument();
   }

   public static MobEffect getEffect(CommandContext commandContext, String string) throws CommandSyntaxException {
      return (MobEffect)commandContext.getArgument(string, MobEffect.class);
   }

   public MobEffect parse(StringReader stringReader) throws CommandSyntaxException {
      ResourceLocation var2 = ResourceLocation.read(stringReader);
      return (MobEffect)Registry.MOB_EFFECT.getOptional(var2).orElseThrow(() -> {
         return ERROR_UNKNOWN_EFFECT.create(var2);
      });
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggestResource((Iterable)Registry.MOB_EFFECT.keySet(), suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
