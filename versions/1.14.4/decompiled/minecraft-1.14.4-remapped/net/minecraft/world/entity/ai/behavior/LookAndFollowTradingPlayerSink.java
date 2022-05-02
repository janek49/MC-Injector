package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityPosWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;

public class LookAndFollowTradingPlayerSink extends Behavior {
   private final float speed;

   public LookAndFollowTradingPlayerSink(float speed) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED), Integer.MAX_VALUE);
      this.speed = speed;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      Player var3 = villager.getTradingPlayer();
      return villager.isAlive() && var3 != null && !villager.isInWater() && !villager.hurtMarked && villager.distanceToSqr(var3) <= 16.0D && var3.containerMenu != null;
   }

   protected boolean canStillUse(ServerLevel serverLevel, Villager villager, long var3) {
      return this.checkExtraStartConditions(serverLevel, villager);
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      this.followPlayer(villager);
   }

   protected void stop(ServerLevel serverLevel, Villager villager, long var3) {
      Brain<?> var5 = villager.getBrain();
      var5.eraseMemory(MemoryModuleType.WALK_TARGET);
      var5.eraseMemory(MemoryModuleType.LOOK_TARGET);
   }

   protected void tick(ServerLevel serverLevel, Villager villager, long var3) {
      this.followPlayer(villager);
   }

   protected boolean timedOut(long l) {
      return false;
   }

   private void followPlayer(Villager villager) {
      EntityPosWrapper var2 = new EntityPosWrapper(villager.getTradingPlayer());
      Brain<?> var3 = villager.getBrain();
      var3.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var2, this.speed, 2)));
      var3.setMemory(MemoryModuleType.LOOK_TARGET, (Object)var2);
   }

   // $FF: synthetic method
   protected boolean canStillUse(ServerLevel var1, LivingEntity var2, long var3) {
      return this.canStillUse(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void stop(ServerLevel var1, LivingEntity var2, long var3) {
      this.stop(var1, (Villager)var2, var3);
   }

   // $FF: synthetic method
   protected void start(ServerLevel var1, LivingEntity var2, long var3) {
      this.start(var1, (Villager)var2, var3);
   }
}
