package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.core.GlobalPos;
import net.minecraft.core.Registry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class AssignProfessionFromJobSite extends Behavior {
   public AssignProfessionFromJobSite() {
      super(ImmutableMap.of(MemoryModuleType.JOB_SITE, MemoryStatus.VALUE_PRESENT));
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, Villager villager) {
      return villager.getVillagerData().getProfession() == VillagerProfession.NONE;
   }

   protected void start(ServerLevel serverLevel, Villager villager, long var3) {
      GlobalPos var5 = (GlobalPos)villager.getBrain().getMemory(MemoryModuleType.JOB_SITE).get();
      MinecraftServer var6 = serverLevel.getServer();
      var6.getLevel(var5.dimension()).getPoiManager().getType(var5.pos()).ifPresent((poiType) -> {
         Registry.VILLAGER_PROFESSION.stream().filter((villagerProfession) -> {
            return villagerProfession.getJobPoiType() == poiType;
         }).findFirst().ifPresent((villagerProfession) -> {
            villager.setVillagerData(villager.getVillagerData().setProfession(villagerProfession));
            villager.refreshBrain(serverLevel);
         });
      });
   }
}
