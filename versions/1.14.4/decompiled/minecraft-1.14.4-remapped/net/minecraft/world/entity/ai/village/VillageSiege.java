package net.minecraft.world.entity.ai.village;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.phys.Vec3;

public class VillageSiege {
   private boolean hasSetupSiege;
   private VillageSiege.State siegeState = VillageSiege.State.SIEGE_DONE;
   private int zombiesToSpawn;
   private int nextSpawnTime;
   private int spawnX;
   private int spawnY;
   private int spawnZ;

   public int tick(ServerLevel serverLevel, boolean var2, boolean var3) {
      if(!serverLevel.isDay() && var2) {
         float var4 = serverLevel.getTimeOfDay(0.0F);
         if((double)var4 == 0.5D) {
            this.siegeState = serverLevel.random.nextInt(10) == 0?VillageSiege.State.SIEGE_TONIGHT:VillageSiege.State.SIEGE_DONE;
         }

         if(this.siegeState == VillageSiege.State.SIEGE_DONE) {
            return 0;
         } else {
            if(!this.hasSetupSiege) {
               if(!this.tryToSetupSiege(serverLevel)) {
                  return 0;
               }

               this.hasSetupSiege = true;
            }

            if(this.nextSpawnTime > 0) {
               --this.nextSpawnTime;
               return 0;
            } else {
               this.nextSpawnTime = 2;
               if(this.zombiesToSpawn > 0) {
                  this.trySpawn(serverLevel);
                  --this.zombiesToSpawn;
               } else {
                  this.siegeState = VillageSiege.State.SIEGE_DONE;
               }

               return 1;
            }
         }
      } else {
         this.siegeState = VillageSiege.State.SIEGE_DONE;
         this.hasSetupSiege = false;
         return 0;
      }
   }

   private boolean tryToSetupSiege(ServerLevel serverLevel) {
      for(Player var3 : serverLevel.players()) {
         if(!var3.isSpectator()) {
            BlockPos var4 = var3.getCommandSenderBlockPosition();
            if(serverLevel.isVillage(var4) && serverLevel.getBiome(var4).getBiomeCategory() != Biome.BiomeCategory.MUSHROOM) {
               for(int var5 = 0; var5 < 10; ++var5) {
                  float var6 = serverLevel.random.nextFloat() * 6.2831855F;
                  this.spawnX = var4.getX() + Mth.floor(Mth.cos(var6) * 32.0F);
                  this.spawnY = var4.getY();
                  this.spawnZ = var4.getZ() + Mth.floor(Mth.sin(var6) * 32.0F);
                  if(this.findRandomSpawnPos(serverLevel, new BlockPos(this.spawnX, this.spawnY, this.spawnZ)) != null) {
                     this.nextSpawnTime = 0;
                     this.zombiesToSpawn = 20;
                     break;
                  }
               }

               return true;
            }
         }
      }

      return false;
   }

   private void trySpawn(ServerLevel serverLevel) {
      Vec3 var2 = this.findRandomSpawnPos(serverLevel, new BlockPos(this.spawnX, this.spawnY, this.spawnZ));
      if(var2 != null) {
         Zombie var3;
         try {
            var3 = new Zombie(serverLevel);
            var3.finalizeSpawn(serverLevel, serverLevel.getCurrentDifficultyAt(new BlockPos(var3)), MobSpawnType.EVENT, (SpawnGroupData)null, (CompoundTag)null);
         } catch (Exception var5) {
            var5.printStackTrace();
            return;
         }

         var3.moveTo(var2.x, var2.y, var2.z, serverLevel.random.nextFloat() * 360.0F, 0.0F);
         serverLevel.addFreshEntity(var3);
      }
   }

   @Nullable
   private Vec3 findRandomSpawnPos(ServerLevel serverLevel, BlockPos blockPos) {
      for(int var3 = 0; var3 < 10; ++var3) {
         int var4 = blockPos.getX() + serverLevel.random.nextInt(16) - 8;
         int var5 = blockPos.getZ() + serverLevel.random.nextInt(16) - 8;
         int var6 = serverLevel.getHeight(Heightmap.Types.WORLD_SURFACE, var4, var5);
         BlockPos var7 = new BlockPos(var4, var6, var5);
         if(serverLevel.isVillage(var7) && Monster.checkMonsterSpawnRules(EntityType.ZOMBIE, serverLevel, MobSpawnType.EVENT, var7, serverLevel.random)) {
            return new Vec3((double)var7.getX() + 0.5D, (double)var7.getY(), (double)var7.getZ() + 0.5D);
         }
      }

      return null;
   }

   static enum State {
      SIEGE_CAN_ACTIVATE,
      SIEGE_TONIGHT,
      SIEGE_DONE;
   }
}
