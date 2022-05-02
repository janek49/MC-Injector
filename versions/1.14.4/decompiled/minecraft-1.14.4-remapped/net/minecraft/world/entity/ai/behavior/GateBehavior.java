package net.minecraft.world.entity.ai.behavior;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.WeightedList;

public class GateBehavior extends Behavior {
   private final Set exitErasedMemories;
   private final GateBehavior.OrderPolicy orderPolicy;
   private final GateBehavior.RunningPolicy runningPolicy;
   private final WeightedList behaviors = new WeightedList();

   public GateBehavior(Map map, Set exitErasedMemories, GateBehavior.OrderPolicy orderPolicy, GateBehavior.RunningPolicy runningPolicy, List list) {
      super(map);
      this.exitErasedMemories = exitErasedMemories;
      this.orderPolicy = orderPolicy;
      this.runningPolicy = runningPolicy;
      list.forEach((pair) -> {
         this.behaviors.add(pair.getFirst(), ((Integer)pair.getSecond()).intValue());
      });
   }

   protected boolean canStillUse(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      return this.behaviors.stream().filter((behavior) -> {
         return behavior.getStatus() == Behavior.Status.RUNNING;
      }).anyMatch((behavior) -> {
         return behavior.canStillUse(serverLevel, livingEntity, var3);
      });
   }

   protected boolean timedOut(long l) {
      return false;
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      this.orderPolicy.apply(this.behaviors);
      this.runningPolicy.apply(this.behaviors, serverLevel, livingEntity, var3);
   }

   protected void tick(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      this.behaviors.stream().filter((behavior) -> {
         return behavior.getStatus() == Behavior.Status.RUNNING;
      }).forEach((behavior) -> {
         behavior.tickOrStop(serverLevel, livingEntity, var3);
      });
   }

   protected void stop(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      this.behaviors.stream().filter((behavior) -> {
         return behavior.getStatus() == Behavior.Status.RUNNING;
      }).forEach((behavior) -> {
         behavior.doStop(serverLevel, livingEntity, var3);
      });
      Set var10000 = this.exitErasedMemories;
      Brain var10001 = livingEntity.getBrain();
      var10000.forEach(var10001::eraseMemory);
   }

   public String toString() {
      Set<? extends Behavior<? super E>> var1 = (Set)this.behaviors.stream().filter((behavior) -> {
         return behavior.getStatus() == Behavior.Status.RUNNING;
      }).collect(Collectors.toSet());
      return "(" + this.getClass().getSimpleName() + "): " + var1;
   }

   static enum OrderPolicy {
      ORDERED((weightedList) -> {
      }),
      SHUFFLED(WeightedList::shuffle);

      private final Consumer consumer;

      private OrderPolicy(Consumer consumer) {
         this.consumer = consumer;
      }

      public void apply(WeightedList weightedList) {
         this.consumer.accept(weightedList);
      }
   }

   static enum RunningPolicy {
      RUN_ONE {
         public void apply(WeightedList weightedList, ServerLevel serverLevel, LivingEntity livingEntity, long var4) {
            weightedList.stream().filter((behavior) -> {
               return behavior.getStatus() == Behavior.Status.STOPPED;
            }).filter((behavior) -> {
               return behavior.tryStart(serverLevel, livingEntity, var4);
            }).findFirst();
         }
      },
      TRY_ALL {
         public void apply(WeightedList weightedList, ServerLevel serverLevel, LivingEntity livingEntity, long var4) {
            weightedList.stream().filter((behavior) -> {
               return behavior.getStatus() == Behavior.Status.STOPPED;
            }).forEach((behavior) -> {
               behavior.tryStart(serverLevel, livingEntity, var4);
            });
         }
      };

      private RunningPolicy() {
      }

      public abstract void apply(WeightedList var1, ServerLevel var2, LivingEntity var3, long var4);
   }
}
