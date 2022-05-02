package net.minecraft.world.entity.npc;

import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.phys.AABB;

public class CatSpawner {
   private int nextTick;

   public int tick(ServerLevel serverLevel, boolean var2, boolean var3) {
      if(var3 && serverLevel.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
         --this.nextTick;
         if(this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick = 1200;
            Player var4 = serverLevel.getRandomPlayer();
            if(var4 == null) {
               return 0;
            } else {
               Random var5 = serverLevel.random;
               int var6 = (8 + var5.nextInt(24)) * (var5.nextBoolean()?-1:1);
               int var7 = (8 + var5.nextInt(24)) * (var5.nextBoolean()?-1:1);
               BlockPos var8 = (new BlockPos(var4)).offset(var6, 0, var7);
               if(!serverLevel.hasChunksAt(var8.getX() - 10, var8.getY() - 10, var8.getZ() - 10, var8.getX() + 10, var8.getY() + 10, var8.getZ() + 10)) {
                  return 0;
               } else {
                  if(NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, serverLevel, var8, EntityType.CAT)) {
                     if(serverLevel.closeToVillage(var8, 2)) {
                        return this.spawnInVillage(serverLevel, var8);
                     }

                     if(Feature.SWAMP_HUT.isInsideFeature(serverLevel, var8)) {
                        return this.spawnInHut(serverLevel, var8);
                     }
                  }

                  return 0;
               }
            }
         }
      } else {
         return 0;
      }
   }

   private int spawnInVillage(ServerLevel serverLevel, BlockPos blockPos) {
      int var3 = 48;
      if(serverLevel.getPoiManager().getCountInRange(PoiType.HOME.getPredicate(), blockPos, 48, PoiManager.Occupancy.IS_OCCUPIED) > 4L) {
         List<Cat> var4 = serverLevel.getEntitiesOfClass(Cat.class, (new AABB(blockPos)).inflate(48.0D, 8.0D, 48.0D));
         if(var4.size() < 5) {
            return this.spawnCat(blockPos, serverLevel);
         }
      }

      return 0;
   }

   private int spawnInHut(Level level, BlockPos blockPos) {
      int var3 = 16;
      List<Cat> var4 = level.getEntitiesOfClass(Cat.class, (new AABB(blockPos)).inflate(16.0D, 8.0D, 16.0D));
      return var4.size() < 1?this.spawnCat(blockPos, level):0;
   }

   private int spawnCat(BlockPos blockPos, Level level) {
      Cat var3 = (Cat)EntityType.CAT.create(level);
      if(var3 == null) {
         return 0;
      } else {
         var3.finalizeSpawn(level, level.getCurrentDifficultyAt(blockPos), MobSpawnType.NATURAL, (SpawnGroupData)null, (CompoundTag)null);
         var3.moveTo(blockPos, 0.0F, 0.0F);
         level.addFreshEntity(var3);
         return 1;
      }
   }
}
