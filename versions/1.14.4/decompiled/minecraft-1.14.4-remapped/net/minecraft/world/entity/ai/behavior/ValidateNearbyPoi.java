package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.state.BlockState;

public class ValidateNearbyPoi extends Behavior {
   private final MemoryModuleType memoryType;
   private final Predicate poiPredicate;

   public ValidateNearbyPoi(PoiType poiType, MemoryModuleType memoryType) {
      super(ImmutableMap.of(memoryType, MemoryStatus.VALUE_PRESENT));
      this.poiPredicate = poiType.getPredicate();
      this.memoryType = memoryType;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, LivingEntity livingEntity) {
      GlobalPos var3 = (GlobalPos)livingEntity.getBrain().getMemory(this.memoryType).get();
      return Objects.equals(serverLevel.getDimension().getType(), var3.dimension()) && var3.pos().closerThan(livingEntity.position(), 5.0D);
   }

   protected void start(ServerLevel serverLevel, LivingEntity livingEntity, long var3) {
      Brain<?> var5 = livingEntity.getBrain();
      GlobalPos var6 = (GlobalPos)var5.getMemory(this.memoryType).get();
      ServerLevel var7 = serverLevel.getServer().getLevel(var6.dimension());
      if(this.poiDoesntExist(var7, var6.pos()) || this.bedIsOccupied(var7, var6.pos(), livingEntity)) {
         var5.eraseMemory(this.memoryType);
      }

   }

   private boolean bedIsOccupied(ServerLevel serverLevel, BlockPos blockPos, LivingEntity livingEntity) {
      BlockState var4 = serverLevel.getBlockState(blockPos);
      return var4.getBlock().is(BlockTags.BEDS) && ((Boolean)var4.getValue(BedBlock.OCCUPIED)).booleanValue() && !livingEntity.isSleeping();
   }

   private boolean poiDoesntExist(ServerLevel serverLevel, BlockPos blockPos) {
      return !serverLevel.getPoiManager().exists(blockPos, this.poiPredicate);
   }
}
