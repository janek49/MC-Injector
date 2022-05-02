package net.minecraft.commands.synchronization;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import java.util.function.Supplier;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.network.FriendlyByteBuf;

public class EmptyArgumentSerializer implements ArgumentSerializer {
   private final Supplier constructor;

   public EmptyArgumentSerializer(Supplier constructor) {
      this.constructor = constructor;
   }

   public void serializeToNetwork(ArgumentType argumentType, FriendlyByteBuf friendlyByteBuf) {
   }

   public ArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      return (ArgumentType)this.constructor.get();
   }

   public void serializeToJson(ArgumentType argumentType, JsonObject jsonObject) {
   }
}
