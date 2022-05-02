package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.function.Predicate;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.behavior.AcquirePoi;
import net.minecraft.world.entity.ai.behavior.AssignProfessionFromJobSite;
import net.minecraft.world.entity.ai.behavior.Celebrate;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.GateBehavior;
import net.minecraft.world.entity.ai.behavior.GiveGiftToHero;
import net.minecraft.world.entity.ai.behavior.GoOutsideToCelebrate;
import net.minecraft.world.entity.ai.behavior.GoToClosestVillage;
import net.minecraft.world.entity.ai.behavior.HarvestFarmland;
import net.minecraft.world.entity.ai.behavior.InsideBrownianWalk;
import net.minecraft.world.entity.ai.behavior.InteractWith;
import net.minecraft.world.entity.ai.behavior.InteractWithDoor;
import net.minecraft.world.entity.ai.behavior.JumpOnBed;
import net.minecraft.world.entity.ai.behavior.LocateHidingPlace;
import net.minecraft.world.entity.ai.behavior.LocateHidingPlaceDuringRaid;
import net.minecraft.world.entity.ai.behavior.LookAndFollowTradingPlayerSink;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MakeLove;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PickUpItems;
import net.minecraft.world.entity.ai.behavior.PlayTagWithOtherKids;
import net.minecraft.world.entity.ai.behavior.ReactToBell;
import net.minecraft.world.entity.ai.behavior.ResetProfession;
import net.minecraft.world.entity.ai.behavior.ResetRaidStatus;
import net.minecraft.world.entity.ai.behavior.RingBell;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetClosestHomeAsWalkTarget;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetHiddenState;
import net.minecraft.world.entity.ai.behavior.SetLookAndInteract;
import net.minecraft.world.entity.ai.behavior.SetRaidStatus;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFromEntity;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromBlockMemory;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.ShowTradesToPlayer;
import net.minecraft.world.entity.ai.behavior.SleepInBed;
import net.minecraft.world.entity.ai.behavior.SocializeAtBell;
import net.minecraft.world.entity.ai.behavior.StrollAroundPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoi;
import net.minecraft.world.entity.ai.behavior.StrollToPoiList;
import net.minecraft.world.entity.ai.behavior.Swim;
import net.minecraft.world.entity.ai.behavior.TradeWithVillager;
import net.minecraft.world.entity.ai.behavior.UpdateActivityFromSchedule;
import net.minecraft.world.entity.ai.behavior.ValidateNearbyPoi;
import net.minecraft.world.entity.ai.behavior.VictoryStroll;
import net.minecraft.world.entity.ai.behavior.VillageBoundRandomStroll;
import net.minecraft.world.entity.ai.behavior.VillagerCalmDown;
import net.minecraft.world.entity.ai.behavior.VillagerPanicTrigger;
import net.minecraft.world.entity.ai.behavior.WakeUp;
import net.minecraft.world.entity.ai.behavior.WorkAtPoi;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerProfession;

public class VillagerGoalPackages {
   public static ImmutableList getCorePackage(VillagerProfession villagerProfession, float var1) {
      return ImmutableList.of(Pair.of(Integer.valueOf(0), new Swim(0.4F, 0.8F)), Pair.of(Integer.valueOf(0), new InteractWithDoor()), Pair.of(Integer.valueOf(0), new LookAtTargetSink(45, 90)), Pair.of(Integer.valueOf(0), new VillagerPanicTrigger()), Pair.of(Integer.valueOf(0), new WakeUp()), Pair.of(Integer.valueOf(0), new ReactToBell()), Pair.of(Integer.valueOf(0), new SetRaidStatus()), Pair.of(Integer.valueOf(1), new MoveToTargetSink(200)), Pair.of(Integer.valueOf(2), new LookAndFollowTradingPlayerSink(var1)), Pair.of(Integer.valueOf(5), new PickUpItems()), Pair.of(Integer.valueOf(10), new AcquirePoi(villagerProfession.getJobPoiType(), MemoryModuleType.JOB_SITE, true)), Pair.of(Integer.valueOf(10), new AcquirePoi(PoiType.HOME, MemoryModuleType.HOME, false)), new Pair[]{Pair.of(Integer.valueOf(10), new AcquirePoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT, true)), Pair.of(Integer.valueOf(10), new AssignProfessionFromJobSite()), Pair.of(Integer.valueOf(10), new ResetProfession())});
   }

   public static ImmutableList getWorkPackage(VillagerProfession villagerProfession, float var1) {
      return ImmutableList.of(getMinimalLookBehavior(), Pair.of(Integer.valueOf(5), new RunOne(ImmutableList.of(Pair.of(new WorkAtPoi(), Integer.valueOf(7)), Pair.of(new StrollAroundPoi(MemoryModuleType.JOB_SITE, 4), Integer.valueOf(2)), Pair.of(new StrollToPoi(MemoryModuleType.JOB_SITE, 1, 10), Integer.valueOf(5)), Pair.of(new StrollToPoiList(MemoryModuleType.SECONDARY_JOB_SITE, 0.4F, 1, 6, MemoryModuleType.JOB_SITE), Integer.valueOf(5)), Pair.of(new HarvestFarmland(), Integer.valueOf(villagerProfession == VillagerProfession.FARMER?2:5))))), Pair.of(Integer.valueOf(10), new ShowTradesToPlayer(400, 1600)), Pair.of(Integer.valueOf(10), new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(Integer.valueOf(2), new SetWalkTargetFromBlockMemory(MemoryModuleType.JOB_SITE, var1, 9, 100, 1200)), Pair.of(Integer.valueOf(3), new GiveGiftToHero(100)), Pair.of(Integer.valueOf(3), new ValidateNearbyPoi(villagerProfession.getJobPoiType(), MemoryModuleType.JOB_SITE)), Pair.of(Integer.valueOf(99), new UpdateActivityFromSchedule()));
   }

   public static ImmutableList getPlayPackage(float f) {
      return ImmutableList.of(Pair.of(Integer.valueOf(0), new MoveToTargetSink(100)), getFullLookBehavior(), Pair.of(Integer.valueOf(5), new PlayTagWithOtherKids()), Pair.of(Integer.valueOf(5), new RunOne(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), Integer.valueOf(2)), Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, f, 2), Integer.valueOf(1)), Pair.of(new VillageBoundRandomStroll(f), Integer.valueOf(1)), Pair.of(new SetWalkTargetFromLookTarget(f, 2), Integer.valueOf(1)), Pair.of(new JumpOnBed(f), Integer.valueOf(2)), Pair.of(new DoNothing(20, 40), Integer.valueOf(2))))), Pair.of(Integer.valueOf(99), new UpdateActivityFromSchedule()));
   }

   public static ImmutableList getRestPackage(VillagerProfession villagerProfession, float var1) {
      return ImmutableList.of(Pair.of(Integer.valueOf(2), new SetWalkTargetFromBlockMemory(MemoryModuleType.HOME, var1, 1, 150, 1200)), Pair.of(Integer.valueOf(3), new ValidateNearbyPoi(PoiType.HOME, MemoryModuleType.HOME)), Pair.of(Integer.valueOf(3), new SleepInBed()), Pair.of(Integer.valueOf(5), new RunOne(ImmutableMap.of(MemoryModuleType.HOME, MemoryStatus.VALUE_ABSENT), ImmutableList.of(Pair.of(new SetClosestHomeAsWalkTarget(var1), Integer.valueOf(1)), Pair.of(new InsideBrownianWalk(var1), Integer.valueOf(4)), Pair.of(new GoToClosestVillage(var1, 4), Integer.valueOf(2)), Pair.of(new DoNothing(20, 40), Integer.valueOf(2))))), getMinimalLookBehavior(), Pair.of(Integer.valueOf(99), new UpdateActivityFromSchedule()));
   }

   public static ImmutableList getMeetPackage(VillagerProfession villagerProfession, float var1) {
      return ImmutableList.of(Pair.of(Integer.valueOf(2), new RunOne(ImmutableList.of(Pair.of(new StrollAroundPoi(MemoryModuleType.MEETING_POINT, 40), Integer.valueOf(2)), Pair.of(new SocializeAtBell(), Integer.valueOf(2))))), Pair.of(Integer.valueOf(10), new ShowTradesToPlayer(400, 1600)), Pair.of(Integer.valueOf(10), new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(Integer.valueOf(2), new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, var1, 6, 100, 200)), Pair.of(Integer.valueOf(3), new GiveGiftToHero(100)), Pair.of(Integer.valueOf(3), new ValidateNearbyPoi(PoiType.MEETING, MemoryModuleType.MEETING_POINT)), Pair.of(Integer.valueOf(3), new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new TradeWithVillager(), Integer.valueOf(1))))), getFullLookBehavior(), Pair.of(Integer.valueOf(99), new UpdateActivityFromSchedule()));
   }

   public static ImmutableList getIdlePackage(VillagerProfession villagerProfession, float var1) {
      return ImmutableList.of(Pair.of(Integer.valueOf(2), new RunOne(ImmutableList.of(Pair.of(InteractWith.of(EntityType.VILLAGER, 8, MemoryModuleType.INTERACTION_TARGET, var1, 2), Integer.valueOf(2)), Pair.of(new InteractWith(EntityType.VILLAGER, 8, Villager::canBreed, Villager::canBreed, MemoryModuleType.BREED_TARGET, var1, 2), Integer.valueOf(1)), Pair.of(InteractWith.of(EntityType.CAT, 8, MemoryModuleType.INTERACTION_TARGET, var1, 2), Integer.valueOf(1)), Pair.of(new VillageBoundRandomStroll(var1), Integer.valueOf(1)), Pair.of(new SetWalkTargetFromLookTarget(var1, 2), Integer.valueOf(1)), Pair.of(new JumpOnBed(var1), Integer.valueOf(1)), Pair.of(new DoNothing(30, 60), Integer.valueOf(1))))), Pair.of(Integer.valueOf(3), new GiveGiftToHero(100)), Pair.of(Integer.valueOf(3), new SetLookAndInteract(EntityType.PLAYER, 4)), Pair.of(Integer.valueOf(3), new ShowTradesToPlayer(400, 1600)), Pair.of(Integer.valueOf(3), new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.INTERACTION_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new TradeWithVillager(), Integer.valueOf(1))))), Pair.of(Integer.valueOf(3), new GateBehavior(ImmutableMap.of(), ImmutableSet.of(MemoryModuleType.BREED_TARGET), GateBehavior.OrderPolicy.ORDERED, GateBehavior.RunningPolicy.RUN_ONE, ImmutableList.of(Pair.of(new MakeLove(), Integer.valueOf(1))))), getFullLookBehavior(), Pair.of(Integer.valueOf(99), new UpdateActivityFromSchedule()));
   }

   public static ImmutableList getPanicPackage(VillagerProfession villagerProfession, float var1) {
      float var2 = var1 * 1.5F;
      return ImmutableList.of(Pair.of(Integer.valueOf(0), new VillagerCalmDown()), Pair.of(Integer.valueOf(1), new SetWalkTargetAwayFromEntity(MemoryModuleType.NEAREST_HOSTILE, var2)), Pair.of(Integer.valueOf(1), new SetWalkTargetAwayFromEntity(MemoryModuleType.HURT_BY_ENTITY, var2)), Pair.of(Integer.valueOf(3), new VillageBoundRandomStroll(var2, 2, 2)), getMinimalLookBehavior());
   }

   public static ImmutableList getPreRaidPackage(VillagerProfession villagerProfession, float var1) {
      return ImmutableList.of(Pair.of(Integer.valueOf(0), new RingBell()), Pair.of(Integer.valueOf(0), new RunOne(ImmutableList.of(Pair.of(new SetWalkTargetFromBlockMemory(MemoryModuleType.MEETING_POINT, var1 * 1.5F, 2, 150, 200), Integer.valueOf(6)), Pair.of(new VillageBoundRandomStroll(var1 * 1.5F), Integer.valueOf(2))))), getMinimalLookBehavior(), Pair.of(Integer.valueOf(99), new ResetRaidStatus()));
   }

   public static ImmutableList getRaidPackage(VillagerProfession villagerProfession, float var1) {
      return ImmutableList.of(Pair.of(Integer.valueOf(0), new RunOne(ImmutableList.of(Pair.of(new GoOutsideToCelebrate(var1), Integer.valueOf(5)), Pair.of(new VictoryStroll(var1 * 1.1F), Integer.valueOf(2))))), Pair.of(Integer.valueOf(0), new Celebrate(600, 600)), Pair.of(Integer.valueOf(2), new LocateHidingPlaceDuringRaid(24, var1 * 1.4F)), getMinimalLookBehavior(), Pair.of(Integer.valueOf(99), new ResetRaidStatus()));
   }

   public static ImmutableList getHidePackage(VillagerProfession villagerProfession, float var1) {
      int var2 = 2;
      return ImmutableList.of(Pair.of(Integer.valueOf(0), new SetHiddenState(15, 2)), Pair.of(Integer.valueOf(1), new LocateHidingPlace(32, var1 * 1.25F, 2)), getMinimalLookBehavior());
   }

   private static Pair getFullLookBehavior() {
      return Pair.of(Integer.valueOf(5), new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.CAT, 8.0F), Integer.valueOf(8)), Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), Integer.valueOf(2)), Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), Integer.valueOf(2)), Pair.of(new SetEntityLookTarget(MobCategory.CREATURE, 8.0F), Integer.valueOf(1)), Pair.of(new SetEntityLookTarget(MobCategory.WATER_CREATURE, 8.0F), Integer.valueOf(1)), Pair.of(new SetEntityLookTarget(MobCategory.MONSTER, 8.0F), Integer.valueOf(1)), Pair.of(new DoNothing(30, 60), Integer.valueOf(2)))));
   }

   private static Pair getMinimalLookBehavior() {
      return Pair.of(Integer.valueOf(5), new RunOne(ImmutableList.of(Pair.of(new SetEntityLookTarget(EntityType.VILLAGER, 8.0F), Integer.valueOf(2)), Pair.of(new SetEntityLookTarget(EntityType.PLAYER, 8.0F), Integer.valueOf(2)), Pair.of(new DoNothing(30, 60), Integer.valueOf(8)))));
   }
}
