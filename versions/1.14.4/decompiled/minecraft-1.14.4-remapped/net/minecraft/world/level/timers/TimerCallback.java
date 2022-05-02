package net.minecraft.world.level.timers;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.timers.TimerQueue;

@FunctionalInterface
public interface TimerCallback {
   void handle(Object var1, TimerQueue var2, long var3);

   public abstract static class Serializer {
      private final ResourceLocation id;
      private final Class cls;

      public Serializer(ResourceLocation id, Class cls) {
         this.id = id;
         this.cls = cls;
      }

      public ResourceLocation getId() {
         return this.id;
      }

      public Class getCls() {
         return this.cls;
      }

      public abstract void serialize(CompoundTag var1, TimerCallback var2);

      public abstract TimerCallback deserialize(CompoundTag var1);
   }
}
