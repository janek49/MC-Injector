package net.minecraft.world.level.levelgen;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.PatrollingMonster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;

public class PatrolSpawner {
   private int nextTick;

   public int tick(ServerLevel serverLevel, boolean var2, boolean var3) {
      if(!var2) {
         return 0;
      } else {
         Random var4 = serverLevel.random;
         --this.nextTick;
         if(this.nextTick > 0) {
            return 0;
         } else {
            this.nextTick += 12000 + var4.nextInt(1200);
            long var5 = serverLevel.getDayTime() / 24000L;
            if(var5 >= 5L && serverLevel.isDay()) {
               if(var4.nextInt(5) != 0) {
                  return 0;
               } else {
                  int var7 = serverLevel.players().size();
                  if(var7 < 1) {
                     return 0;
                  } else {
                     Player var8 = (Player)serverLevel.players().get(var4.nextInt(var7));
                     if(var8.isSpectator()) {
                        return 0;
                     } else if(serverLevel.isVillage(var8.getCommandSenderBlockPosition())) {
                        return 0;
                     } else {
                        int var9 = (24 + var4.nextInt(24)) * (var4.nextBoolean()?-1:1);
                        int var10 = (24 + var4.nextInt(24)) * (var4.nextBoolean()?-1:1);
                        BlockPos.MutableBlockPos var11 = new BlockPos.MutableBlockPos();
                        var11.set(var8.x, var8.y, var8.z).move(var9, 0, var10);
                        if(!serverLevel.hasChunksAt(var11.getX() - 10, var11.getY() - 10, var11.getZ() - 10, var11.getX() + 10, var11.getY() + 10, var11.getZ() + 10)) {
                           return 0;
                        } else {
                           Biome var12 = serverLevel.getBiome(var11);
                           Biome.BiomeCategory var13 = var12.getBiomeCategory();
                           if(var13 == Biome.BiomeCategory.MUSHROOM) {
                              return 0;
                           } else {
                              int var14 = 0;
                              int var15 = (int)Math.ceil((double)serverLevel.getCurrentDifficultyAt(var11).getEffectiveDifficulty()) + 1;

                              for(int var16 = 0; var16 < var15; ++var16) {
                                 ++var14;
                                 var11.setY(serverLevel.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, var11).getY());
                                 if(var16 == 0) {
                                    if(!this.spawnPatrolMember(serverLevel, var11, var4, true)) {
                                       break;
                                    }
                                 } else {
                                    this.spawnPatrolMember(serverLevel, var11, var4, false);
                                 }

                                 var11.setX(var11.getX() + var4.nextInt(5) - var4.nextInt(5));
                                 var11.setZ(var11.getZ() + var4.nextInt(5) - var4.nextInt(5));
                              }

                              return var14;
                           }
                        }
                     }
                  }
               }
            } else {
               return 0;
            }
         }
      }
   }

   private boolean spawnPatrolMember(Level level, BlockPos blockPos, Random random, boolean var4) {
      if(!PatrollingMonster.checkPatrollingMonsterSpawnRules(EntityType.PILLAGER, level, MobSpawnType.PATROL, blockPos, random)) {
         return false;
      } else {
         PatrollingMonster var5 = (PatrollingMonster)EntityType.PILLAGER.create(level);
         if(var5 != null) {
            if(var4) {
               var5.setPatrolLeader(true);
               var5.findPatrolTarget();
            }

            var5.setPos((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
            var5.finalizeSpawn(level, level.getCurrentDifficultyAt(blockPos), MobSpawnType.PATROL, (SpawnGroupData)null, (CompoundTag)null);
            level.addFreshEntity(var5);
            return true;
         } else {
            return false;
         }
      }
   }
}
