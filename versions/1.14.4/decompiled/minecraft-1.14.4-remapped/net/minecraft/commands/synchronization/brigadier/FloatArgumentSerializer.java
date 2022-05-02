package net.minecraft.commands.synchronization.brigadier;

import com.google.gson.JsonObject;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.synchronization.ArgumentSerializer;
import net.minecraft.commands.synchronization.brigadier.BrigadierArgumentSerializers;
import net.minecraft.network.FriendlyByteBuf;

public class FloatArgumentSerializer implements ArgumentSerializer {
   public void serializeToNetwork(FloatArgumentType floatArgumentType, FriendlyByteBuf friendlyByteBuf) {
      boolean var3 = floatArgumentType.getMinimum() != -3.4028235E38F;
      boolean var4 = floatArgumentType.getMaximum() != Float.MAX_VALUE;
      friendlyByteBuf.writeByte(BrigadierArgumentSerializers.createNumberFlags(var3, var4));
      if(var3) {
         friendlyByteBuf.writeFloat(floatArgumentType.getMinimum());
      }

      if(var4) {
         friendlyByteBuf.writeFloat(floatArgumentType.getMaximum());
      }

   }

   public FloatArgumentType deserializeFromNetwork(FriendlyByteBuf friendlyByteBuf) {
      byte var2 = friendlyByteBuf.readByte();
      float var3 = BrigadierArgumentSerializers.numberHasMin(var2)?friendlyByteBuf.readFloat():-3.4028235E38F;
      float var4 = BrigadierArgumentSerializers.numberHasMax(var2)?friendlyByteBuf.readFloat():Float.MAX_VALUE;
      return FloatArgumentType.floatArg(var3, var4);
   }

   public void serializeToJson(FloatArgumentType floatArgumentType, JsonObject jsonObject) {
      if(floatArgumentType.getMinimum() != -3.4028235E38F) {
         jsonObject.addProperty("min", Float.valueOf(floatArgumentType.getMinimum()));
      }

      if(floatArgumentType.getMaximum() != Float.MAX_VALUE) {
         jsonObject.addProperty("max", Float.valueOf(floatArgumentType.getMaximum()));
      }

   }

   // $FF: synthetic method
   public ArgumentType deserializeFromNetwork(FriendlyByteBuf var1) {
      return this.deserializeFromNetwork(var1);
   }
}
