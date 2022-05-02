package net.minecraft.world.entity.ai.goal;

import com.google.common.collect.Lists;
import java.util.EnumSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import java.util.function.ToDoubleFunction;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.DoorInteractGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation;
import net.minecraft.world.entity.ai.util.RandomPos;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.pathfinder.Node;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;

public class MoveThroughVillageGoal extends Goal {
   protected final PathfinderMob mob;
   private final double speedModifier;
   private Path path;
   private BlockPos poiPos;
   private final boolean onlyAtNight;
   private final List visited = Lists.newArrayList();
   private final int distanceToPoi;
   private final BooleanSupplier canDealWithDoors;

   public MoveThroughVillageGoal(PathfinderMob mob, double speedModifier, boolean onlyAtNight, int distanceToPoi, BooleanSupplier canDealWithDoors) {
      this.mob = mob;
      this.speedModifier = speedModifier;
      this.onlyAtNight = onlyAtNight;
      this.distanceToPoi = distanceToPoi;
      this.canDealWithDoors = canDealWithDoors;
      this.setFlags(EnumSet.of(Goal.Flag.MOVE));
      if(!(mob.getNavigation() instanceof GroundPathNavigation)) {
         throw new IllegalArgumentException("Unsupported mob for MoveThroughVillageGoal");
      }
   }

   public boolean canUse() {
      this.updateVisited();
      if(this.onlyAtNight && this.mob.level.isDay()) {
         return false;
      } else {
         ServerLevel var1 = (ServerLevel)this.mob.level;
         BlockPos var2 = new BlockPos(this.mob);
         if(!var1.closeToVillage(var2, 6)) {
            return false;
         } else {
            Vec3 var3 = RandomPos.getLandPos(this.mob, 15, 7, (var3) -> {
               if(!var1.isVillage(var3)) {
                  return Double.NEGATIVE_INFINITY;
               } else {
                  Optional<BlockPos> var4 = var1.getPoiManager().find(PoiType.ALL, this::hasNotVisited, var3, 10, PoiManager.Occupancy.IS_OCCUPIED);
                  return !var4.isPresent()?Double.NEGATIVE_INFINITY:-((BlockPos)var4.get()).distSqr(var2);
               }
            });
            if(var3 == null) {
               return false;
            } else {
               Optional<BlockPos> var4 = var1.getPoiManager().find(PoiType.ALL, this::hasNotVisited, new BlockPos(var3), 10, PoiManager.Occupancy.IS_OCCUPIED);
               if(!var4.isPresent()) {
                  return false;
               } else {
                  this.poiPos = ((BlockPos)var4.get()).immutable();
                  GroundPathNavigation var5 = (GroundPathNavigation)this.mob.getNavigation();
                  boolean var6 = var5.canOpenDoors();
                  var5.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                  this.path = var5.createPath((BlockPos)this.poiPos, 0);
                  var5.setCanOpenDoors(var6);
                  if(this.path == null) {
                     Vec3 var7 = RandomPos.getPosTowards(this.mob, 10, 7, new Vec3((double)this.poiPos.getX(), (double)this.poiPos.getY(), (double)this.poiPos.getZ()));
                     if(var7 == null) {
                        return false;
                     }

                     var5.setCanOpenDoors(this.canDealWithDoors.getAsBoolean());
                     this.path = this.mob.getNavigation().createPath(var7.x, var7.y, var7.z, 0);
                     var5.setCanOpenDoors(var6);
                     if(this.path == null) {
                        return false;
                     }
                  }

                  for(int var7 = 0; var7 < this.path.getSize(); ++var7) {
                     Node var8 = this.path.get(var7);
                     BlockPos var9 = new BlockPos(var8.x, var8.y + 1, var8.z);
                     if(DoorInteractGoal.isDoor(this.mob.level, var9)) {
                        this.path = this.mob.getNavigation().createPath((double)var8.x, (double)var8.y, (double)var8.z, 0);
                        break;
                     }
                  }

                  return this.path != null;
               }
            }
         }
      }
   }

   public boolean canContinueToUse() {
      return this.mob.getNavigation().isDone()?false:!this.poiPos.closerThan(this.mob.position(), (double)(this.mob.getBbWidth() + (float)this.distanceToPoi));
   }

   public void start() {
      this.mob.getNavigation().moveTo(this.path, this.speedModifier);
   }

   public void stop() {
      if(this.mob.getNavigation().isDone() || this.poiPos.closerThan(this.mob.position(), (double)this.distanceToPoi)) {
         this.visited.add(this.poiPos);
      }

   }

   private boolean hasNotVisited(BlockPos blockPos) {
      for(BlockPos var3 : this.visited) {
         if(Objects.equals(blockPos, var3)) {
            return false;
         }
      }

      return true;
   }

   private void updateVisited() {
      if(this.visited.size() > 15) {
         this.visited.remove(0);
      }

   }
}
