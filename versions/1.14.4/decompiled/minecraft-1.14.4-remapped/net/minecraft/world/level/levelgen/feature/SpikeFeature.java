package net.minecraft.world.level.levelgen.feature;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.ImmutableMap.Builder;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.boss.enderdragon.EndCrystal;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.IronBarsBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.SpikeConfiguration;
import net.minecraft.world.phys.AABB;

public class SpikeFeature extends Feature {
   private static final LoadingCache SPIKE_CACHE = CacheBuilder.newBuilder().expireAfterWrite(5L, TimeUnit.MINUTES).build(new SpikeFeature.SpikeCacheLoader());

   public SpikeFeature(Function function) {
      super(function);
   }

   public static List getSpikesForLevel(LevelAccessor levelAccessor) {
      Random var1 = new Random(levelAccessor.getSeed());
      long var2 = var1.nextLong() & 65535L;
      return (List)SPIKE_CACHE.getUnchecked(Long.valueOf(var2));
   }

   public boolean place(LevelAccessor levelAccessor, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, SpikeConfiguration spikeConfiguration) {
      List<SpikeFeature.EndSpike> var6 = spikeConfiguration.getSpikes();
      if(var6.isEmpty()) {
         var6 = getSpikesForLevel(levelAccessor);
      }

      for(SpikeFeature.EndSpike var8 : var6) {
         if(var8.isCenterWithinChunk(blockPos)) {
            this.placeSpike(levelAccessor, random, spikeConfiguration, var8);
         }
      }

      return true;
   }

   private void placeSpike(LevelAccessor levelAccessor, Random random, SpikeConfiguration spikeConfiguration, SpikeFeature.EndSpike spikeFeature$EndSpike) {
      int var5 = spikeFeature$EndSpike.getRadius();

      for(BlockPos var7 : BlockPos.betweenClosed(new BlockPos(spikeFeature$EndSpike.getCenterX() - var5, 0, spikeFeature$EndSpike.getCenterZ() - var5), new BlockPos(spikeFeature$EndSpike.getCenterX() + var5, spikeFeature$EndSpike.getHeight() + 10, spikeFeature$EndSpike.getCenterZ() + var5))) {
         if(var7.closerThan(new BlockPos(spikeFeature$EndSpike.getCenterX(), var7.getY(), spikeFeature$EndSpike.getCenterZ()), (double)var5) && var7.getY() < spikeFeature$EndSpike.getHeight()) {
            this.setBlock(levelAccessor, var7, Blocks.OBSIDIAN.defaultBlockState());
         } else if(var7.getY() > 65) {
            this.setBlock(levelAccessor, var7, Blocks.AIR.defaultBlockState());
         }
      }

      if(spikeFeature$EndSpike.isGuarded()) {
         int var6 = -2;
         int var7 = 2;
         int var8 = 3;
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();

         for(int var10 = -2; var10 <= 2; ++var10) {
            for(int var11 = -2; var11 <= 2; ++var11) {
               for(int var12 = 0; var12 <= 3; ++var12) {
                  boolean var13 = Mth.abs(var10) == 2;
                  boolean var14 = Mth.abs(var11) == 2;
                  boolean var15 = var12 == 3;
                  if(var13 || var14 || var15) {
                     boolean var16 = var10 == -2 || var10 == 2 || var15;
                     boolean var17 = var11 == -2 || var11 == 2 || var15;
                     BlockState var18 = (BlockState)((BlockState)((BlockState)((BlockState)Blocks.IRON_BARS.defaultBlockState().setValue(IronBarsBlock.NORTH, Boolean.valueOf(var16 && var11 != -2))).setValue(IronBarsBlock.SOUTH, Boolean.valueOf(var16 && var11 != 2))).setValue(IronBarsBlock.WEST, Boolean.valueOf(var17 && var10 != -2))).setValue(IronBarsBlock.EAST, Boolean.valueOf(var17 && var10 != 2));
                     this.setBlock(levelAccessor, var9.set(spikeFeature$EndSpike.getCenterX() + var10, spikeFeature$EndSpike.getHeight() + var12, spikeFeature$EndSpike.getCenterZ() + var11), var18);
                  }
               }
            }
         }
      }

      EndCrystal var6 = (EndCrystal)EntityType.END_CRYSTAL.create(levelAccessor.getLevel());
      var6.setBeamTarget(spikeConfiguration.getCrystalBeamTarget());
      var6.setInvulnerable(spikeConfiguration.isCrystalInvulnerable());
      var6.moveTo((double)((float)spikeFeature$EndSpike.getCenterX() + 0.5F), (double)(spikeFeature$EndSpike.getHeight() + 1), (double)((float)spikeFeature$EndSpike.getCenterZ() + 0.5F), random.nextFloat() * 360.0F, 0.0F);
      levelAccessor.addFreshEntity(var6);
      this.setBlock(levelAccessor, new BlockPos(spikeFeature$EndSpike.getCenterX(), spikeFeature$EndSpike.getHeight(), spikeFeature$EndSpike.getCenterZ()), Blocks.BEDROCK.defaultBlockState());
   }

   public static class EndSpike {
      private final int centerX;
      private final int centerZ;
      private final int radius;
      private final int height;
      private final boolean guarded;
      private final AABB topBoundingBox;

      public EndSpike(int centerX, int centerZ, int radius, int height, boolean guarded) {
         this.centerX = centerX;
         this.centerZ = centerZ;
         this.radius = radius;
         this.height = height;
         this.guarded = guarded;
         this.topBoundingBox = new AABB((double)(centerX - radius), 0.0D, (double)(centerZ - radius), (double)(centerX + radius), 256.0D, (double)(centerZ + radius));
      }

      public boolean isCenterWithinChunk(BlockPos blockPos) {
         return blockPos.getX() >> 4 == this.centerX >> 4 && blockPos.getZ() >> 4 == this.centerZ >> 4;
      }

      public int getCenterX() {
         return this.centerX;
      }

      public int getCenterZ() {
         return this.centerZ;
      }

      public int getRadius() {
         return this.radius;
      }

      public int getHeight() {
         return this.height;
      }

      public boolean isGuarded() {
         return this.guarded;
      }

      public AABB getTopBoundingBox() {
         return this.topBoundingBox;
      }

      Dynamic serialize(DynamicOps dynamicOps) {
         Builder<T, T> var2 = ImmutableMap.builder();
         var2.put(dynamicOps.createString("centerX"), dynamicOps.createInt(this.centerX));
         var2.put(dynamicOps.createString("centerZ"), dynamicOps.createInt(this.centerZ));
         var2.put(dynamicOps.createString("radius"), dynamicOps.createInt(this.radius));
         var2.put(dynamicOps.createString("height"), dynamicOps.createInt(this.height));
         var2.put(dynamicOps.createString("guarded"), dynamicOps.createBoolean(this.guarded));
         return new Dynamic(dynamicOps, dynamicOps.createMap(var2.build()));
      }

      public static SpikeFeature.EndSpike deserialize(Dynamic dynamic) {
         return new SpikeFeature.EndSpike(dynamic.get("centerX").asInt(0), dynamic.get("centerZ").asInt(0), dynamic.get("radius").asInt(0), dynamic.get("height").asInt(0), dynamic.get("guarded").asBoolean(false));
      }
   }

   static class SpikeCacheLoader extends CacheLoader {
      private SpikeCacheLoader() {
      }

      public List load(Long long) {
         List<Integer> list = (List)IntStream.range(0, 10).boxed().collect(Collectors.toList());
         Collections.shuffle(list, new Random(long.longValue()));
         List<SpikeFeature.EndSpike> var3 = Lists.newArrayList();

         for(int var4 = 0; var4 < 10; ++var4) {
            int var5 = Mth.floor(42.0D * Math.cos(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double)var4)));
            int var6 = Mth.floor(42.0D * Math.sin(2.0D * (-3.141592653589793D + 0.3141592653589793D * (double)var4)));
            int var7 = ((Integer)list.get(var4)).intValue();
            int var8 = 2 + var7 / 3;
            int var9 = 76 + var7 * 3;
            boolean var10 = var7 == 1 || var7 == 2;
            var3.add(new SpikeFeature.EndSpike(var5, var6, var8, var9, var10));
         }

         return var3;
      }

      // $FF: synthetic method
      public Object load(Object var1) throws Exception {
         return this.load((Long)var1);
      }
   }
}
