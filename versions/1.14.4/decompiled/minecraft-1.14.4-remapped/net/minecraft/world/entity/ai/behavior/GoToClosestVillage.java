package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.phys.Vec3;

public class GoToClosestVillage extends Behavior {
   private final float speed;
   private final int closeEnoughDistance;

   public GoToClosestVillage(float speed, int closeEnoughDistance) {
      super(ImmutableMap.of(MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT));
      this.speed = speed;
      this.closeEnoughDistance = closeEnoughDistance;
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      return !serverLevel.isVillage(new BlockPos(villager));
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      PoiManager var5 = serverLevel.getPoiManager();
      int var6 = var5.sectionsToVillage(SectionPos.of(new BlockPos(villager)));
      Vec3 var7 = null;

      for(int var8 = 0; var8 < 5; ++var8) {
         Vec3 var9 = RandomPos.getLandPos(villager, 15, 7, (blockPos) -> {
            return (double)(-serverLevel.sectionsToVillage(SectionPos.of(blockPos)));
         });
         if(var9 != null) {
            int var10 = var5.sectionsToVillage(SectionPos.of(new BlockPos(var9)));
            if(var10 < var6) {
               var7 = var9;
               break;
            }

            if(var10 == var6) {
               var7 = var9;
            }
         }
      }

      if(var7 != null) {
         villager.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var7, this.speed, this.closeEnoughDistance)));
      }

   }
}
