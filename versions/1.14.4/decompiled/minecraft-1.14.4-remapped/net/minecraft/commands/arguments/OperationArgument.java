package net.minecraft.commands.arguments;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import java.util.Arrays;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.scores.Score;

public class OperationArgument implements ArgumentType {
   private static final Collection EXAMPLES = Arrays.asList(new String[]{"=", ">", "<"});
   private static final SimpleCommandExceptionType ERROR_INVALID_OPERATION = new SimpleCommandExceptionType(new TranslatableComponent("arguments.operation.invalid", new Object[0]));
   private static final SimpleCommandExceptionType ERROR_DIVIDE_BY_ZERO = new SimpleCommandExceptionType(new TranslatableComponent("arguments.operation.div0", new Object[0]));

   public static OperationArgument operation() {
      return new OperationArgument();
   }

   public static OperationArgument.Operation getOperation(CommandContext commandContext, String string) throws CommandSyntaxException {
      return (OperationArgument.Operation)commandContext.getArgument(string, OperationArgument.Operation.class);
   }

   public OperationArgument.Operation parse(StringReader stringReader) throws CommandSyntaxException {
      if(!stringReader.canRead()) {
         throw ERROR_INVALID_OPERATION.create();
      } else {
         int var2 = stringReader.getCursor();

         while(stringReader.canRead() && stringReader.peek() != 32) {
            stringReader.skip();
         }

         return getOperation(stringReader.getString().substring(var2, stringReader.getCursor()));
      }
   }

   public CompletableFuture listSuggestions(CommandContext commandContext, SuggestionsBuilder suggestionsBuilder) {
      return SharedSuggestionProvider.suggest(new String[]{"=", "+=", "-=", "*=", "/=", "%=", "<", ">", "><"}, suggestionsBuilder);
   }

   public Collection getExamples() {
      return EXAMPLES;
   }

   private static OperationArgument.Operation getOperation(String string) throws CommandSyntaxException {
      return (OperationArgument.Operation)(string.equals("><")?(var0, var1) -> {
         int var2 = var0.getScore();
         var0.setScore(var1.getScore());
         var1.setScore(var2);
      }:getSimpleOperation(string));
   }

   private static OperationArgument.SimpleOperation getSimpleOperation(String string) throws CommandSyntaxException {
      byte var2 = -1;
      switch(string.hashCode()) {
      case 60:
         if(string.equals("<")) {
            var2 = 6;
         }
         break;
      case 61:
         if(string.equals("=")) {
            var2 = 0;
         }
         break;
      case 62:
         if(string.equals(">")) {
            var2 = 7;
         }
         break;
      case 1208:
         if(string.equals("%=")) {
            var2 = 5;
         }
         break;
      case 1363:
         if(string.equals("*=")) {
            var2 = 3;
         }
         break;
      case 1394:
         if(string.equals("+=")) {
            var2 = 1;
         }
         break;
      case 1456:
         if(string.equals("-=")) {
            var2 = 2;
         }
         break;
      case 1518:
         if(string.equals("/=")) {
            var2 = 4;
         }
      }

      switch(var2) {
      case 0:
         return (var0, var1) -> {
            return var1;
         };
      case 1:
         return (var0, var1) -> {
            return var0 + var1;
         };
      case 2:
         return (var0, var1) -> {
            return var0 - var1;
         };
      case 3:
         return (var0, var1) -> {
            return var0 * var1;
         };
      case 4:
         return (var0, var1) -> {
            if(var1 == 0) {
               throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
               return Mth.intFloorDiv(var0, var1);
            }
         };
      case 5:
         return (var0, var1) -> {
            if(var1 == 0) {
               throw ERROR_DIVIDE_BY_ZERO.create();
            } else {
               return Mth.positiveModulo(var0, var1);
            }
         };
      case 6:
         return Math::min;
      case 7:
         return Math::max;
      default:
         throw ERROR_INVALID_OPERATION.create();
      }
   }

   // $FF: synthetic method
   public Object parse(StringReader var1) throws CommandSyntaxException {
      return this.parse(var1);
   }

   @FunctionalInterface
   public interface Operation {
      void apply(Score var1, Score var2) throws CommandSyntaxException;
   }

   @FunctionalInterface
   interface SimpleOperation extends OperationArgument.Operation {
      int apply(int var1, int var2) throws CommandSyntaxException;

      default void apply(Score var1, Score var2) throws CommandSyntaxException {
         var1.setScore(this.apply(var1.getScore(), var2.getScore()));
      }
   }
}
