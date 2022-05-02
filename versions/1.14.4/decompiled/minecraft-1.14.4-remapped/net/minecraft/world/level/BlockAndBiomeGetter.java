package net.minecraft.world.level;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.LightLayer;
import net.minecraft.world.level.biome.Biome;

public interface BlockAndBiomeGetter extends BlockGetter {
   Biome getBiome(BlockPos var1);

   int getBrightness(LightLayer var1, BlockPos var2);

   default boolean canSeeSky(BlockPos blockPos) {
      return this.getBrightness(LightLayer.SKY, blockPos) >= this.getMaxLightLevel();
   }

   default int getLightColor(BlockPos blockPos, int var2) {
      int var3 = this.getBrightness(LightLayer.SKY, blockPos);
      int var4 = this.getBrightness(LightLayer.BLOCK, blockPos);
      if(var4 < var2) {
         var4 = var2;
      }

      return var3 << 20 | var4 << 4;
   }
}
