package net.minecraft.world.entity.ai.sensing;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import java.util.List;
import java.util.Set;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.level.dimension.DimensionType;

public class SecondaryPoiSensor extends Sensor {
   public SecondaryPoiSensor() {
      super(40);
   }

   protected void doTick(ServerLevel serverLevel, Villager villager) {
      DimensionType var3 = serverLevel.getDimension().getType();
      BlockPos var4 = new BlockPos(villager);
      List<GlobalPos> var5 = Lists.newArrayList();
      int var6 = 4;

      for(int var7 = -4; var7 <= 4; ++var7) {
         for(int var8 = -2; var8 <= 2; ++var8) {
            for(int var9 = -4; var9 <= 4; ++var9) {
               BlockPos var10 = var4.offset(var7, var8, var9);
               if(villager.getVillagerData().getProfession().getSecondaryPoi().contains(serverLevel.getBlockState(var10).getBlock())) {
                  var5.add(GlobalPos.of(var3, var10));
               }
            }
         }
      }

      Brain<?> var7 = villager.getBrain();
      if(!var5.isEmpty()) {
         var7.setMemory(MemoryModuleType.SECONDARY_JOB_SITE, (Object)var5);
      } else {
         var7.eraseMemory(MemoryModuleType.SECONDARY_JOB_SITE);
      }

   }

   public Set requires() {
      return ImmutableSet.of(MemoryModuleType.SECONDARY_JOB_SITE);
   }
}
