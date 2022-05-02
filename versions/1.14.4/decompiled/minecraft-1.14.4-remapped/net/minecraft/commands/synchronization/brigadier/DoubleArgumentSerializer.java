package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;

public class DoubleArgumentSerializer implements ArgumentSerializer {
   public void serializeToNetwork(DoubleArgumentType doubleArgumentType, FriendlyByteBuf friendlyByteBuf) {
      boolean var3 = doubleArgumentType.getMinimum() != -1.7976931348623157E308D;
      boolean var4 = doubleArgumentType.getMaximum() != Double.MAX_VALUE;
      friendlyByteBuf.writeByte(BrigadierArgumentSerializers.createNumberFlags(var3, var4));
      if(var3) {
         friendlyByteBuf.writeDouble(doubleArgumentType.getMinimum());
      }

      if(var4) {
         friendlyByteBuf.writeDouble(doubleArgumentType.getMaximum());
      }

   }

   public DoubleArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      byte var2 = friendlyByteBuf.readByte();
      double var3 = BrigadierArgumentSerializers.numberHasMin(var2)?friendlyByteBuf.readDouble():-1.7976931348623157E308D;
      double var5 = BrigadierArgumentSerializers.numberHasMax(var2)?friendlyByteBuf.readDouble():Double.MAX_VALUE;
      return DoubleArgumentType.doubleArg(var3, var5);
   }

   public void serializeToJson(DoubleArgumentType doubleArgumentType, JsonObject jsonObject) {
      if(doubleArgumentType.getMinimum() != -1.7976931348623157E308D) {
         jsonObject.addProperty("min", Double.valueOf(doubleArgumentType.getMinimum()));
      }

      if(doubleArgumentType.getMaximum() != Double.MAX_VALUE) {
         jsonObject.addProperty("max", Double.valueOf(doubleArgumentType.getMaximum()));
      }

   }

   // $FF: synthetic method
   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }
}
