package net.minecraft.world.level.timers;

import net.minecraft.commands.CommandFunction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.ServerFunctionManager;
import net.minecraft.tags.Tag;
import net.minecraft.world.level.timers.TimerCallback;
import net.minecraft.world.level.timers.TimerQueue;

public class FunctionTagCallback implements TimerCallback {
   private final ResourceLocation tagId;

   public FunctionTagCallback(ResourceLocation tagId) {
      this.tagId = tagId;
   }

   public void handle(MinecraftServer minecraftServer, TimerQueue timerQueue, long var3) {
      ServerFunctionManager var5 = minecraftServer.getFunctions();
      Tag<CommandFunction> var6 = var5.getTags().getTagOrEmpty(this.tagId);

      for(CommandFunction var8 : var6.getValues()) {
         var5.execute(var8, var5.getGameLoopSender());
      }

   }

   public static class Serializer extends TimerCallback.Serializer {
      public Serializer() {
         super(new ResourceLocation("function_tag"), FunctionTagCallback.class);
      }

      public void serialize(CompoundTag compoundTag, FunctionTagCallback functionTagCallback) {
         compoundTag.putString("Name", functionTagCallback.tagId.toString());
      }

      public FunctionTagCallback deserialize(CompoundTag compoundTag) {
         ResourceLocation var2 = new ResourceLocation(compoundTag.getString("Name"));
         return new FunctionTagCallback(var2);
      }

      // $FF: synthetic method
      public TimerCallback deserialize(CompoundTag var1) {
         return this.deserialize(var1);
      }
   }
}
