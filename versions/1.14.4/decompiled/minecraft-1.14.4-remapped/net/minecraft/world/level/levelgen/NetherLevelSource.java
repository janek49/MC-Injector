package net.minecraft.world.level.levelgen;

import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.NetherGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;

public class NetherLevelSource extends NoiseBasedChunkGenerator {
   private final double[] yOffsets = this.makeYOffsets();

   public NetherLevelSource(Level level, BiomeSource biomeSource, NetherGeneratorSettings netherGeneratorSettings) {
      super(level, biomeSource, 4, 8, 128, netherGeneratorSettings, false);
   }

   protected void fillNoiseColumn(double[] doubles, int var2, int var3) {
      double var4 = 684.412D;
      double var6 = 2053.236D;
      double var8 = 8.555150000000001D;
      double var10 = 34.2206D;
      int var12 = -10;
      int var13 = 3;
      this.fillNoiseColumn(doubles, var2, var3, 684.412D, 2053.236D, 8.555150000000001D, 34.2206D, 3, -10);
   }

   protected double[] getDepthAndScale(int var1, int var2) {
      return new double[]{0.0D, 0.0D};
   }

   protected double getYOffset(double var1, double var3, int var5) {
      return this.yOffsets[var5];
   }

   private double[] makeYOffsets() {
      double[] doubles = new double[this.getNoiseSizeY()];

      for(int var2 = 0; var2 < this.getNoiseSizeY(); ++var2) {
         doubles[var2] = Math.cos((double)var2 * 3.141592653589793D * 6.0D / (double)this.getNoiseSizeY()) * 2.0D;
         double var3 = (double)var2;
         if(var2 > this.getNoiseSizeY() / 2) {
            var3 = (double)(this.getNoiseSizeY() - 1 - var2);
         }

         if(var3 < 4.0D) {
            var3 = 4.0D - var3;
            doubles[var2] -= var3 * var3 * var3 * 10.0D;
         }
      }

      return doubles;
   }

   public List getMobsAt(MobCategory mobCategory, BlockPos blockPos) {
      if(mobCategory == MobCategory.MONSTER) {
         if(Feature.NETHER_BRIDGE.isInsideFeature(this.level, blockPos)) {
            return Feature.NETHER_BRIDGE.getSpecialEnemies();
         }

         if(Feature.NETHER_BRIDGE.isInsideBoundingFeature(this.level, blockPos) && this.level.getBlockState(blockPos.below()).getBlock() == Blocks.NETHER_BRICKS) {
            return Feature.NETHER_BRIDGE.getSpecialEnemies();
         }
      }

      return super.getMobsAt(mobCategory, blockPos);
   }

   public int getSpawnHeight() {
      return this.level.getSeaLevel() + 1;
   }

   public int getGenDepth() {
      return 128;
   }

   public int getSeaLevel() {
      return 32;
   }
}
