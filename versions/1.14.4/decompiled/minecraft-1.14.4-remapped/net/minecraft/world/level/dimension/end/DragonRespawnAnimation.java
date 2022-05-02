package net.minecraft.world.level.dimension.end;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeConfiguration;
import net.minecraft.world.level.levelgen.feature.SpikeFeature;

public enum DragonRespawnAnimation {
   START {
      public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List list, int var4, BlockPos blockPosx) {
         BlockPos blockPos = new BlockPos(0, 128, 0);

         for(EndCrystal var8 : list) {
            var8.setBeamTarget(blockPos);
         }

         endDragonFight.setRespawnStage(PREPARING_TO_SUMMON_PILLARS);
      }
   },
   PREPARING_TO_SUMMON_PILLARS {
      public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List list, int var4, BlockPos blockPos) {
         if(var4 < 100) {
            if(var4 == 0 || var4 == 50 || var4 == 51 || var4 == 52 || var4 >= 95) {
               serverLevel.levelEvent(3001, new BlockPos(0, 128, 0), 0);
            }
         } else {
            endDragonFight.setRespawnStage(SUMMONING_PILLARS);
         }

      }
   },
   SUMMONING_PILLARS {
      public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List list, int var4, BlockPos blockPos) {
         int var6 = 40;
         boolean var7 = var4 % 40 == 0;
         boolean var8 = var4 % 40 == 39;
         if(var7 || var8) {
            List<SpikeFeature.EndSpike> var9 = SpikeFeature.getSpikesForLevel(serverLevel);
            int var10 = var4 / 40;
            if(var10 < var9.size()) {
               SpikeFeature.EndSpike var11 = (SpikeFeature.EndSpike)var9.get(var10);
               if(var7) {
                  for(EndCrystal var13 : list) {
                     var13.setBeamTarget(new BlockPos(var11.getCenterX(), var11.getHeight() + 1, var11.getCenterZ()));
                  }
               } else {
                  int var12 = 10;

                  for(BlockPos var14 : BlockPos.betweenClosed(new BlockPos(var11.getCenterX() - 10, var11.getHeight() - 10, var11.getCenterZ() - 10), new BlockPos(var11.getCenterX() + 10, var11.getHeight() + 10, var11.getCenterZ() + 10))) {
                     serverLevel.removeBlock(var14, false);
                  }

                  serverLevel.explode((Entity)null, (double)((float)var11.getCenterX() + 0.5F), (double)var11.getHeight(), (double)((float)var11.getCenterZ() + 0.5F), 5.0F, Explosion.BlockInteraction.DESTROY);
                  SpikeConfiguration var13 = new SpikeConfiguration(true, ImmutableList.of(var11), new BlockPos(0, 128, 0));
                  Feature.END_SPIKE.place(serverLevel, serverLevel.getChunkSource().getGenerator(), new Random(), new BlockPos(var11.getCenterX(), 45, var11.getCenterZ()), var13);
               }
            } else if(var7) {
               endDragonFight.setRespawnStage(SUMMONING_DRAGON);
            }
         }

      }
   },
   SUMMONING_DRAGON {
      public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List list, int var4, BlockPos blockPos) {
         if(var4 >= 100) {
            endDragonFight.setRespawnStage(END);
            endDragonFight.resetSpikeCrystals();

            for(EndCrystal var7 : list) {
               var7.setBeamTarget((BlockPos)null);
               serverLevel.explode(var7, var7.x, var7.y, var7.z, 6.0F, Explosion.BlockInteraction.NONE);
               var7.remove();
            }
         } else if(var4 >= 80) {
            serverLevel.levelEvent(3001, new BlockPos(0, 128, 0), 0);
         } else if(var4 == 0) {
            for(EndCrystal var7 : list) {
               var7.setBeamTarget(new BlockPos(0, 128, 0));
            }
         } else if(var4 < 5) {
            serverLevel.levelEvent(3001, new BlockPos(0, 128, 0), 0);
         }

      }
   },
   END {
      public void tick(ServerLevel serverLevel, EndDragonFight endDragonFight, List list, int var4, BlockPos blockPos) {
      }
   };

   private DragonRespawnAnimation() {
   }

   public abstract void tick(ServerLevel var1, EndDragonFight var2, List var3, int var4, BlockPos var5);
}
