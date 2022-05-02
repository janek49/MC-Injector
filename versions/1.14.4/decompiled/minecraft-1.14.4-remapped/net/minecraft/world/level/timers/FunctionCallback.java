package net.minecraft.world.level.timers;

import java.util.function.Consumer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class FunctionCallback implements TimerCallback {
   private final ResourceLocation functionId;

   public FunctionCallback(ResourceLocation functionId) {
      this.functionId = functionId;
   }

   public void handle(MinecraftServer minecraftServer, TimerQueue timerQueue, long var3) {
      ServerFunctionManager var5 = minecraftServer.getFunctions();
      var5.get(this.functionId).ifPresent((commandFunction) -> {
         var5.execute(commandFunction, var5.getGameLoopSender());
      });
   }

   public static class Serializer extends TimerCallback.Serializer {
      public Serializer() {
         super(new ResourceLocation("function"), FunctionCallback.class);
      }

      public void serialize(CompoundTag compoundTag, FunctionCallback functionCallback) {
         compoundTag.putString("Name", functionCallback.functionId.toString());
      }

      public FunctionCallback deserialize(CompoundTag compoundTag) {
         ResourceLocation var2 = new ResourceLocation(compoundTag.getString("Name"));
         return new FunctionCallback(var2);
      }

      // $FF: synthetic method
      public TimerCallback deserialize(CompoundTag var1) {
         return this.deserialize(var1);
      }
   }
}
