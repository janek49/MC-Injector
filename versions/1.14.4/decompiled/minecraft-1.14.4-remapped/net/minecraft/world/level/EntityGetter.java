package net.minecraft.world.level;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public interface EntityGetter {
   List getEntities(@Nullable Entity var1, AABB var2, @Nullable Predicate var3);

   List getEntitiesOfClass(Class var1, AABB var2, @Nullable Predicate var3);

   default List getLoadedEntitiesOfClass(Class class, AABB aABB, @Nullable Predicate predicate) {
      return this.getEntitiesOfClass(class, aABB, predicate);
   }

   List players();

   default List getEntities(@Nullable Entity entity, AABB aABB) {
      return this.getEntities(entity, aABB, EntitySelector.NO_SPECTATORS);
   }

   default boolean isUnobstructed(@Nullable Entity entity, VoxelShape voxelShape) {
      return voxelShape.isEmpty()?true:this.getEntities(entity, voxelShape.bounds()).stream().filter((var1) -> {
         return !var1.removed && var1.blocksBuilding && (entity == null || !var1.isPassengerOfSameVehicle(entity));
      }).noneMatch((entity) -> {
         return Shapes.joinIsNotEmpty(voxelShape, Shapes.create(entity.getBoundingBox()), BooleanOp.AND);
      });
   }

   default List getEntitiesOfClass(Class class, AABB aABB) {
      return this.getEntitiesOfClass(class, aABB, EntitySelector.NO_SPECTATORS);
   }

   default List getLoadedEntitiesOfClass(Class class, AABB aABB) {
      return this.getLoadedEntitiesOfClass(class, aABB, EntitySelector.NO_SPECTATORS);
   }

   default Stream getEntityCollisions(@Nullable Entity entity, AABB aABB, Set set) {
      if(aABB.getSize() < 1.0E-7D) {
         return Stream.empty();
      } else {
         AABB aABB = aABB.inflate(1.0E-7D);
         Stream var10000 = this.getEntities(entity, aABB).stream().filter((entity) -> {
            return !set.contains(entity);
         }).filter((var1) -> {
            return entity == null || !entity.isPassengerOfSameVehicle(var1);
         }).flatMap((var1) -> {
            return Stream.of(new AABB[]{var1.getCollideBox(), entity == null?null:entity.getCollideAgainstBox(var1)});
         }).filter(Objects::nonNull);
         aABB.getClass();
         return var10000.filter(aABB::intersects).map(Shapes::create);
      }
   }

   @Nullable
   default Player getNearestPlayer(double var1, double var3, double var5, double var7, @Nullable Predicate predicate) {
      double var10 = -1.0D;
      Player var12 = null;

      for(Player var14 : this.players()) {
         if(predicate == null || predicate.test(var14)) {
            double var15 = var14.distanceToSqr(var1, var3, var5);
            if((var7 < 0.0D || var15 < var7 * var7) && (var10 == -1.0D || var15 < var10)) {
               var10 = var15;
               var12 = var14;
            }
         }
      }

      return var12;
   }

   @Nullable
   default Player getNearestPlayer(Entity entity, double var2) {
      return this.getNearestPlayer(entity.x, entity.y, entity.z, var2, false);
   }

   @Nullable
   default Player getNearestPlayer(double var1, double var3, double var5, double var7, boolean var9) {
      Predicate<Entity> var10 = var9?EntitySelector.NO_CREATIVE_OR_SPECTATOR:EntitySelector.NO_SPECTATORS;
      return this.getNearestPlayer(var1, var3, var5, var7, var10);
   }

   @Nullable
   default Player getNearestPlayerIgnoreY(double var1, double var3, double var5) {
      double var7 = -1.0D;
      Player var9 = null;

      for(Player var11 : this.players()) {
         if(EntitySelector.NO_SPECTATORS.test(var11)) {
            double var12 = var11.distanceToSqr(var1, var11.y, var3);
            if((var5 < 0.0D || var12 < var5 * var5) && (var7 == -1.0D || var12 < var7)) {
               var7 = var12;
               var9 = var11;
            }
         }
      }

      return var9;
   }

   default boolean hasNearbyAlivePlayer(double var1, double var3, double var5, double var7) {
      for(Player var10 : this.players()) {
         if(EntitySelector.NO_SPECTATORS.test(var10) && EntitySelector.LIVING_ENTITY_STILL_ALIVE.test(var10)) {
            double var11 = var10.distanceToSqr(var1, var3, var5);
            if(var7 < 0.0D || var11 < var7 * var7) {
               return true;
            }
         }
      }

      return false;
   }

   @Nullable
   default Player getNearestPlayer(TargetingConditions targetingConditions, LivingEntity livingEntity) {
      return (Player)this.getNearestEntity(this.players(), targetingConditions, livingEntity, livingEntity.x, livingEntity.y, livingEntity.z);
   }

   @Nullable
   default Player getNearestPlayer(TargetingConditions targetingConditions, LivingEntity livingEntity, double var3, double var5, double var7) {
      return (Player)this.getNearestEntity(this.players(), targetingConditions, livingEntity, var3, var5, var7);
   }

   @Nullable
   default Player getNearestPlayer(TargetingConditions targetingConditions, double var2, double var4, double var6) {
      return (Player)this.getNearestEntity(this.players(), targetingConditions, (LivingEntity)null, var2, var4, var6);
   }

   @Nullable
   default LivingEntity getNearestEntity(Class class, TargetingConditions targetingConditions, @Nullable LivingEntity var3, double var4, double var6, double var8, AABB aABB) {
      return this.getNearestEntity(this.getEntitiesOfClass(class, aABB, (Predicate)null), targetingConditions, var3, var4, var6, var8);
   }

   @Nullable
   default LivingEntity getNearestLoadedEntity(Class class, TargetingConditions targetingConditions, @Nullable LivingEntity var3, double var4, double var6, double var8, AABB aABB) {
      return this.getNearestEntity(this.getLoadedEntitiesOfClass(class, aABB, (Predicate)null), targetingConditions, var3, var4, var6, var8);
   }

   @Nullable
   default LivingEntity getNearestEntity(List list, TargetingConditions targetingConditions, @Nullable LivingEntity var3, double var4, double var6, double var8) {
      double var10 = -1.0D;
      T var12 = null;

      for(T var14 : list) {
         if(targetingConditions.test(var3, var14)) {
            double var15 = var14.distanceToSqr(var4, var6, var8);
            if(var10 == -1.0D || var15 < var10) {
               var10 = var15;
               var12 = var14;
            }
         }
      }

      return var12;
   }

   default List getNearbyPlayers(TargetingConditions targetingConditions, LivingEntity livingEntity, AABB aABB) {
      List<Player> list = Lists.newArrayList();

      for(Player var6 : this.players()) {
         if(aABB.contains(var6.x, var6.y, var6.z) && targetingConditions.test(livingEntity, var6)) {
            list.add(var6);
         }
      }

      return list;
   }

   default List getNearbyEntities(Class class, TargetingConditions targetingConditions, LivingEntity livingEntity, AABB aABB) {
      List<T> list = this.getEntitiesOfClass(class, aABB, (Predicate)null);
      List<T> var6 = Lists.newArrayList();

      for(T var8 : list) {
         if(targetingConditions.test(livingEntity, var8)) {
            var6.add(var8);
         }
      }

      return var6;
   }

   @Nullable
   default Player getPlayerByUUID(UUID uUID) {
      for(int var2 = 0; var2 < this.players().size(); ++var2) {
         Player var3 = (Player)this.players().get(var2);
         if(uUID.equals(var3.getUUID())) {
            return var3;
         }
      }

      return null;
   }
}
