package net.minecraft.world.entity.projectile;

import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public final class ProjectileUtil {
   public static HitResult forwardsRaycast(Entity var0, boolean var1, boolean var2, @Nullable Entity var3, ClipContext.Block clipContext$Block) {
      return forwardsRaycast(var0, var1, var2, var3, clipContext$Block, true, (var2x) -> {
         return !var2x.isSpectator() && var2x.isPickable() && (var2 || !var2x.is(var3)) && !var2x.noPhysics;
      }, var0.getBoundingBox().expandTowards(var0.getDeltaMovement()).inflate(1.0D));
   }

   public static HitResult getHitResult(Entity entity, AABB aABB, Predicate predicate, ClipContext.Block clipContext$Block, boolean var4) {
      return forwardsRaycast(entity, var4, false, (Entity)null, clipContext$Block, false, predicate, aABB);
   }

   @Nullable
   public static EntityHitResult getHitResult(Level level, Entity entity, Vec3 var2, Vec3 var3, AABB aABB, Predicate predicate) {
      return getHitResult(level, entity, var2, var3, aABB, predicate, Double.MAX_VALUE);
   }

   private static HitResult forwardsRaycast(Entity var0, boolean var1, boolean var2, @Nullable Entity var3, ClipContext.Block clipContext$Block, boolean var5, Predicate predicate, AABB aABB) {
      double var8 = var0.x;
      double var10 = var0.y;
      double var12 = var0.z;
      Vec3 var14 = var0.getDeltaMovement();
      Level var15 = var0.level;
      Vec3 var16 = new Vec3(var8, var10, var12);
      if(var5 && !var15.noCollision(var0, var0.getBoundingBox(), (Set)(!var2 && var3 != null?getIgnoredEntities(var3):ImmutableSet.of()))) {
         return new BlockHitResult(var16, Direction.getNearest(var14.x, var14.y, var14.z), new BlockPos(var0), false);
      } else {
         Vec3 var17 = var16.add(var14);
         HitResult var18 = var15.clip(new ClipContext(var16, var17, clipContext$Block, ClipContext.Fluid.NONE, var0));
         if(var1) {
            if(var18.getType() != HitResult.Type.MISS) {
               var17 = var18.getLocation();
            }

            HitResult var19 = getHitResult(var15, var0, var16, var17, aABB, predicate);
            if(var19 != null) {
               var18 = var19;
            }
         }

         return var18;
      }
   }

   @Nullable
   public static EntityHitResult getEntityHitResult(Entity entity, Vec3 var1, Vec3 var2, AABB aABB, Predicate predicate, double var5) {
      Level var7 = entity.level;
      double var8 = var5;
      Entity var10 = null;
      Vec3 var11 = null;

      for(Entity var13 : var7.getEntities(entity, aABB, predicate)) {
         AABB var14 = var13.getBoundingBox().inflate((double)var13.getPickRadius());
         Optional<Vec3> var15 = var14.clip(var1, var2);
         if(var14.contains(var1)) {
            if(var8 >= 0.0D) {
               var10 = var13;
               var11 = (Vec3)var15.orElse(var1);
               var8 = 0.0D;
            }
         } else if(var15.isPresent()) {
            Vec3 var16 = (Vec3)var15.get();
            double var17 = var1.distanceToSqr(var16);
            if(var17 < var8 || var8 == 0.0D) {
               if(var13.getRootVehicle() == entity.getRootVehicle()) {
                  if(var8 == 0.0D) {
                     var10 = var13;
                     var11 = var16;
                  }
               } else {
                  var10 = var13;
                  var11 = var16;
                  var8 = var17;
               }
            }
         }
      }

      if(var10 == null) {
         return null;
      } else {
         return new EntityHitResult(var10, var11);
      }
   }

   @Nullable
   public static EntityHitResult getHitResult(Level level, Entity entity, Vec3 var2, Vec3 var3, AABB aABB, Predicate predicate, double var6) {
      double var8 = var6;
      Entity var10 = null;

      for(Entity var12 : level.getEntities(entity, aABB, predicate)) {
         AABB var13 = var12.getBoundingBox().inflate(0.30000001192092896D);
         Optional<Vec3> var14 = var13.clip(var2, var3);
         if(var14.isPresent()) {
            double var15 = var2.distanceToSqr((Vec3)var14.get());
            if(var15 < var8) {
               var10 = var12;
               var8 = var15;
            }
         }
      }

      if(var10 == null) {
         return null;
      } else {
         return new EntityHitResult(var10);
      }
   }

   private static Set getIgnoredEntities(Entity entity) {
      Entity entity = entity.getVehicle();
      return entity != null?ImmutableSet.of(entity, entity):ImmutableSet.of(entity);
   }

   public static final void rotateTowardsMovement(Entity entity, float var1) {
      Vec3 var2 = entity.getDeltaMovement();
      float var3 = Mth.sqrt(Entity.getHorizontalDistanceSqr(var2));
      entity.yRot = (float)(Mth.atan2(var2.z, var2.x) * 57.2957763671875D) + 90.0F;

      for(entity.xRot = (float)(Mth.atan2((double)var3, var2.y) * 57.2957763671875D) - 90.0F; entity.xRot - entity.xRotO < -180.0F; entity.xRotO -= 360.0F) {
         ;
      }

      while(entity.xRot - entity.xRotO >= 180.0F) {
         entity.xRotO += 360.0F;
      }

      while(entity.yRot - entity.yRotO < -180.0F) {
         entity.yRotO -= 360.0F;
      }

      while(entity.yRot - entity.yRotO >= 180.0F) {
         entity.yRotO += 360.0F;
      }

      entity.xRot = Mth.lerp(var1, entity.xRotO, entity.xRot);
      entity.yRot = Mth.lerp(var1, entity.yRotO, entity.yRot);
   }

   public static InteractionHand getWeaponHoldingHand(LivingEntity livingEntity, Item item) {
      return livingEntity.getMainHandItem().getItem() == item?InteractionHand.MAIN_HAND:InteractionHand.OFF_HAND;
   }

   public static AbstractArrow getMobArrow(LivingEntity livingEntity, ItemStack itemStack, float var2) {
      ArrowItem var3 = (ArrowItem)((ArrowItem)(itemStack.getItem() instanceof ArrowItem?itemStack.getItem():Items.ARROW));
      AbstractArrow var4 = var3.createArrow(livingEntity.level, itemStack, livingEntity);
      var4.setEnchantmentEffectsFromEntity(livingEntity, var2);
      if(itemStack.getItem() == Items.TIPPED_ARROW && var4 instanceof Arrow) {
         ((Arrow)var4).setEffectsFromItem(itemStack);
      }

      return var4;
   }
}
