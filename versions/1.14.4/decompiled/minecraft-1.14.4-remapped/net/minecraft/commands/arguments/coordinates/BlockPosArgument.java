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
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

public class BlockPosArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"0 0 0", "~ ~ ~", "^ ^ ^", "^1 ^ ^-5", "~0.5 ~1 ~-5"});
   public static final SimpleCommandExceptionType ERROR_NOT_LOADED = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.unloaded", new Object[0]));
   public static final SimpleCommandExceptionType ERROR_OUT_OF_WORLD = new SimpleCommandExceptionType(new TranslatableComponent("argument.pos.outofworld", new Object[0]));

   public static BlockPosArgument blockPos() {
      return new BlockPosArgument();
   }

   public static BlockPos getLoadedBlockPos(CommandContext commandContext, String string) throws CommandSyntaxException {
      BlockPos blockPos = ((Coordinates)commandContext.getArgument(string, Coordinates.class)).getBlockPos((CommandSourceStack)commandContext.getSource());
      if(!((CommandSourceStack)commandContext.getSource()).getLevel().hasChunkAt(blockPos)) {
         throw ERROR_NOT_LOADED.create();
      } else {
         ((CommandSourceStack)commandContext.getSource()).getLevel();
         if(!ServerLevel.isInWorldBounds(blockPos)) {
            throw ERROR_OUT_OF_WORLD.create();
         } else {
            return blockPos;
         }
      }
   }

   public static BlockPos getOrLoadBlockPos(CommandContext commandContext, String string) throws CommandSyntaxException {
      return ((Coordinates)commandContext.getArgument(string, Coordinates.class)).getBlockPos((CommandSourceStack)commandContext.getSource());
   }

   public Coordinates parse(StringReader stringReader) throws CommandSyntaxException {
      return (Coordinates)(stringReader.canRead() && stringReader.peek() == 94?LocalCoordinates.parse(stringReader):WorldCoordinates.parseInt(stringReader));
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
            var4 = ((SharedSuggestionProvider)commandContext.getSource()).getRelevantCoordinates();
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
