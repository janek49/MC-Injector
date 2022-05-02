package net.minecraft.world.level.levelgen;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.stats.Stats;
import net.minecraft.util.Mth;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

public class PhantomSpawner {
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
            this.nextTick += (60 + var4.nextInt(60)) * 20;
            if(serverLevel.getSkyDarken() < 5 && serverLevel.dimension.isHasSkyLight()) {
               return 0;
            } else {
               int var5 = 0;

               for(Player var7 : serverLevel.players()) {
                  if(!var7.isSpectator()) {
                     BlockPos var8 = new BlockPos(var7);
                     if(!serverLevel.dimension.isHasSkyLight() || var8.getY() >= serverLevel.getSeaLevel() && serverLevel.canSeeSky(var8)) {
                        DifficultyInstance var9 = serverLevel.getCurrentDifficultyAt(var8);
                        if(var9.isHarderThan(var4.nextFloat() * 3.0F)) {
                           ServerStatsCounter var10 = ((ServerPlayer)var7).getStats();
                           int var11 = Mth.clamp(var10.getValue(Stats.CUSTOM.get(Stats.TIME_SINCE_REST)), 1, Integer.MAX_VALUE);
                           int var12 = 24000;
                           if(var4.nextInt(var11) >= 72000) {
                              BlockPos var13 = var8.above(20 + var4.nextInt(15)).east(-10 + var4.nextInt(21)).south(-10 + var4.nextInt(21));
                              BlockState var14 = serverLevel.getBlockState(var13);
                              FluidState var15 = serverLevel.getFluidState(var13);
                              if(NaturalSpawner.isValidEmptySpawnBlock(serverLevel, var13, var14, var15)) {
                                 SpawnGroupData var16 = null;
                                 int var17 = 1 + var4.nextInt(var9.getDifficulty().getId() + 1);

                                 for(int var18 = 0; var18 < var17; ++var18) {
                                    Phantom var19 = (Phantom)EntityType.PHANTOM.create(serverLevel);
                                    var19.moveTo(var13, 0.0F, 0.0F);
                                    var16 = var19.finalizeSpawn(serverLevel, var9, MobSpawnType.NATURAL, var16, (CompoundTag)null);
                                    serverLevel.addFreshEntity(var19);
                                 }

                                 var5 += var17;
                              }
                           }
                        }
                     }
                  }
               }

               return var5;
            }
         }
      }
   }
}
