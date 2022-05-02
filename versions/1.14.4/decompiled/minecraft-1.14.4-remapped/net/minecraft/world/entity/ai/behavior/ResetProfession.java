package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerProfession;

public class ResetProfession extends Behavior {
   public ResetProfession() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_ABSENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      VillagerData var3 = villager.getVillagerData();
      return var3.getProfession() != VillagerProfession.NONE && var3.getProfession() != VillagerProfession.NITWIT && villager.getVillagerXp() == 0 && var3.getLevel() <= 1;
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      villager.setVillagerData(villager.getVillagerData().setProfession(VillagerProfession.NONE));
      villager.refreshBrain(serverLevel);
   }
}
