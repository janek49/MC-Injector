package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class SleepInBed extends Behavior {
   private long nextOkStartTime;

   public SleepInBed() {
      super(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      if(livingEntity.isPassenger()) {
         return false;
      } else {
         GlobalPos var3 = (GlobalPos)livingEntity.getBrain().getMemory(MemoryModuleType.HOME).get();
         if(!Objects.equals(serverLevel.getDimension().getType(), var3.dimension())) {
            return false;
         } else {
            BlockState var4 = serverLevel.getBlockState(var3.pos());
            return var3.pos().closerThan(livingEntity.position(), 2.0D) && var4.getBlock().is(BlockTags.BEDS) && !((Boolean)var4.getValue(BedBlock.OCCUPIED)).booleanValue();
         }
      }
   }

   protected boolean canStillUse(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Optional<GlobalPos> var5 = livingEntity.getBrain().getMemory(MemoryModuleType.HOME);
      if(!var5.isPresent()) {
         return false;
      } else {
         BlockPos var6 = ((GlobalPos)var5.get()).pos();
         return livingEntity.getBrain().isActive(Activity.REST) && livingEntity.y > (double)var6.getY() + 0.4D && var6.closerThan(livingEntity.position(), 1.14D);
      }
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      if(var3 > this.nextOkStartTime) {
         livingEntity.getBrain().getMemory(MemoryModuleType.OPENED_DOORS).ifPresent((set) -> {
            InteractWithDoor.closeAllOpenedDoors(serverLevel, ImmutableList.of(), 0, livingEntity, livingEntity.getBrain());
         });
         livingEntity.startSleeping(((GlobalPos)livingEntity.getBrain().getMemory(MemoryModuleType.HOME).get()).pos());
      }

   }

   protected boolean timedOut(long l) {
      return false;
   }

   protected void stop(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      if(livingEntity.isSleeping()) {
         livingEntity.stopSleeping();
         this.nextOkStartTime = var3 + 40L;
      }

   }
}
