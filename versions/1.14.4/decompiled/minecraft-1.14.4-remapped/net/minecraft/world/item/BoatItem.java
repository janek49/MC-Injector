package net.minecraft.world.item;

import java.util.List;
import java.util.function.Predicate;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.Boat;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public class BoatItem extends Item {
   private static final Predicate ENTITY_PREDICATE = EntitySelector.NO_SPECTATORS.and(Entity::isPickable);
   private final Boat.Type type;

   public BoatItem(Boat.Type type, Item.Properties item$Properties) {
      super(item$Properties);
      this.type = type;
   }

   public InteractionResultHolder use(Level level, Player player, InteractionHand interactionHand) {
      ItemStack var4 = player.getItemInHand(interactionHand);
      HitResult var5 = getPlayerPOVHitResult(level, player, ClipContext.Fluid.ANY);
      if(var5.getType() == HitResult.Type.MISS) {
         return new InteractionResultHolder(InteractionResult.PASS, var4);
      } else {
         Vec3 var6 = player.getViewVector(1.0F);
         double var7 = 5.0D;
         List<Entity> var9 = level.getEntities((Entity)player, player.getBoundingBox().expandTowards(var6.scale(5.0D)).inflate(1.0D), ENTITY_PREDICATE);
         if(!var9.isEmpty()) {
            Vec3 var10 = player.getEyePosition(1.0F);

            for(Entity var12 : var9) {
               AABB var13 = var12.getBoundingBox().inflate((double)var12.getPickRadius());
               if(var13.contains(var10)) {
                  return new InteractionResultHolder(InteractionResult.PASS, var4);
               }
            }
         }

         if(var5.getType() == HitResult.Type.BLOCK) {
            Boat var10 = new Boat(level, var5.getLocation().x, var5.getLocation().y, var5.getLocation().z);
            var10.setType(this.type);
            var10.yRot = player.yRot;
            if(!level.noCollision(var10, var10.getBoundingBox().inflate(-0.1D))) {
               return new InteractionResultHolder(InteractionResult.FAIL, var4);
            } else {
               if(!level.isClientSide) {
                  level.addFreshEntity(var10);
               }

               if(!player.abilities.instabuild) {
                  var4.shrink(1);
               }

               player.awardStat(Stats.ITEM_USED.get(this));
               return new InteractionResultHolder(InteractionResult.SUCCESS, var4);
            }
         } else {
            return new InteractionResultHolder(InteractionResult.PASS, var4);
         }
      }
   }
}
