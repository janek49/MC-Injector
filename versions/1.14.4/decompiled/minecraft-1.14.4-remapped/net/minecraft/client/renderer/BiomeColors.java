package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Cursor3D;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.biome.Biome;

@ClientJarOnly
public class BiomeColors {
   private static final BiomeColors.ColorResolver GRASS_COLOR_RESOLVER = Biome::getGrassColor;
   private static final BiomeColors.ColorResolver FOLIAGE_COLOR_RESOLVER = Biome::getFoliageColor;
   private static final BiomeColors.ColorResolver WATER_COLOR_RESOLVER = (biome, blockPos) -> {
      return biome.getWaterColor();
   };
   private static final BiomeColors.ColorResolver WATER_FOG_COLOR_RESOLVER = (biome, blockPos) -> {
      return biome.getWaterFogColor();
   };

   private static int getAverageColor(BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos, BiomeColors.ColorResolver biomeColors$ColorResolver) {
      int var3 = 0;
      int var4 = 0;
      int var5 = 0;
      int var6 = Minecraft.getInstance().options.biomeBlendRadius;
      if(var6 == 0) {
         return biomeColors$ColorResolver.getColor(blockAndBiomeGetter.getBiome(blockPos), blockPos);
      } else {
         int var7 = (var6 * 2 + 1) * (var6 * 2 + 1);
         Cursor3D var8 = new Cursor3D(blockPos.getX() - var6, blockPos.getY(), blockPos.getZ() - var6, blockPos.getX() + var6, blockPos.getY(), blockPos.getZ() + var6);

         int var10;
         for(BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos(); var8.advance(); var5 += var10 & 255) {
            var9.set(var8.nextX(), var8.nextY(), var8.nextZ());
            var10 = biomeColors$ColorResolver.getColor(blockAndBiomeGetter.getBiome(var9), var9);
            var3 += (var10 & 16711680) >> 16;
            var4 += (var10 & '\uff00') >> 8;
         }

         return (var3 / var7 & 255) << 16 | (var4 / var7 & 255) << 8 | var5 / var7 & 255;
      }
   }

   public static int getAverageGrassColor(BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
      return getAverageColor(blockAndBiomeGetter, blockPos, GRASS_COLOR_RESOLVER);
   }

   public static int getAverageFoliageColor(BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
      return getAverageColor(blockAndBiomeGetter, blockPos, FOLIAGE_COLOR_RESOLVER);
   }

   public static int getAverageWaterColor(BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
      return getAverageColor(blockAndBiomeGetter, blockPos, WATER_COLOR_RESOLVER);
   }

   @ClientJarOnly
   interface ColorResolver {
      int getColor(Biome var1, BlockPos var2);
   }
}
