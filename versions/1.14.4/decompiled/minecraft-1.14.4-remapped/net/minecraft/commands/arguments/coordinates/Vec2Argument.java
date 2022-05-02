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
import net.minecraft.commands.arguments.coordinates.WorldCoordinate;
import net.minecraft.commands.arguments.coordinates.WorldCoordinates;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

public class Vec2Argument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"0 0", "~ ~", "0.1 -0.5", "~1 ~-2"});
   public static final SimpleCommandExceptionType ERROR_NOT_COMPLETE = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos2d.incomplete", new Object[0]));
   private final boolean centerCorrect;

   public Vec2Argument(boolean centerCorrect) {
      this.centerCorrect = centerCorrect;
   }

   public static Vec2Argument vec2() {
      return new Vec2Argument(true);
   }

   public static Vec2 getVec2(CommandContext commandContext, String string) throws CommandSyntaxException {
      Vec3 var2 = ((Coordinates)commandContext.getArgument(string, Coordinates.class)).getPosition((CommandSourceStack)commandContext.getSource());
      return new Vec2((float)var2.x, (float)var2.z);
   }

   public Coordinates parse(StringReader stringReader) throws CommandSyntaxException {
      int var2 = stringReader.getCursor();
      if(!stringReader.canRead()) {
         throw ERROR_NOT_COMPLETE.createWithContext(stringReader);
      } else {
         WorldCoordinate var3 = WorldCoordinate.parseDouble(stringReader, this.centerCorrect);
         if(stringReader.canRead() && stringReader.peek() == 32) {
            stringReader.skip();
            WorldCoordinate var4 = WorldCoordinate.parseDouble(stringReader, this.centerCorrect);
            return new WorldCoordinates(var3, new WorldCoordinate(true, 0.0D), var4);
         } else {
            stringReader.setCursor(var2);
            throw ERROR_NOT_COMPLETE.createWithContext(stringReader);
         }
      }
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

         return SharedSuggestionProvider.suggest2DCoordinates(var3, var4, suggestionsBuilder, Commands.createValidator(this::parse));
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
