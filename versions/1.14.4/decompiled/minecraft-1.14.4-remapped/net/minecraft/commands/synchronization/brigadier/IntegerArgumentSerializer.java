package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;

public class IntegerArgumentSerializer implements ArgumentSerializer {
   public void serializeToNetwork(IntegerArgumentType integerArgumentType, FriendlyByteBuf friendlyByteBuf) {
      boolean var3 = integerArgumentType.getMinimum() != Integer.MIN_VALUE;
      boolean var4 = integerArgumentType.getMaximum() != Integer.MAX_VALUE;
      friendlyByteBuf.writeByte(BrigadierArgumentSerializers.createNumberFlags(var3, var4));
      if(var3) {
         friendlyByteBuf.writeInt(integerArgumentType.getMinimum());
      }

      if(var4) {
         friendlyByteBuf.writeInt(integerArgumentType.getMaximum());
      }

   }

   public IntegerArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      byte var2 = friendlyByteBuf.readByte();
      int var3 = BrigadierArgumentSerializers.numberHasMin(var2)?friendlyByteBuf.readInt():Integer.MIN_VALUE;
      int var4 = BrigadierArgumentSerializers.numberHasMax(var2)?friendlyByteBuf.readInt():Integer.MAX_VALUE;
      return IntegerArgumentType.integer(var3, var4);
   }

   public void serializeToJson(IntegerArgumentType integerArgumentType, JsonObject jsonObject) {
      if(integerArgumentType.getMinimum() != Integer.MIN_VALUE) {
         jsonObject.addProperty("min", Integer.valueOf(integerArgumentType.getMinimum()));
      }

      if(integerArgumentType.getMaximum() != Integer.MAX_VALUE) {
         jsonObject.addProperty("max", Integer.valueOf(integerArgumentType.getMaximum()));
      }

   }

   // $FF: synthetic method
   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }
}
