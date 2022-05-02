package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType.StringType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class StringArgumentSerializer implements ArgumentSerializer {
   public void serializeToNetwork(StringArgumentType stringArgumentType, FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeEnum(stringArgumentType.getType());
   }

   public StringArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      StringType var2 = (StringType)friendlyByteBuf.readEnum(StringType.class);
      switch(var2) {
      case SINGLE_WORD:
         return StringArgumentType.word();
      case QUOTABLE_PHRASE:
         return StringArgumentType.string();
      case GREEDY_PHRASE:
      default:
         return StringArgumentType.greedyString();
      }
   }

   public void serializeToJson(StringArgumentType stringArgumentType, JsonObject jsonObject) {
      switch(stringArgumentType.getType()) {
      case SINGLE_WORD:
         jsonObject.addProperty("type", "word");
         break;
      case QUOTABLE_PHRASE:
         jsonObject.addProperty("type", "phrase");
         break;
      case GREEDY_PHRASE:
      default:
         jsonObject.addProperty("type", "greedy");
      }

   }

   // $FF: synthetic method
   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }
}
