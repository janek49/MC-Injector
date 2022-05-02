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
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;

public class ParticleArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"foo", "foo:bar", "particle with options"});
   public static final DynamicCommandExceptionType ERROR_UNKNOWN_PARTICLE = new DynamicCommandExceptionType((object) -> {
      return new TranslatableComponent("particle.notFound", new Object[]{object});
   });

   public static ParticleArgument particle() {
      return new ParticleArgument();
   }

   public static ParticleOptions getParticle(CommandContext commandContext, String string) {
      return (ParticleOptions)commandContext.getArgument(string, ParticleOptions.class);
   }

   public ParticleOptions parse(StringReader stringReader) throws CommandSyntaxException {
      return readParticle(stringReader);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   public static ParticleOptions readParticle(StringReader stringReader) throws CommandSyntaxException {
      ResourceLocation var1 = ResourceLocation.read(stringReader);
      ParticleType<?> var2 = (ParticleType)Registry.PARTICLE_TYPE.getOptional(var1).orElseThrow(() -> {
         return ERROR_UNKNOWN_PARTICLE.create(var1);
      });
      return readParticle(stringReader, var2);
   }

   private static ParticleOptions readParticle(StringReader stringReader, ParticleType particleType) throws CommandSyntaxException {
      return particleType.getDeserializer().fromCommand(particleType, stringReader);
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggestResource((Iterable)Registry.PARTICLE_TYPE.keySet(), suggestionsBuilder);
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
