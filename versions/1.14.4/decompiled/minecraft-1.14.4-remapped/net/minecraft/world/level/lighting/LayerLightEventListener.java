package net.minecraft.world.level.lighting;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.world.level.chunk.DataLayer;
import net.minecraft.world.level.lighting.LightEventListener;

public interface LayerLightEventListener extends LightEventListener {
   @Nullable
   DataLayer getDataLayerData(SectionPos var1);

   int getLightValue(BlockPos var1);

   public static enum DummyLightLayerEventListener implements LayerLightEventListener {
      INSTANCE;

      @Nullable
      public DataLayer getDataLayerData(SectionPos sectionPos) {
         return null;
      }

      public int getLightValue(BlockPos blockPos) {
         return 0;
      }

      public void updateSectionStatus(SectionPos sectionPos, boolean var2) {
      }
   }
}
