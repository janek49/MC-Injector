package net.minecraft.commands.arguments;

import com.google.gson.JsonObject;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Arrays;
import java.util.Collection;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public interface RangeArgument extends ArgumentType {
   static default RangeArgument.Ints intRange() {
      return new RangeArgument.Ints();
   }

   public static class Floats implements RangeArgument {
      private static final Collection EXAMPLES = Arrays.asList(new String[]{"0..5.2", "0", "-5.4", "-100.76..", "..100"});

      public MinMaxBounds.Floats parse(StringReader stringReader) throws CommandSyntaxException {
         return MinMaxBounds.Floats.fromReader(stringReader);
      }

      public Collection getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }

      public static class Serializer extends RangeArgument.Serializer {
         public RangeArgument.Floats deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
            return new RangeArgument.Floats();
         }

         // $FF: synthetic method
         public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
            return this.deserializeFromNetwork(var1);
         }
      }
   }

   public static class Ints implements RangeArgument {
      private static final Collection EXAMPLES = Arrays.asList(new String[]{"0..5", "0", "-5", "-100..", "..100"});

      public static MinMaxBounds.Ints getRange(CommandContext commandContext, String string) {
         return (MinMaxBounds.Ints)commandContext.getArgument(string, MinMaxBounds.Ints.class);
      }

      public MinMaxBounds.Ints parse(StringReader stringReader) throws CommandSyntaxException {
         return MinMaxBounds.Ints.fromReader(stringReader);
      }

      public Collection getExamples() {
         return EXAMPLES;
      }

      // $FF: synthetic method
      public Object parse(StringReader var1) throws CommandSyntaxException {
         return this.parse(var1);
      }

      public static class Serializer extends RangeArgument.Serializer {
         public RangeArgument.Ints deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
            return new RangeArgument.Ints();
         }

         // $FF: synthetic method
         public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
            return this.deserializeFromNetwork(var1);
         }
      }
   }

   public abstract static class Serializer implements ArgumentSerializer {
      public void serializeToNetwork(RangeArgument rangeArgument, FriendlyByteBuf friendlyByteBuf) {
      }

      public void serializeToJson(RangeArgument rangeArgument, JsonObject jsonObject) {
      }
   }
}
