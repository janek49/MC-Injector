package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.chunk.LightChunkGetter;
import net.minecraft.world.level.lighting.BlockLightEngine;
import net.minecraft.world.level.lighting.LayerLightEngine;
import net.minecraft.world.level.lighting.LayerLightEventListener;
import net.minecraft.world.level.lighting.LightEventListener;
import net.minecraft.world.level.lighting.SkyLightEngine;

public class LevelLightEngine implements LightEventListener {
   @Nullable
   private final LayerLightEngine blockEngine;
   @Nullable
   private final LayerLightEngine skyEngine;

   public LevelLightEngine(LightChunkGetter lightChunkGetter, boolean var2, boolean var3) {
      this.blockEngine = var2?new BlockLightEngine(lightChunkGetter):null;
      this.skyEngine = var3?new SkyLightEngine(lightChunkGetter):null;
   }

   public void checkBlock(BlockPos blockPos) {
      if(this.blockEngine != null) {
         this.blockEngine.checkBlock(blockPos);
      }

      if(this.skyEngine != null) {
         this.skyEngine.checkBlock(blockPos);
      }

   }

   public void onBlockEmissionIncrease(BlockPos blockPos, int var2) {
      if(this.blockEngine != null) {
         this.blockEngine.onBlockEmissionIncrease(blockPos, var2);
      }

   }

   public boolean hasLightWork() {
      return this.skyEngine != null && this.skyEngine.hasLightWork()?true:this.blockEngine != null && this.blockEngine.hasLightWork();
   }

   public int runUpdates(int var1, boolean var2, boolean var3) {
      if(this.blockEngine != null && this.skyEngine != null) {
         int var4 = var1 / 2;
         int var5 = this.blockEngine.runUpdates(var4, var2, var3);
         int var6 = var1 - var4 + var5;
         int var7 = this.skyEngine.runUpdates(var6, var2, var3);
         return var5 == 0 && var7 > 0?this.blockEngine.runUpdates(var7, var2, var3):var7;
      } else {
         return this.blockEngine != null?this.blockEngine.runUpdates(var1, var2, var3):(this.skyEngine != null?this.skyEngine.runUpdates(var1, var2, var3):var1);
      }
   }

   public void updateSectionStatus(SectionPos sectionPos, boolean var2) {
      if(this.blockEngine != null) {
         this.blockEngine.updateSectionStatus(sectionPos, var2);
      }

      if(this.skyEngine != null) {
         this.skyEngine.updateSectionStatus(sectionPos, var2);
      }

   }

   public void enableLightSources(ChunkPos chunkPos, boolean var2) {
      if(this.blockEngine != null) {
         this.blockEngine.enableLightSources(chunkPos, var2);
      }

      if(this.skyEngine != null) {
         this.skyEngine.enableLightSources(chunkPos, var2);
      }

   }

   public LayerLightEventListener getLayerListener(LightLayer lightLayer) {
      return (LayerLightEventListener)(lightLayer == LightLayer.BLOCK?(this.blockEngine == null?LayerLightEventListener.DummyLightLayerEventListener.INSTANCE:this.blockEngine):(this.skyEngine == null?LayerLightEventListener.DummyLightLayerEventListener.INSTANCE:this.skyEngine));
   }

   public String getDebugData(LightLayer lightLayer, SectionPos sectionPos) {
      if(lightLayer == LightLayer.BLOCK) {
         if(this.blockEngine != null) {
            return this.blockEngine.getDebugData(sectionPos.asLong());
         }
      } else if(this.skyEngine != null) {
         return this.skyEngine.getDebugData(sectionPos.asLong());
      }

      return "n/a";
   }

   public void queueSectionData(LightLayer lightLayer, SectionPos sectionPos, @Nullable DataLayer dataLayer) {
      if(lightLayer == LightLayer.BLOCK) {
         if(this.blockEngine != null) {
            this.blockEngine.queueSectionData(sectionPos.asLong(), dataLayer);
         }
      } else if(this.skyEngine != null) {
         this.skyEngine.queueSectionData(sectionPos.asLong(), dataLayer);
      }

   }

   public void retainData(ChunkPos chunkPos, boolean var2) {
      if(this.blockEngine != null) {
         this.blockEngine.retainData(chunkPos, var2);
      }

      if(this.skyEngine != null) {
         this.skyEngine.retainData(chunkPos, var2);
      }

   }
}
