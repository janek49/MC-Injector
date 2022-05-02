package net.minecraft.world.entity.ai.behavior;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.ToIntFunction;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.EntityPosWrapper;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.memory.WalkTarget;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.phys.Vec3;

public class PlayTagWithOtherKids extends Behavior {
   public PlayTagWithOtherKids() {
      super(ImmutableMap.of(MemoryModuleType.VISIBLE_VILLAGER_BABIES, MemoryStatus.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryStatus.VALUE_ABSENT, MemoryModuleType.LOOK_TARGET, MemoryStatus.REGISTERED, MemoryModuleType.INTERACTION_TARGET, MemoryStatus.REGISTERED));
   }

   protected boolean checkExtraStartConditions(ServerLevel serverLevel, PathfinderMob pathfinderMob) {
      return serverLevel.getRandom().nextInt(10) == 0 && this.hasFriendsNearby(pathfinderMob);
   }

   protected void start(ServerLevel serverLevel, PathfinderMob pathfinderMob, long var3) {
      LivingEntity var5 = this.seeIfSomeoneIsChasingMe(pathfinderMob);
      if(var5 != null) {
         this.fleeFromChaser(serverLevel, pathfinderMob, var5);
      } else {
         Optional<LivingEntity> var6 = this.findSomeoneBeingChased(pathfinderMob);
         if(var6.isPresent()) {
            chaseKid(pathfinderMob, (LivingEntity)var6.get());
         } else {
            this.findSomeoneToChase(pathfinderMob).ifPresent((livingEntity) -> {
               chaseKid(pathfinderMob, livingEntity);
            });
         }
      }
   }

   private void fleeFromChaser(ServerLevel serverLevel, PathfinderMob pathfinderMob, LivingEntity livingEntity) {
      for(int var4 = 0; var4 < 10; ++var4) {
         Vec3 var5 = RandomPos.getLandPos(pathfinderMob, 20, 8);
         if(var5 != null && serverLevel.isVillage(new BlockPos(var5))) {
            pathfinderMob.getBrain().setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(var5, 0.6F, 0)));
            return;
         }
      }

   }

   private static void chaseKid(PathfinderMob pathfinderMob, LivingEntity livingEntity) {
      Brain<?> var2 = pathfinderMob.getBrain();
      var2.setMemory(MemoryModuleType.INTERACTION_TARGET, (Object)livingEntity);
      var2.setMemory(MemoryModuleType.LOOK_TARGET, (Object)(new EntityPosWrapper(livingEntity)));
      var2.setMemory(MemoryModuleType.WALK_TARGET, (Object)(new WalkTarget(new EntityPosWrapper(livingEntity), 0.6F, 1)));
   }

   private Optional findSomeoneToChase(PathfinderMob pathfinderMob) {
      return this.getFriendsNearby(pathfinderMob).stream().findAny();
   }

   private Optional findSomeoneBeingChased(PathfinderMob pathfinderMob) {
      Map<LivingEntity, Integer> var2 = this.checkHowManyChasersEachFriendHas(pathfinderMob);
      return var2.entrySet().stream().sorted(Comparator.comparingInt(Entry::getValue)).filter((map$Entry) -> {
         return ((Integer)map$Entry.getValue()).intValue() > 0 && ((Integer)map$Entry.getValue()).intValue() <= 5;
      }).map(Entry::getKey).findFirst();
   }

   private Map checkHowManyChasersEachFriendHas(PathfinderMob pathfinderMob) {
      Map<LivingEntity, Integer> map = Maps.newHashMap();
      this.getFriendsNearby(pathfinderMob).stream().filter(this::isChasingSomeone).forEach((livingEntity) -> {
         Integer var10000 = (Integer)map.compute(this.whoAreYouChasing(livingEntity), (livingEntity, var1) -> {
            return Integer.valueOf(var1 == null?1:var1.intValue() + 1);
         });
      });
      return map;
   }

   private List getFriendsNearby(PathfinderMob pathfinderMob) {
      return (List)pathfinderMob.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get();
   }

   private LivingEntity whoAreYouChasing(LivingEntity livingEntity) {
      return (LivingEntity)livingEntity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).get();
   }

   @Nullable
   private LivingEntity seeIfSomeoneIsChasingMe(LivingEntity livingEntity) {
      return (LivingEntity)((List)livingEntity.getBrain().getMemory(MemoryModuleType.VISIBLE_VILLAGER_BABIES).get()).stream().filter((var2) -> {
         return this.isFriendChasingMe(livingEntity, var2);
      }).findAny().orElse((Object)null);
   }

   private boolean isChasingSomeone(LivingEntity livingEntity) {
      return livingEntity.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).isPresent();
   }

   private boolean isFriendChasingMe(LivingEntity var1, LivingEntity var2) {
      return var2.getBrain().getMemory(MemoryModuleType.INTERACTION_TARGET).filter((var1x) -> {
         return var1x == var1;
      }).isPresent();
   }

   private boolean hasFriendsNearby(PathfinderMob pathfinderMob) {
      return pathfinderMob.getBrain().hasMemoryValue(MemoryModuleType.VISIBLE_VILLAGER_BABIES);
   }
}
