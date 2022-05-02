package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.LongArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;

public class LongArgumentSerializer implements ArgumentSerializer {
   public void serializeToNetwork(LongArgumentType longArgumentType, FriendlyByteBuf friendlyByteBuf) {
      boolean var3 = longArgumentType.getMinimum() != Long.MIN_VALUE;
      boolean var4 = longArgumentType.getMaximum() != Long.MAX_VALUE;
      friendlyByteBuf.writeByte(BrigadierArgumentSerializers.createNumberFlags(var3, var4));
      if(var3) {
         friendlyByteBuf.writeLong(longArgumentType.getMinimum());
      }

      if(var4) {
         friendlyByteBuf.writeLong(longArgumentType.getMaximum());
      }

   }

   public LongArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      byte var2 = friendlyByteBuf.readByte();
      long var3 = BrigadierArgumentSerializers.numberHasMin(var2)?friendlyByteBuf.readLong():Long.MIN_VALUE;
      long var5 = BrigadierArgumentSerializers.numberHasMax(var2)?friendlyByteBuf.readLong():Long.MAX_VALUE;
      return LongArgumentType.longArg(var3, var5);
   }

   public void serializeToJson(LongArgumentType longArgumentType, JsonObject jsonObject) {
      if(longArgumentType.getMinimum() != Long.MIN_VALUE) {
         jsonObject.addProperty("min", Long.valueOf(longArgumentType.getMinimum()));
      }

      if(longArgumentType.getMaximum() != Long.MAX_VALUE) {
         jsonObject.addProperty("max", Long.valueOf(longArgumentType.getMaximum()));
      }

   }

   // $FF: synthetic method
   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }
}
