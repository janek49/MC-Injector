package net.minecraft.commands.arguments.coordinates;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.coordinates.Coordinates;
import net.minecraft.commands.arguments.coordinates.LocalCoordinates;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.Vec3;

public class Vec3Argument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "0.1 -0.5 .9", "~0.5 ~1 ~-5"});
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos3d.incomplete", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_MIXED_TYPE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.mixed", new Object[0]));
   private final boolean centerCorrect;

   public Vec3Argument(boolean centerCorrect) {
      this.centerCorrect = centerCorrect;
   }

   public static Vec3Argument vec3() {
      return new Vec3Argument(true);
   }

   public static Vec3Argument vec3(boolean b) {
      return new Vec3Argument(b);
   }

   public static Vec3 getVec3(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((Coordinates)commandContext.getArgument(string, Coordinates.class)).getPosition((CommandSourceStack)commandContext.getSource());
   }

   public static Coordinates getCoordinates(CommandContext commandContext, String string) {
      return (Coordinates)commandContext.getArgument(string, Coordinates.class);
   }

   public Coordinates parse(StringReader stringReader) throws CommandSyntaxException {
      return (Coordinates)(stringReader.canRead() && stringReader.peek() == 94?LocalCoordinates.parse(stringReader):WorldCoordinates.parseDouble(stringReader, this.centerCorrect));
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      if(!(commandContext.getSource() instanceof SharedSuggestionProvider)) {
         return Suggestions.empty();
      } else {
         String var3 = suggestionsBuilder.getRemaining();
         Collection<SharedSuggestionProvider.TextCoordinates> var4;
         if(!var3.isEmpty() && var3.charAt(0) == 94) {
            var4 = Collections.singleton(SharedSuggestionProvider.TextCoordinates.DEFAULT_LOCAL);
         } else {
            var4 = ((SharedSuggestionProvider)commandContext.getSource()).getAbsoluteCoordinates();
         }

         return SharedSuggestionProvider.suggestCoordinates(var3, var4, suggestionsBuilder, Commands.createValidator(this::parse));
      }
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }
}
