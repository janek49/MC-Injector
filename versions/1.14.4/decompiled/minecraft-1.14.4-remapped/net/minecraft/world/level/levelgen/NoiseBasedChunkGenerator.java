package net.minecraft.world.level.levelgen;

import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import java.util.Random;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.LevelChunkSection;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.ChunkGeneratorSettings;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.structures.JigsawJunction;
import net.minecraft.world.level.levelgen.feature.structures.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.synth.PerlinNoise;
import net.minecraft.world.level.levelgen.synth.PerlinSimplexNoise;
import net.minecraft.world.level.levelgen.synth.SurfaceNoise;

public abstract class NoiseBasedChunkGenerator extends ChunkGenerator {
   private static final float[] BEARD_KERNEL = (float[])Util.make(new float[13824], (floats) -> {
      for(int var1 = 0; var1 < 24; ++var1) {
         for(int var2 = 0; var2 < 24; ++var2) {
            for(int var3 = 0; var3 < 24; ++var3) {
               floats[var1 * 24 * 24 + var2 * 24 + var3] = (float)computeContribution(var2 - 12, var3 - 12, var1 - 12);
            }
         }
      }

   });
   private static final BlockState AIR = Blocks.AIR.defaultBlockState();
   private final int chunkHeight;
   private final int chunkWidth;
   private final int chunkCountX;
   private final int chunkCountY;
   private final int chunkCountZ;
   protected final WorldgenRandom random;
   private final PerlinNoise minLimitPerlinNoise;
   private final PerlinNoise maxLimitPerlinNoise;
   private final PerlinNoise mainPerlinNoise;
   private final SurfaceNoise surfaceNoise;
   protected final BlockState defaultBlock;
   protected final BlockState defaultFluid;

   public NoiseBasedChunkGenerator(LevelAccessor levelAccessor, BiomeSource biomeSource, int chunkWidth, int chunkHeight, int var5, ChunkGeneratorSettings chunkGeneratorSettings, boolean var7) {
      super(levelAccessor, biomeSource, chunkGeneratorSettings);
      this.chunkHeight = chunkHeight;
      this.chunkWidth = chunkWidth;
      this.defaultBlock = chunkGeneratorSettings.getDefaultBlock();
      this.defaultFluid = chunkGeneratorSettings.getDefaultFluid();
      this.chunkCountX = 16 / this.chunkWidth;
      this.chunkCountY = var5 / this.chunkHeight;
      this.chunkCountZ = 16 / this.chunkWidth;
      this.random = new WorldgenRandom(this.seed);
      this.minLimitPerlinNoise = new PerlinNoise(this.random, 16);
      this.maxLimitPerlinNoise = new PerlinNoise(this.random, 16);
      this.mainPerlinNoise = new PerlinNoise(this.random, 8);
      this.surfaceNoise = (SurfaceNoise)(var7?new PerlinSimplexNoise(this.random, 4):new PerlinNoise(this.random, 4));
   }

   private double sampleAndClampNoise(int var1, int var2, int var3, double var4, double var6, double var8, double var10) {
      double var12 = 0.0D;
      double var14 = 0.0D;
      double var16 = 0.0D;
      double var18 = 1.0D;

      for(int var20 = 0; var20 < 16; ++var20) {
         double var21 = PerlinNoise.wrap((double)var1 * var4 * var18);
         double var23 = PerlinNoise.wrap((double)var2 * var6 * var18);
         double var25 = PerlinNoise.wrap((double)var3 * var4 * var18);
         double var27 = var6 * var18;
         var12 += this.minLimitPerlinNoise.getOctaveNoise(var20).noise(var21, var23, var25, var27, (double)var2 * var27) / var18;
         var14 += this.maxLimitPerlinNoise.getOctaveNoise(var20).noise(var21, var23, var25, var27, (double)var2 * var27) / var18;
         if(var20 < 8) {
            var16 += this.mainPerlinNoise.getOctaveNoise(var20).noise(PerlinNoise.wrap((double)var1 * var8 * var18), PerlinNoise.wrap((double)var2 * var10 * var18), PerlinNoise.wrap((double)var3 * var8 * var18), var10 * var18, (double)var2 * var10 * var18) / var18;
         }

         var18 /= 2.0D;
      }

      return Mth.clampedLerp(var12 / 512.0D, var14 / 512.0D, (var16 / 10.0D + 1.0D) / 2.0D);
   }

   protected double[] makeAndFillNoiseColumn(int var1, int var2) {
      double[] doubles = new double[this.chunkCountY + 1];
      this.fillNoiseColumn(doubles, var1, var2);
      return doubles;
   }

   protected void fillNoiseColumn(double[] doubles, int var2, int var3, double var4, double var6, double var8, double var10, int var12, int var13) {
      double[] doubles = this.getDepthAndScale(var2, var3);
      double var15 = doubles[0];
      double var17 = doubles[1];
      double var19 = this.getTopSlideStart();
      double var21 = this.getBottomSlideStart();

      for(int var23 = 0; var23 < this.getNoiseSizeY(); ++var23) {
         double var24 = this.sampleAndClampNoise(var2, var23, var3, var4, var6, var8, var10);
         var24 = var24 - this.getYOffset(var15, var17, var23);
         if((double)var23 > var19) {
            var24 = Mth.clampedLerp(var24, (double)var13, ((double)var23 - var19) / (double)var12);
         } else if((double)var23 < var21) {
            var24 = Mth.clampedLerp(var24, -30.0D, (var21 - (double)var23) / (var21 - 1.0D));
         }

         doubles[var23] = var24;
      }

   }

   protected abstract double[] getDepthAndScale(int var1, int var2);

   protected abstract double getYOffset(double var1, double var3, int var5);

   protected double getTopSlideStart() {
      return (double)(this.getNoiseSizeY() - 4);
   }

   protected double getBottomSlideStart() {
      return 0.0D;
   }

   public int getBaseHeight(int var1, int var2, Heightmap.Types heightmap$Types) {
      int var4 = Math.floorDiv(var1, this.chunkWidth);
      int var5 = Math.floorDiv(var2, this.chunkWidth);
      int var6 = Math.floorMod(var1, this.chunkWidth);
      int var7 = Math.floorMod(var2, this.chunkWidth);
      double var8 = (double)var6 / (double)this.chunkWidth;
      double var10 = (double)var7 / (double)this.chunkWidth;
      double[][] vars12 = new double[][]{this.makeAndFillNoiseColumn(var4, var5), this.makeAndFillNoiseColumn(var4, var5 + 1), this.makeAndFillNoiseColumn(var4 + 1, var5), this.makeAndFillNoiseColumn(var4 + 1, var5 + 1)};
      int var13 = this.getSeaLevel();

      for(int var14 = this.chunkCountY - 1; var14 >= 0; --var14) {
         double var15 = vars12[0][var14];
         double var17 = vars12[1][var14];
         double var19 = vars12[2][var14];
         double var21 = vars12[3][var14];
         double var23 = vars12[0][var14 + 1];
         double var25 = vars12[1][var14 + 1];
         double var27 = vars12[2][var14 + 1];
         double var29 = vars12[3][var14 + 1];

         for(int var31 = this.chunkHeight - 1; var31 >= 0; --var31) {
            double var32 = (double)var31 / (double)this.chunkHeight;
            double var34 = Mth.lerp3(var32, var8, var10, var15, var23, var19, var27, var17, var25, var21, var29);
            int var36 = var14 * this.chunkHeight + var31;
            if(var34 > 0.0D || var36 < var13) {
               BlockState var37;
               if(var34 > 0.0D) {
                  var37 = this.defaultBlock;
               } else {
                  var37 = this.defaultFluid;
               }

               if(heightmap$Types.isOpaque().test(var37)) {
                  return var36 + 1;
               }
            }
         }
      }

      return 0;
   }

   protected abstract void fillNoiseColumn(double[] var1, int var2, int var3);

   public int getNoiseSizeY() {
      return this.chunkCountY + 1;
   }

   public void buildSurfaceAndBedrock(ChunkAccess chunkAccess) {
      ChunkPos var2 = chunkAccess.getPos();
      int var3 = var2.x;
      int var4 = var2.z;
      WorldgenRandom var5 = new WorldgenRandom();
      var5.setBaseChunkSeed(var3, var4);
      ChunkPos var6 = chunkAccess.getPos();
      int var7 = var6.getMinBlockX();
      int var8 = var6.getMinBlockZ();
      double var9 = 0.0625D;
      Biome[] vars11 = chunkAccess.getBiomes();

      for(int var12 = 0; var12 < 16; ++var12) {
         for(int var13 = 0; var13 < 16; ++var13) {
            int var14 = var7 + var12;
            int var15 = var8 + var13;
            int var16 = chunkAccess.getHeight(Heightmap.Types.WORLD_SURFACE_WG, var12, var13) + 1;
            double var17 = this.surfaceNoise.getSurfaceNoiseValue((double)var14 * 0.0625D, (double)var15 * 0.0625D, 0.0625D, (double)var12 * 0.0625D);
            vars11[var13 * 16 + var12].buildSurfaceAt(var5, chunkAccess, var14, var15, var16, var17, this.getSettings().getDefaultBlock(), this.getSettings().getDefaultFluid(), this.getSeaLevel(), this.level.getSeed());
         }
      }

      this.setBedrock(chunkAccess, var5);
   }

   protected void setBedrock(ChunkAccess chunkAccess, Random random) {
      BlockPos.MutableBlockPos var3 = new BlockPos.MutableBlockPos();
      int var4 = chunkAccess.getPos().getMinBlockX();
      int var5 = chunkAccess.getPos().getMinBlockZ();
      T var6 = this.getSettings();
      int var7 = var6.getBedrockFloorPosition();
      int var8 = var6.getBedrockRoofPosition();

      for(BlockPos var10 : BlockPos.betweenClosed(var4, 0, var5, var4 + 15, 0, var5 + 15)) {
         if(var8 > 0) {
            for(int var11 = var8; var11 >= var8 - 4; --var11) {
               if(var11 >= var8 - random.nextInt(5)) {
                  chunkAccess.setBlockState(var3.set(var10.getX(), var11, var10.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
               }
            }
         }

         if(var7 < 256) {
            for(int var11 = var7 + 4; var11 >= var7; --var11) {
               if(var11 <= var7 + random.nextInt(5)) {
                  chunkAccess.setBlockState(var3.set(var10.getX(), var11, var10.getZ()), Blocks.BEDROCK.defaultBlockState(), false);
               }
            }
         }
      }

   }

   public void fillFromNoise(LevelAccessor levelAccessor, ChunkAccess chunkAccess) {
      int var3 = this.getSeaLevel();
      ObjectList<PoolElementStructurePiece> var4 = new ObjectArrayList(10);
      ObjectList<JigsawJunction> var5 = new ObjectArrayList(32);
      ChunkPos var6 = chunkAccess.getPos();
      int var7 = var6.x;
      int var8 = var6.z;
      int var9 = var7 << 4;
      int var10 = var8 << 4;

      for(StructureFeature<?> var12 : Feature.NOISE_AFFECTING_FEATURES) {
         String var13 = var12.getFeatureName();
         LongIterator var14 = chunkAccess.getReferencesForFeature(var13).iterator();

         while(var14.hasNext()) {
            long var15 = var14.nextLong();
            ChunkPos var17 = new ChunkPos(var15);
            ChunkAccess var18 = levelAccessor.getChunk(var17.x, var17.z);
            StructureStart var19 = var18.getStartForFeature(var13);
            if(var19 != null && var19.isValid()) {
               for(StructurePiece var21 : var19.getPieces()) {
                  if(var21.isCloseToChunk(var6, 12) && var21 instanceof PoolElementStructurePiece) {
                     PoolElementStructurePiece var22 = (PoolElementStructurePiece)var21;
                     StructureTemplatePool.Projection var23 = var22.getElement().getProjection();
                     if(var23 == StructureTemplatePool.Projection.RIGID) {
                        var4.add(var22);
                     }

                     for(JigsawJunction var25 : var22.getJunctions()) {
                        int var26 = var25.getSourceX();
                        int var27 = var25.getSourceZ();
                        if(var26 > var9 - 12 && var27 > var10 - 12 && var26 < var9 + 15 + 12 && var27 < var10 + 15 + 12) {
                           var5.add(var25);
                        }
                     }
                  }
               }
            }
         }
      }

      double[][][] vars11 = new double[2][this.chunkCountZ + 1][this.chunkCountY + 1];

      for(int var12 = 0; var12 < this.chunkCountZ + 1; ++var12) {
         vars11[0][var12] = new double[this.chunkCountY + 1];
         this.fillNoiseColumn(vars11[0][var12], var7 * this.chunkCountX, var8 * this.chunkCountZ + var12);
         vars11[1][var12] = new double[this.chunkCountY + 1];
      }

      ProtoChunk var12 = (ProtoChunk)chunkAccess;
      Heightmap var13 = var12.getOrCreateHeightmapUnprimed(Heightmap.Types.OCEAN_FLOOR_WG);
      Heightmap var14 = var12.getOrCreateHeightmapUnprimed(Heightmap.Types.WORLD_SURFACE_WG);
      BlockPos.MutableBlockPos var15 = new BlockPos.MutableBlockPos();
      ObjectListIterator<PoolElementStructurePiece> var16 = var4.iterator();
      ObjectListIterator<JigsawJunction> var17 = var5.iterator();

      for(int var18 = 0; var18 < this.chunkCountX; ++var18) {
         for(int var19 = 0; var19 < this.chunkCountZ + 1; ++var19) {
            this.fillNoiseColumn(vars11[1][var19], var7 * this.chunkCountX + var18 + 1, var8 * this.chunkCountZ + var19);
         }

         for(int var19 = 0; var19 < this.chunkCountZ; ++var19) {
            LevelChunkSection var20 = var12.getOrCreateSection(15);
            var20.acquire();

            for(int var21 = this.chunkCountY - 1; var21 >= 0; --var21) {
               double var22 = vars11[0][var19][var21];
               double var24 = vars11[0][var19 + 1][var21];
               double var26 = vars11[1][var19][var21];
               double var28 = vars11[1][var19 + 1][var21];
               double var30 = vars11[0][var19][var21 + 1];
               double var32 = vars11[0][var19 + 1][var21 + 1];
               double var34 = vars11[1][var19][var21 + 1];
               double var36 = vars11[1][var19 + 1][var21 + 1];

               for(int var38 = this.chunkHeight - 1; var38 >= 0; --var38) {
                  int var39 = var21 * this.chunkHeight + var38;
                  int var40 = var39 & 15;
                  int var41 = var39 >> 4;
                  if(var20.bottomBlockY() >> 4 != var41) {
                     var20.release();
                     var20 = var12.getOrCreateSection(var41);
                     var20.acquire();
                  }

                  double var42 = (double)var38 / (double)this.chunkHeight;
                  double var44 = Mth.lerp(var42, var22, var30);
                  double var46 = Mth.lerp(var42, var26, var34);
                  double var48 = Mth.lerp(var42, var24, var32);
                  double var50 = Mth.lerp(var42, var28, var36);

                  for(int var52 = 0; var52 < this.chunkWidth; ++var52) {
                     int var53 = var9 + var18 * this.chunkWidth + var52;
                     int var54 = var53 & 15;
                     double var55 = (double)var52 / (double)this.chunkWidth;
                     double var57 = Mth.lerp(var55, var44, var46);
                     double var59 = Mth.lerp(var55, var48, var50);

                     for(int var61 = 0; var61 < this.chunkWidth; ++var61) {
                        int var62 = var10 + var19 * this.chunkWidth + var61;
                        int var63 = var62 & 15;
                        double var64 = (double)var61 / (double)this.chunkWidth;
                        double var66 = Mth.lerp(var64, var57, var59);
                        double var68 = Mth.clamp(var66 / 200.0D, -1.0D, 1.0D);

                        int var72;
                        int var73;
                        int var74;
                        for(var68 = var68 / 2.0D - var68 * var68 * var68 / 24.0D; var16.hasNext(); var68 += getContribution(var72, var73, var74) * 0.8D) {
                           PoolElementStructurePiece var70 = (PoolElementStructurePiece)var16.next();
                           BoundingBox var71 = var70.getBoundingBox();
                           var72 = Math.max(0, Math.max(var71.x0 - var53, var53 - var71.x1));
                           var73 = var39 - (var71.y0 + var70.getGroundLevelDelta());
                           var74 = Math.max(0, Math.max(var71.z0 - var62, var62 - var71.z1));
                        }

                        var16.back(var4.size());

                        while(var17.hasNext()) {
                           JigsawJunction var70 = (JigsawJunction)var17.next();
                           int var71 = var53 - var70.getSourceX();
                           var72 = var39 - var70.getSourceGroundY();
                           var73 = var62 - var70.getSourceZ();
                           var68 += getContribution(var71, var72, var73) * 0.4D;
                        }

                        var17.back(var5.size());
                        BlockState var70;
                        if(var68 > 0.0D) {
                           var70 = this.defaultBlock;
                        } else if(var39 < var3) {
                           var70 = this.defaultFluid;
                        } else {
                           var70 = AIR;
                        }

                        if(var70 != AIR) {
                           if(var70.getLightEmission() != 0) {
                              var15.set(var53, var39, var62);
                              var12.addLight(var15);
                           }

                           var20.setBlockState(var54, var40, var63, var70, false);
                           var13.update(var54, var39, var63, var70);
                           var14.update(var54, var39, var63, var70);
                        }
                     }
                  }
               }
            }

            var20.release();
         }

         double[][] vars19 = vars11[0];
         vars11[0] = vars11[1];
         vars11[1] = vars19;
      }

   }

   private static double getContribution(int var0, int var1, int var2) {
      int var3 = var0 + 12;
      int var4 = var1 + 12;
      int var5 = var2 + 12;
      return var3 >= 0 && var3 < 24?(var4 >= 0 && var4 < 24?(var5 >= 0 && var5 < 24?(double)BEARD_KERNEL[var5 * 24 * 24 + var3 * 24 + var4]:0.0D):0.0D):0.0D;
   }

   private static double computeContribution(int var0, int var1, int var2) {
      double var3 = (double)(var0 * var0 + var2 * var2);
      double var5 = (double)var1 + 0.5D;
      double var7 = var5 * var5;
      double var9 = Math.pow(2.718281828459045D, -(var7 / 16.0D + var3 / 16.0D));
      double var11 = -var5 * Mth.fastInvSqrt(var7 / 2.0D + var3 / 2.0D) / 2.0D;
      return var11 * var9;
   }
}
