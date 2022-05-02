package net.minecraft.world.entity;

import com.google.common.base.Predicates;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.world.Container;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.scores.Team;

public final class EntitySelector {
   public static final Predicate ENTITY_STILL_ALIVE = Entity::isAlive;
   public static final Predicate LIVING_ENTITY_STILL_ALIVE = LivingEntity::isAlive;
   public static final Predicate ENTITY_NOT_BEING_RIDDEN = (entity) -> {
      return entity.isAlive() && !entity.isVehicle() && !entity.isPassenger();
   };
   public static final Predicate CONTAINER_ENTITY_SELECTOR = (entity) -> {
      return entity instanceof Container && entity.isAlive();
   };
   public static final Predicate NO_CREATIVE_OR_SPECTATOR = (entity) -> {
      return !(entity instanceof Player) || !entity.isSpectator() && !((Player)entity).isCreative();
   };
   public static final Predicate NO_SPECTATORS = (entity) -> {
      return !entity.isSpectator();
   };

   public static Predicate withinDistance(double var0, double var2, double var4, double var6) {
      double var8 = var6 * var6;
      return (entity) -> {
         return entity != null && entity.distanceToSqr(var0, var2, var4) <= var8;
      };
   }

   public static Predicate pushableBy(Entity entity) {
      Team var1 = entity.getTeam();
      Team.CollisionRule var2 = var1 == null?Team.CollisionRule.ALWAYS:var1.getCollisionRule();
      return (Predicate)(var2 == Team.CollisionRule.NEVER?Predicates.alwaysFalse():NO_SPECTATORS.and((var3) -> {
         if(!var3.isPushable()) {
            return false;
         } else if(!entity.level.isClientSide || var3 instanceof Player && ((Player)var3).isLocalPlayer()) {
            Team team = var3.getTeam();
            Team.CollisionRule var5 = team == null?Team.CollisionRule.ALWAYS:team.getCollisionRule();
            if(var5 == Team.CollisionRule.NEVER) {
               return false;
            } else {
               boolean var6 = var1 != null && var1.isAlliedTo(team);
               return (var2 == Team.CollisionRule.PUSH_OWN_TEAM || var5 == Team.CollisionRule.PUSH_OWN_TEAM) && var6?false:var2 != Team.CollisionRule.PUSH_OTHER_TEAMS && var5 != Team.CollisionRule.PUSH_OTHER_TEAMS || var6;
            }
         } else {
            return false;
         }
      }));
   }

   public static Predicate notRiding(Entity entity) {
      return (var1) -> {
         while(true) {
            if(var1.isPassenger()) {
               var1 = var1.getVehicle();
               if(var1 != entity) {
                  continue;
               }

               return false;
            }

            return true;
         }
      };
   }

   public static class MobCanWearArmourEntitySelector implements Predicate {
      private final ItemStack itemStack;

      public MobCanWearArmourEntitySelector(ItemStack itemStack) {
         this.itemStack = itemStack;
      }

      public boolean test(@Nullable Entity entity) {
         if(!entity.isAlive()) {
            return false;
         } else if(!(entity instanceof LivingEntity)) {
            return false;
         } else {
            LivingEntity var2 = (LivingEntity)entity;
            return var2.canTakeItem(this.itemStack);
         }
      }

      // $FF: synthetic method
      public boolean test(@Nullable Object var1) {
         return this.test((Entity)var1);
      }
   }
}
