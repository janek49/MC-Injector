package net.minecraft.client.renderer.block;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import it.unimi.dsi.fastutil.longs.Long2FloatLinkedOpenHashMap;
import it.unimi.dsi.fastutil.longs.Long2IntLinkedOpenHashMap;
import java.util.BitSet;
import java.util.List;
import java.util.Random;
import java.util.function.Consumer;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class ModelBlockRenderer {
   private final BlockColors blockColors;
   private static final ThreadLocal CACHE = ThreadLocal.withInitial(() -> {
      return new ModelBlockRenderer.Cache();
   });

   public ModelBlockRenderer(BlockColors blockColors) {
      this.blockColors = blockColors;
   }

   public boolean tesselateBlock(BlockAndBiomeGetter blockAndBiomeGetter, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean var6, Random random, long var8) {
      boolean var10 = Minecraft.useAmbientOcclusion() && blockState.getLightEmission() == 0 && bakedModel.useAmbientOcclusion();

      try {
         return var10?this.tesselateWithAO(blockAndBiomeGetter, bakedModel, blockState, blockPos, bufferBuilder, var6, random, var8):this.tesselateWithoutAO(blockAndBiomeGetter, bakedModel, blockState, blockPos, bufferBuilder, var6, random, var8);
      } catch (Throwable var14) {
         CrashReport var12 = CrashReport.forThrowable(var14, "Tesselating block model");
         CrashReportCategory var13 = var12.addCategory("Block model being tesselated");
         CrashReportCategory.populateBlockDetails(var13, blockPos, blockState);
         var13.setDetail("Using AO", (Object)Boolean.valueOf(var10));
         throw new ReportedException(var12);
      }
   }

   public boolean tesselateWithAO(BlockAndBiomeGetter blockAndBiomeGetter, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean var6, Random random, long var8) {
      boolean var10 = false;
      float[] vars11 = new float[Direction.values().length * 2];
      BitSet var12 = new BitSet(3);
      ModelBlockRenderer.AmbientOcclusionFace var13 = new ModelBlockRenderer.AmbientOcclusionFace();

      for(Direction var17 : Direction.values()) {
         random.setSeed(var8);
         List<BakedQuad> var18 = bakedModel.getQuads(blockState, var17, random);
         if(!var18.isEmpty() && (!var6 || Block.shouldRenderFace(blockState, blockAndBiomeGetter, blockPos, var17))) {
            this.renderModelFaceAO(blockAndBiomeGetter, blockState, blockPos, bufferBuilder, var18, vars11, var12, var13);
            var10 = true;
         }
      }

      random.setSeed(var8);
      List<BakedQuad> var14 = bakedModel.getQuads(blockState, (Direction)null, random);
      if(!var14.isEmpty()) {
         this.renderModelFaceAO(blockAndBiomeGetter, blockState, blockPos, bufferBuilder, var14, vars11, var12, var13);
         var10 = true;
      }

      return var10;
   }

   public boolean tesselateWithoutAO(BlockAndBiomeGetter blockAndBiomeGetter, BakedModel bakedModel, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, boolean var6, Random random, long var8) {
      boolean var10 = false;
      BitSet var11 = new BitSet(3);

      for(Direction var15 : Direction.values()) {
         random.setSeed(var8);
         List<BakedQuad> var16 = bakedModel.getQuads(blockState, var15, random);
         if(!var16.isEmpty() && (!var6 || Block.shouldRenderFace(blockState, blockAndBiomeGetter, blockPos, var15))) {
            int var17 = blockState.getLightColor(blockAndBiomeGetter, blockPos.relative(var15));
            this.renderModelFaceFlat(blockAndBiomeGetter, blockState, blockPos, var17, false, bufferBuilder, var16, var11);
            var10 = true;
         }
      }

      random.setSeed(var8);
      List<BakedQuad> var12 = bakedModel.getQuads(blockState, (Direction)null, random);
      if(!var12.isEmpty()) {
         this.renderModelFaceFlat(blockAndBiomeGetter, blockState, blockPos, -1, true, bufferBuilder, var12, var11);
         var10 = true;
      }

      return var10;
   }

   private void renderModelFaceAO(BlockAndBiomeGetter blockAndBiomeGetter, BlockState blockState, BlockPos blockPos, BufferBuilder bufferBuilder, List list, float[] floats, BitSet bitSet, ModelBlockRenderer.AmbientOcclusionFace modelBlockRenderer$AmbientOcclusionFace) {
      Vec3 var9 = blockState.getOffset(blockAndBiomeGetter, blockPos);
      double var10 = (double)blockPos.getX() + var9.x;
      double var12 = (double)blockPos.getY() + var9.y;
      double var14 = (double)blockPos.getZ() + var9.z;
      int var16 = 0;

      for(int var17 = list.size(); var16 < var17; ++var16) {
         BakedQuad var18 = (BakedQuad)list.get(var16);
         this.calculateShape(blockAndBiomeGetter, blockState, blockPos, var18.getVertices(), var18.getDirection(), floats, bitSet);
         modelBlockRenderer$AmbientOcclusionFace.calculate(blockAndBiomeGetter, blockState, blockPos, var18.getDirection(), floats, bitSet);
         bufferBuilder.putBulkData(var18.getVertices());
         bufferBuilder.faceTex2(modelBlockRenderer$AmbientOcclusionFace.lightmap[0], modelBlockRenderer$AmbientOcclusionFace.lightmap[1], modelBlockRenderer$AmbientOcclusionFace.lightmap[2], modelBlockRenderer$AmbientOcclusionFace.lightmap[3]);
         if(var18.isTinted()) {
            int var19 = this.blockColors.getColor(blockState, blockAndBiomeGetter, blockPos, var18.getTintIndex());
            float var20 = (float)(var19 >> 16 & 255) / 255.0F;
            float var21 = (float)(var19 >> 8 & 255) / 255.0F;
            float var22 = (float)(var19 & 255) / 255.0F;
            bufferBuilder.faceTint(modelBlockRenderer$AmbientOcclusionFace.brightness[0] * var20, modelBlockRenderer$AmbientOcclusionFace.brightness[0] * var21, modelBlockRenderer$AmbientOcclusionFace.brightness[0] * var22, 4);
            bufferBuilder.faceTint(modelBlockRenderer$AmbientOcclusionFace.brightness[1] * var20, modelBlockRenderer$AmbientOcclusionFace.brightness[1] * var21, modelBlockRenderer$AmbientOcclusionFace.brightness[1] * var22, 3);
            bufferBuilder.faceTint(modelBlockRenderer$AmbientOcclusionFace.brightness[2] * var20, modelBlockRenderer$AmbientOcclusionFace.brightness[2] * var21, modelBlockRenderer$AmbientOcclusionFace.brightness[2] * var22, 2);
            bufferBuilder.faceTint(modelBlockRenderer$AmbientOcclusionFace.brightness[3] * var20, modelBlockRenderer$AmbientOcclusionFace.brightness[3] * var21, modelBlockRenderer$AmbientOcclusionFace.brightness[3] * var22, 1);
         } else {
            bufferBuilder.faceTint(modelBlockRenderer$AmbientOcclusionFace.brightness[0], modelBlockRenderer$AmbientOcclusionFace.brightness[0], modelBlockRenderer$AmbientOcclusionFace.brightness[0], 4);
            bufferBuilder.faceTint(modelBlockRenderer$AmbientOcclusionFace.brightness[1], modelBlockRenderer$AmbientOcclusionFace.brightness[1], modelBlockRenderer$AmbientOcclusionFace.brightness[1], 3);
            bufferBuilder.faceTint(modelBlockRenderer$AmbientOcclusionFace.brightness[2], modelBlockRenderer$AmbientOcclusionFace.brightness[2], modelBlockRenderer$AmbientOcclusionFace.brightness[2], 2);
            bufferBuilder.faceTint(modelBlockRenderer$AmbientOcclusionFace.brightness[3], modelBlockRenderer$AmbientOcclusionFace.brightness[3], modelBlockRenderer$AmbientOcclusionFace.brightness[3], 1);
         }

         bufferBuilder.postProcessFacePosition(var10, var12, var14);
      }

   }

   private void calculateShape(BlockAndBiomeGetter blockAndBiomeGetter, BlockState blockState, BlockPos blockPos, int[] ints, Direction direction, @Nullable float[] floats, BitSet bitSet) {
      float var8 = 32.0F;
      float var9 = 32.0F;
      float var10 = 32.0F;
      float var11 = -32.0F;
      float var12 = -32.0F;
      float var13 = -32.0F;

      for(int var14 = 0; var14 < 4; ++var14) {
         float var15 = Float.intBitsToFloat(ints[var14 * 7]);
         float var16 = Float.intBitsToFloat(ints[var14 * 7 + 1]);
         float var17 = Float.intBitsToFloat(ints[var14 * 7 + 2]);
         var8 = Math.min(var8, var15);
         var9 = Math.min(var9, var16);
         var10 = Math.min(var10, var17);
         var11 = Math.max(var11, var15);
         var12 = Math.max(var12, var16);
         var13 = Math.max(var13, var17);
      }

      if(floats != null) {
         floats[Direction.WEST.get3DDataValue()] = var8;
         floats[Direction.EAST.get3DDataValue()] = var11;
         floats[Direction.DOWN.get3DDataValue()] = var9;
         floats[Direction.UP.get3DDataValue()] = var12;
         floats[Direction.NORTH.get3DDataValue()] = var10;
         floats[Direction.SOUTH.get3DDataValue()] = var13;
         int var14 = Direction.values().length;
         floats[Direction.WEST.get3DDataValue() + var14] = 1.0F - var8;
         floats[Direction.EAST.get3DDataValue() + var14] = 1.0F - var11;
         floats[Direction.DOWN.get3DDataValue() + var14] = 1.0F - var9;
         floats[Direction.UP.get3DDataValue() + var14] = 1.0F - var12;
         floats[Direction.NORTH.get3DDataValue() + var14] = 1.0F - var10;
         floats[Direction.SOUTH.get3DDataValue() + var14] = 1.0F - var13;
      }

      float var14 = 1.0E-4F;
      float var15 = 0.9999F;
      switch(direction) {
      case DOWN:
         bitSet.set(1, var8 >= 1.0E-4F || var10 >= 1.0E-4F || var11 <= 0.9999F || var13 <= 0.9999F);
         bitSet.set(0, var9 == var12 && (var9 < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndBiomeGetter, blockPos)));
         break;
      case UP:
         bitSet.set(1, var8 >= 1.0E-4F || var10 >= 1.0E-4F || var11 <= 0.9999F || var13 <= 0.9999F);
         bitSet.set(0, var9 == var12 && (var12 > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndBiomeGetter, blockPos)));
         break;
      case NORTH:
         bitSet.set(1, var8 >= 1.0E-4F || var9 >= 1.0E-4F || var11 <= 0.9999F || var12 <= 0.9999F);
         bitSet.set(0, var10 == var13 && (var10 < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndBiomeGetter, blockPos)));
         break;
      case SOUTH:
         bitSet.set(1, var8 >= 1.0E-4F || var9 >= 1.0E-4F || var11 <= 0.9999F || var12 <= 0.9999F);
         bitSet.set(0, var10 == var13 && (var13 > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndBiomeGetter, blockPos)));
         break;
      case WEST:
         bitSet.set(1, var9 >= 1.0E-4F || var10 >= 1.0E-4F || var12 <= 0.9999F || var13 <= 0.9999F);
         bitSet.set(0, var8 == var11 && (var8 < 1.0E-4F || blockState.isCollisionShapeFullBlock(blockAndBiomeGetter, blockPos)));
         break;
      case EAST:
         bitSet.set(1, var9 >= 1.0E-4F || var10 >= 1.0E-4F || var12 <= 0.9999F || var13 <= 0.9999F);
         bitSet.set(0, var8 == var11 && (var11 > 0.9999F || blockState.isCollisionShapeFullBlock(blockAndBiomeGetter, blockPos)));
      }

   }

   private void renderModelFaceFlat(BlockAndBiomeGetter blockAndBiomeGetter, BlockState blockState, BlockPos blockPos, int var4, boolean var5, BufferBuilder bufferBuilder, List list, BitSet bitSet) {
      Vec3 var9 = blockState.getOffset(blockAndBiomeGetter, blockPos);
      double var10 = (double)blockPos.getX() + var9.x;
      double var12 = (double)blockPos.getY() + var9.y;
      double var14 = (double)blockPos.getZ() + var9.z;
      int var16 = 0;

      for(int var17 = list.size(); var16 < var17; ++var16) {
         BakedQuad var18 = (BakedQuad)list.get(var16);
         if(var5) {
            this.calculateShape(blockAndBiomeGetter, blockState, blockPos, var18.getVertices(), var18.getDirection(), (float[])null, bitSet);
            BlockPos var19 = bitSet.get(0)?blockPos.relative(var18.getDirection()):blockPos;
            var4 = blockState.getLightColor(blockAndBiomeGetter, var19);
         }

         bufferBuilder.putBulkData(var18.getVertices());
         bufferBuilder.faceTex2(var4, var4, var4, var4);
         if(var18.isTinted()) {
            int var19 = this.blockColors.getColor(blockState, blockAndBiomeGetter, blockPos, var18.getTintIndex());
            float var20 = (float)(var19 >> 16 & 255) / 255.0F;
            float var21 = (float)(var19 >> 8 & 255) / 255.0F;
            float var22 = (float)(var19 & 255) / 255.0F;
            bufferBuilder.faceTint(var20, var21, var22, 4);
            bufferBuilder.faceTint(var20, var21, var22, 3);
            bufferBuilder.faceTint(var20, var21, var22, 2);
            bufferBuilder.faceTint(var20, var21, var22, 1);
         }

         bufferBuilder.postProcessFacePosition(var10, var12, var14);
      }

   }

   public void renderModel(BakedModel bakedModel, float var2, float var3, float var4, float var5) {
      this.renderModel((BlockState)null, bakedModel, var2, var3, var4, var5);
   }

   public void renderModel(@Nullable BlockState blockState, BakedModel bakedModel, float var3, float var4, float var5, float var6) {
      Random var7 = new Random();
      long var8 = 42L;

      for(Direction var13 : Direction.values()) {
         var7.setSeed(42L);
         this.renderQuadList(var3, var4, var5, var6, bakedModel.getQuads(blockState, var13, var7));
      }

      var7.setSeed(42L);
      this.renderQuadList(var3, var4, var5, var6, bakedModel.getQuads(blockState, (Direction)null, var7));
   }

   public void renderSingleBlock(BakedModel bakedModel, BlockState blockState, float var3, boolean var4) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      int var5 = this.blockColors.getColor(blockState, (BlockAndBiomeGetter)null, (BlockPos)null, 0);
      float var6 = (float)(var5 >> 16 & 255) / 255.0F;
      float var7 = (float)(var5 >> 8 & 255) / 255.0F;
      float var8 = (float)(var5 & 255) / 255.0F;
      if(!var4) {
         GlStateManager.color4f(var3, var3, var3, 1.0F);
      }

      this.renderModel(blockState, bakedModel, var3, var6, var7, var8);
   }

   private void renderQuadList(float var1, float var2, float var3, float var4, List list) {
      Tesselator var6 = Tesselator.getInstance();
      BufferBuilder var7 = var6.getBuilder();
      int var8 = 0;

      for(int var9 = list.size(); var8 < var9; ++var8) {
         BakedQuad var10 = (BakedQuad)list.get(var8);
         var7.begin(7, DefaultVertexFormat.BLOCK_NORMALS);
         var7.putBulkData(var10.getVertices());
         if(var10.isTinted()) {
            var7.fixupQuadColor(var2 * var1, var3 * var1, var4 * var1);
         } else {
            var7.fixupQuadColor(var1, var1, var1);
         }

         Vec3i var11 = var10.getDirection().getNormal();
         var7.postNormal((float)var11.getX(), (float)var11.getY(), (float)var11.getZ());
         var6.end();
      }

   }

   public static void enableCaching() {
      ((ModelBlockRenderer.Cache)CACHE.get()).enable();
   }

   public static void clearCache() {
      ((ModelBlockRenderer.Cache)CACHE.get()).disable();
   }

   @ClientJarOnly
   public static enum AdjacencyInfo {
      DOWN(new Direction[]{Direction.WEST, Direction.EAST, Direction.NORTH, Direction.SOUTH}, 0.5F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.SOUTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.SOUTH}),
      UP(new Direction[]{Direction.EAST, Direction.WEST, Direction.NORTH, Direction.SOUTH}, 1.0F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.SOUTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.SOUTH}),
      NORTH(new Direction[]{Direction.UP, Direction.DOWN, Direction.EAST, Direction.WEST}, 0.8F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_WEST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_EAST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST}),
      SOUTH(new Direction[]{Direction.WEST, Direction.EAST, Direction.DOWN, Direction.UP}, 0.8F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.WEST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_WEST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.WEST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.WEST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.EAST}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_EAST, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.EAST, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.EAST}),
      WEST(new Direction[]{Direction.UP, Direction.DOWN, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.SOUTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.SOUTH}),
      EAST(new Direction[]{Direction.DOWN, Direction.UP, Direction.NORTH, Direction.SOUTH}, 0.6F, true, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.SOUTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.DOWN, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.NORTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_NORTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.NORTH}, new ModelBlockRenderer.SizeInfo[]{ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.SOUTH, ModelBlockRenderer.SizeInfo.FLIP_UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.FLIP_SOUTH, ModelBlockRenderer.SizeInfo.UP, ModelBlockRenderer.SizeInfo.SOUTH});

      private final Direction[] corners;
      private final boolean doNonCubicWeight;
      private final ModelBlockRenderer.SizeInfo[] vert0Weights;
      private final ModelBlockRenderer.SizeInfo[] vert1Weights;
      private final ModelBlockRenderer.SizeInfo[] vert2Weights;
      private final ModelBlockRenderer.SizeInfo[] vert3Weights;
      private static final ModelBlockRenderer.AdjacencyInfo[] BY_FACING = (ModelBlockRenderer.AdjacencyInfo[])Util.make(new ModelBlockRenderer.AdjacencyInfo[6], (modelBlockRenderer$AdjacencyInfos) -> {
         modelBlockRenderer$AdjacencyInfos[Direction.DOWN.get3DDataValue()] = DOWN;
         modelBlockRenderer$AdjacencyInfos[Direction.UP.get3DDataValue()] = UP;
         modelBlockRenderer$AdjacencyInfos[Direction.NORTH.get3DDataValue()] = NORTH;
         modelBlockRenderer$AdjacencyInfos[Direction.SOUTH.get3DDataValue()] = SOUTH;
         modelBlockRenderer$AdjacencyInfos[Direction.WEST.get3DDataValue()] = WEST;
         modelBlockRenderer$AdjacencyInfos[Direction.EAST.get3DDataValue()] = EAST;
      });

      private AdjacencyInfo(Direction[] corners, float var4, boolean doNonCubicWeight, ModelBlockRenderer.SizeInfo[] vert0Weights, ModelBlockRenderer.SizeInfo[] vert1Weights, ModelBlockRenderer.SizeInfo[] vert2Weights, ModelBlockRenderer.SizeInfo[] vert3Weights) {
         this.corners = corners;
         this.doNonCubicWeight = doNonCubicWeight;
         this.vert0Weights = vert0Weights;
         this.vert1Weights = vert1Weights;
         this.vert2Weights = vert2Weights;
         this.vert3Weights = vert3Weights;
      }

      public static ModelBlockRenderer.AdjacencyInfo fromFacing(Direction facing) {
         return BY_FACING[facing.get3DDataValue()];
      }
   }

   @ClientJarOnly
   class AmbientOcclusionFace {
      private final float[] brightness = new float[4];
      private final int[] lightmap = new int[4];

      public void calculate(BlockAndBiomeGetter blockAndBiomeGetter, BlockState blockState, BlockPos blockPos, Direction direction, float[] floats, BitSet bitSet) {
         BlockPos blockPos = bitSet.get(0)?blockPos.relative(direction):blockPos;
         ModelBlockRenderer.AdjacencyInfo var8 = ModelBlockRenderer.AdjacencyInfo.fromFacing(direction);
         BlockPos.MutableBlockPos var9 = new BlockPos.MutableBlockPos();
         ModelBlockRenderer.Cache var10 = (ModelBlockRenderer.Cache)ModelBlockRenderer.CACHE.get();
         var9.set((Vec3i)blockPos).move(var8.corners[0]);
         BlockState var11 = blockAndBiomeGetter.getBlockState(var9);
         int var12 = var10.getLightColor(var11, blockAndBiomeGetter, var9);
         float var13 = var10.getShadeBrightness(var11, blockAndBiomeGetter, var9);
         var9.set((Vec3i)blockPos).move(var8.corners[1]);
         BlockState var14 = blockAndBiomeGetter.getBlockState(var9);
         int var15 = var10.getLightColor(var14, blockAndBiomeGetter, var9);
         float var16 = var10.getShadeBrightness(var14, blockAndBiomeGetter, var9);
         var9.set((Vec3i)blockPos).move(var8.corners[2]);
         BlockState var17 = blockAndBiomeGetter.getBlockState(var9);
         int var18 = var10.getLightColor(var17, blockAndBiomeGetter, var9);
         float var19 = var10.getShadeBrightness(var17, blockAndBiomeGetter, var9);
         var9.set((Vec3i)blockPos).move(var8.corners[3]);
         BlockState var20 = blockAndBiomeGetter.getBlockState(var9);
         int var21 = var10.getLightColor(var20, blockAndBiomeGetter, var9);
         float var22 = var10.getShadeBrightness(var20, blockAndBiomeGetter, var9);
         var9.set((Vec3i)blockPos).move(var8.corners[0]).move(direction);
         boolean var23 = blockAndBiomeGetter.getBlockState(var9).getLightBlock(blockAndBiomeGetter, var9) == 0;
         var9.set((Vec3i)blockPos).move(var8.corners[1]).move(direction);
         boolean var24 = blockAndBiomeGetter.getBlockState(var9).getLightBlock(blockAndBiomeGetter, var9) == 0;
         var9.set((Vec3i)blockPos).move(var8.corners[2]).move(direction);
         boolean var25 = blockAndBiomeGetter.getBlockState(var9).getLightBlock(blockAndBiomeGetter, var9) == 0;
         var9.set((Vec3i)blockPos).move(var8.corners[3]).move(direction);
         boolean var26 = blockAndBiomeGetter.getBlockState(var9).getLightBlock(blockAndBiomeGetter, var9) == 0;
         float var27;
         int var31;
         if(!var25 && !var23) {
            var27 = var13;
            var31 = var12;
         } else {
            var9.set((Vec3i)blockPos).move(var8.corners[0]).move(var8.corners[2]);
            BlockState var35 = blockAndBiomeGetter.getBlockState(var9);
            var27 = var10.getShadeBrightness(var35, blockAndBiomeGetter, var9);
            var31 = var10.getLightColor(var35, blockAndBiomeGetter, var9);
         }

         float var28;
         int var32;
         if(!var26 && !var23) {
            var28 = var13;
            var32 = var12;
         } else {
            var9.set((Vec3i)blockPos).move(var8.corners[0]).move(var8.corners[3]);
            BlockState var35 = blockAndBiomeGetter.getBlockState(var9);
            var28 = var10.getShadeBrightness(var35, blockAndBiomeGetter, var9);
            var32 = var10.getLightColor(var35, blockAndBiomeGetter, var9);
         }

         float var29;
         int var33;
         if(!var25 && !var24) {
            var29 = var13;
            var33 = var12;
         } else {
            var9.set((Vec3i)blockPos).move(var8.corners[1]).move(var8.corners[2]);
            BlockState var35 = blockAndBiomeGetter.getBlockState(var9);
            var29 = var10.getShadeBrightness(var35, blockAndBiomeGetter, var9);
            var33 = var10.getLightColor(var35, blockAndBiomeGetter, var9);
         }

         float var30;
         int var34;
         if(!var26 && !var24) {
            var30 = var13;
            var34 = var12;
         } else {
            var9.set((Vec3i)blockPos).move(var8.corners[1]).move(var8.corners[3]);
            BlockState var35 = blockAndBiomeGetter.getBlockState(var9);
            var30 = var10.getShadeBrightness(var35, blockAndBiomeGetter, var9);
            var34 = var10.getLightColor(var35, blockAndBiomeGetter, var9);
         }

         int var35 = var10.getLightColor(blockState, blockAndBiomeGetter, blockPos);
         var9.set((Vec3i)blockPos).move(direction);
         BlockState var36 = blockAndBiomeGetter.getBlockState(var9);
         if(bitSet.get(0) || !var36.isSolidRender(blockAndBiomeGetter, var9)) {
            var35 = var10.getLightColor(var36, blockAndBiomeGetter, var9);
         }

         float var37 = bitSet.get(0)?var10.getShadeBrightness(blockAndBiomeGetter.getBlockState(blockPos), blockAndBiomeGetter, blockPos):var10.getShadeBrightness(blockAndBiomeGetter.getBlockState(blockPos), blockAndBiomeGetter, blockPos);
         ModelBlockRenderer.AmbientVertexRemap var38 = ModelBlockRenderer.AmbientVertexRemap.fromFacing(direction);
         if(bitSet.get(1) && var8.doNonCubicWeight) {
            float var39 = (var22 + var13 + var28 + var37) * 0.25F;
            float var40 = (var19 + var13 + var27 + var37) * 0.25F;
            float var41 = (var19 + var16 + var29 + var37) * 0.25F;
            float var42 = (var22 + var16 + var30 + var37) * 0.25F;
            float var43 = floats[var8.vert0Weights[0].shape] * floats[var8.vert0Weights[1].shape];
            float var44 = floats[var8.vert0Weights[2].shape] * floats[var8.vert0Weights[3].shape];
            float var45 = floats[var8.vert0Weights[4].shape] * floats[var8.vert0Weights[5].shape];
            float var46 = floats[var8.vert0Weights[6].shape] * floats[var8.vert0Weights[7].shape];
            float var47 = floats[var8.vert1Weights[0].shape] * floats[var8.vert1Weights[1].shape];
            float var48 = floats[var8.vert1Weights[2].shape] * floats[var8.vert1Weights[3].shape];
            float var49 = floats[var8.vert1Weights[4].shape] * floats[var8.vert1Weights[5].shape];
            float var50 = floats[var8.vert1Weights[6].shape] * floats[var8.vert1Weights[7].shape];
            float var51 = floats[var8.vert2Weights[0].shape] * floats[var8.vert2Weights[1].shape];
            float var52 = floats[var8.vert2Weights[2].shape] * floats[var8.vert2Weights[3].shape];
            float var53 = floats[var8.vert2Weights[4].shape] * floats[var8.vert2Weights[5].shape];
            float var54 = floats[var8.vert2Weights[6].shape] * floats[var8.vert2Weights[7].shape];
            float var55 = floats[var8.vert3Weights[0].shape] * floats[var8.vert3Weights[1].shape];
            float var56 = floats[var8.vert3Weights[2].shape] * floats[var8.vert3Weights[3].shape];
            float var57 = floats[var8.vert3Weights[4].shape] * floats[var8.vert3Weights[5].shape];
            float var58 = floats[var8.vert3Weights[6].shape] * floats[var8.vert3Weights[7].shape];
            this.brightness[var38.vert0] = var39 * var43 + var40 * var44 + var41 * var45 + var42 * var46;
            this.brightness[var38.vert1] = var39 * var47 + var40 * var48 + var41 * var49 + var42 * var50;
            this.brightness[var38.vert2] = var39 * var51 + var40 * var52 + var41 * var53 + var42 * var54;
            this.brightness[var38.vert3] = var39 * var55 + var40 * var56 + var41 * var57 + var42 * var58;
            int var59 = this.blend(var21, var12, var32, var35);
            int var60 = this.blend(var18, var12, var31, var35);
            int var61 = this.blend(var18, var15, var33, var35);
            int var62 = this.blend(var21, var15, var34, var35);
            this.lightmap[var38.vert0] = this.blend(var59, var60, var61, var62, var43, var44, var45, var46);
            this.lightmap[var38.vert1] = this.blend(var59, var60, var61, var62, var47, var48, var49, var50);
            this.lightmap[var38.vert2] = this.blend(var59, var60, var61, var62, var51, var52, var53, var54);
            this.lightmap[var38.vert3] = this.blend(var59, var60, var61, var62, var55, var56, var57, var58);
         } else {
            float var39 = (var22 + var13 + var28 + var37) * 0.25F;
            float var40 = (var19 + var13 + var27 + var37) * 0.25F;
            float var41 = (var19 + var16 + var29 + var37) * 0.25F;
            float var42 = (var22 + var16 + var30 + var37) * 0.25F;
            this.lightmap[var38.vert0] = this.blend(var21, var12, var32, var35);
            this.lightmap[var38.vert1] = this.blend(var18, var12, var31, var35);
            this.lightmap[var38.vert2] = this.blend(var18, var15, var33, var35);
            this.lightmap[var38.vert3] = this.blend(var21, var15, var34, var35);
            this.brightness[var38.vert0] = var39;
            this.brightness[var38.vert1] = var40;
            this.brightness[var38.vert2] = var41;
            this.brightness[var38.vert3] = var42;
         }

      }

      private int blend(int var1, int var2, int var3, int var4) {
         if(var1 == 0) {
            var1 = var4;
         }

         if(var2 == 0) {
            var2 = var4;
         }

         if(var3 == 0) {
            var3 = var4;
         }

         return var1 + var2 + var3 + var4 >> 2 & 16711935;
      }

      private int blend(int var1, int var2, int var3, int var4, float var5, float var6, float var7, float var8) {
         int var9 = (int)((float)(var1 >> 16 & 255) * var5 + (float)(var2 >> 16 & 255) * var6 + (float)(var3 >> 16 & 255) * var7 + (float)(var4 >> 16 & 255) * var8) & 255;
         int var10 = (int)((float)(var1 & 255) * var5 + (float)(var2 & 255) * var6 + (float)(var3 & 255) * var7 + (float)(var4 & 255) * var8) & 255;
         return var9 << 16 | var10;
      }
   }

   @ClientJarOnly
   static enum AmbientVertexRemap {
      DOWN(0, 1, 2, 3),
      UP(2, 3, 0, 1),
      NORTH(3, 0, 1, 2),
      SOUTH(0, 1, 2, 3),
      WEST(3, 0, 1, 2),
      EAST(1, 2, 3, 0);

      private final int vert0;
      private final int vert1;
      private final int vert2;
      private final int vert3;
      private static final ModelBlockRenderer.AmbientVertexRemap[] BY_FACING = (ModelBlockRenderer.AmbientVertexRemap[])Util.make(new ModelBlockRenderer.AmbientVertexRemap[6], (modelBlockRenderer$AmbientVertexRemaps) -> {
         modelBlockRenderer$AmbientVertexRemaps[Direction.DOWN.get3DDataValue()] = DOWN;
         modelBlockRenderer$AmbientVertexRemaps[Direction.UP.get3DDataValue()] = UP;
         modelBlockRenderer$AmbientVertexRemaps[Direction.NORTH.get3DDataValue()] = NORTH;
         modelBlockRenderer$AmbientVertexRemaps[Direction.SOUTH.get3DDataValue()] = SOUTH;
         modelBlockRenderer$AmbientVertexRemaps[Direction.WEST.get3DDataValue()] = WEST;
         modelBlockRenderer$AmbientVertexRemaps[Direction.EAST.get3DDataValue()] = EAST;
      });

      private AmbientVertexRemap(int vert0, int vert1, int vert2, int vert3) {
         this.vert0 = vert0;
         this.vert1 = vert1;
         this.vert2 = vert2;
         this.vert3 = vert3;
      }

      public static ModelBlockRenderer.AmbientVertexRemap fromFacing(Direction facing) {
         return BY_FACING[facing.get3DDataValue()];
      }
   }

   @ClientJarOnly
   static class Cache {
      private boolean enabled;
      private final Long2IntLinkedOpenHashMap colorCache;
      private final Long2FloatLinkedOpenHashMap brightnessCache;

      private Cache() {
         this.colorCache = (Long2IntLinkedOpenHashMap)Util.make(() -> {
            Long2IntLinkedOpenHashMap long2IntLinkedOpenHashMap = new Long2IntLinkedOpenHashMap(100, 0.25F) {
               protected void rehash(int i) {
               }
            };
            long2IntLinkedOpenHashMap.defaultReturnValue(Integer.MAX_VALUE);
            return long2IntLinkedOpenHashMap;
         });
         this.brightnessCache = (Long2FloatLinkedOpenHashMap)Util.make(() -> {
            Long2FloatLinkedOpenHashMap long2FloatLinkedOpenHashMap = new Long2FloatLinkedOpenHashMap(100, 0.25F) {
               protected void rehash(int i) {
               }
            };
            long2FloatLinkedOpenHashMap.defaultReturnValue(Float.NaN);
            return long2FloatLinkedOpenHashMap;
         });
      }

      public void enable() {
         this.enabled = true;
      }

      public void disable() {
         this.enabled = false;
         this.colorCache.clear();
         this.brightnessCache.clear();
      }

      public int getLightColor(BlockState blockState, BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
         long var4 = blockPos.asLong();
         if(this.enabled) {
            int var6 = this.colorCache.get(var4);
            if(var6 != Integer.MAX_VALUE) {
               return var6;
            }
         }

         int var6 = blockState.getLightColor(blockAndBiomeGetter, blockPos);
         if(this.enabled) {
            if(this.colorCache.size() == 100) {
               this.colorCache.removeFirstInt();
            }

            this.colorCache.put(var4, var6);
         }

         return var6;
      }

      public float getShadeBrightness(BlockState blockState, BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
         long var4 = blockPos.asLong();
         if(this.enabled) {
            float var6 = this.brightnessCache.get(var4);
            if(!Float.isNaN(var6)) {
               return var6;
            }
         }

         float var6 = blockState.getShadeBrightness(blockAndBiomeGetter, blockPos);
         if(this.enabled) {
            if(this.brightnessCache.size() == 100) {
               this.brightnessCache.removeFirstFloat();
            }

            this.brightnessCache.put(var4, var6);
         }

         return var6;
      }
   }

   @ClientJarOnly
   public static enum SizeInfo {
      DOWN(Direction.DOWN, false),
      UP(Direction.UP, false),
      NORTH(Direction.NORTH, false),
      SOUTH(Direction.SOUTH, false),
      WEST(Direction.WEST, false),
      EAST(Direction.EAST, false),
      FLIP_DOWN(Direction.DOWN, true),
      FLIP_UP(Direction.UP, true),
      FLIP_NORTH(Direction.NORTH, true),
      FLIP_SOUTH(Direction.SOUTH, true),
      FLIP_WEST(Direction.WEST, true),
      FLIP_EAST(Direction.EAST, true);

      private final int shape;

      private SizeInfo(Direction direction, boolean var4) {
         this.shape = direction.get3DDataValue() + (var4?Direction.values().length:0);
      }
   }
}
