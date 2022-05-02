package net.minecraft.client.renderer.block;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.StainedGlassBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

@ClientJarOnly
public class LiquidBlockRenderer {
   private final TextureAtlasSprite[] lavaIcons = new TextureAtlasSprite[2];
   private final TextureAtlasSprite[] waterIcons = new TextureAtlasSprite[2];
   private TextureAtlasSprite waterOverlay;

   protected void setupSprites() {
      TextureAtlas var1 = Minecraft.getInstance().getTextureAtlas();
      this.lavaIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.LAVA.defaultBlockState()).getParticleIcon();
      this.lavaIcons[1] = var1.getSprite(ModelBakery.LAVA_FLOW);
      this.waterIcons[0] = Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(Blocks.WATER.defaultBlockState()).getParticleIcon();
      this.waterIcons[1] = var1.getSprite(ModelBakery.WATER_FLOW);
      this.waterOverlay = var1.getSprite(ModelBakery.WATER_OVERLAY);
   }

   private static boolean isNeighborSameFluid(BlockGetter blockGetter, BlockPos blockPos, Direction direction, FluidState fluidState) {
      BlockPos blockPos = blockPos.relative(direction);
      FluidState var5 = blockGetter.getFluidState(blockPos);
      return var5.getType().isSame(fluidState.getType());
   }

   private static boolean isFaceOccluded(BlockGetter blockGetter, BlockPos blockPos, Direction direction, float var3) {
      BlockPos blockPos = blockPos.relative(direction);
      BlockState var5 = blockGetter.getBlockState(blockPos);
      if(var5.canOcclude()) {
         VoxelShape var6 = Shapes.box(0.0D, 0.0D, 0.0D, 1.0D, (double)var3, 1.0D);
         VoxelShape var7 = var5.getOcclusionShape(blockGetter, blockPos);
         return Shapes.blockOccudes(var6, var7, direction);
      } else {
         return false;
      }
   }

   public boolean tesselate(BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos, BufferBuilder bufferBuilder, FluidState fluidState) {
      boolean var5 = fluidState.is(FluidTags.LAVA);
      TextureAtlasSprite[] vars6 = var5?this.lavaIcons:this.waterIcons;
      int var7 = var5?16777215:BiomeColors.getAverageWaterColor(blockAndBiomeGetter, blockPos);
      float var8 = (float)(var7 >> 16 & 255) / 255.0F;
      float var9 = (float)(var7 >> 8 & 255) / 255.0F;
      float var10 = (float)(var7 & 255) / 255.0F;
      boolean var11 = !isNeighborSameFluid(blockAndBiomeGetter, blockPos, Direction.UP, fluidState);
      boolean var12 = !isNeighborSameFluid(blockAndBiomeGetter, blockPos, Direction.DOWN, fluidState) && !isFaceOccluded(blockAndBiomeGetter, blockPos, Direction.DOWN, 0.8888889F);
      boolean var13 = !isNeighborSameFluid(blockAndBiomeGetter, blockPos, Direction.NORTH, fluidState);
      boolean var14 = !isNeighborSameFluid(blockAndBiomeGetter, blockPos, Direction.SOUTH, fluidState);
      boolean var15 = !isNeighborSameFluid(blockAndBiomeGetter, blockPos, Direction.WEST, fluidState);
      boolean var16 = !isNeighborSameFluid(blockAndBiomeGetter, blockPos, Direction.EAST, fluidState);
      if(!var11 && !var12 && !var16 && !var15 && !var13 && !var14) {
         return false;
      } else {
         boolean var17 = false;
         float var18 = 0.5F;
         float var19 = 1.0F;
         float var20 = 0.8F;
         float var21 = 0.6F;
         float var22 = this.getWaterHeight(blockAndBiomeGetter, blockPos, fluidState.getType());
         float var23 = this.getWaterHeight(blockAndBiomeGetter, blockPos.south(), fluidState.getType());
         float var24 = this.getWaterHeight(blockAndBiomeGetter, blockPos.east().south(), fluidState.getType());
         float var25 = this.getWaterHeight(blockAndBiomeGetter, blockPos.east(), fluidState.getType());
         double var26 = (double)blockPos.getX();
         double var28 = (double)blockPos.getY();
         double var30 = (double)blockPos.getZ();
         float var32 = 0.001F;
         if(var11 && !isFaceOccluded(blockAndBiomeGetter, blockPos, Direction.UP, Math.min(Math.min(var22, var23), Math.min(var24, var25)))) {
            var17 = true;
            var22 -= 0.001F;
            var23 -= 0.001F;
            var24 -= 0.001F;
            var25 -= 0.001F;
            Vec3 var41 = fluidState.getFlow(blockAndBiomeGetter, blockPos);
            float var33;
            float var34;
            float var35;
            float var36;
            float var37;
            float var38;
            float var39;
            float var40;
            if(var41.x == 0.0D && var41.z == 0.0D) {
               TextureAtlasSprite var42 = vars6[0];
               var33 = var42.getU(0.0D);
               var37 = var42.getV(0.0D);
               var34 = var33;
               var38 = var42.getV(16.0D);
               var35 = var42.getU(16.0D);
               var39 = var38;
               var36 = var35;
               var40 = var37;
            } else {
               TextureAtlasSprite var42 = vars6[1];
               float var43 = (float)Mth.atan2(var41.z, var41.x) - 1.5707964F;
               float var44 = Mth.sin(var43) * 0.25F;
               float var45 = Mth.cos(var43) * 0.25F;
               float var46 = 8.0F;
               var33 = var42.getU((double)(8.0F + (-var45 - var44) * 16.0F));
               var37 = var42.getV((double)(8.0F + (-var45 + var44) * 16.0F));
               var34 = var42.getU((double)(8.0F + (-var45 + var44) * 16.0F));
               var38 = var42.getV((double)(8.0F + (var45 + var44) * 16.0F));
               var35 = var42.getU((double)(8.0F + (var45 + var44) * 16.0F));
               var39 = var42.getV((double)(8.0F + (var45 - var44) * 16.0F));
               var36 = var42.getU((double)(8.0F + (var45 - var44) * 16.0F));
               var40 = var42.getV((double)(8.0F + (-var45 - var44) * 16.0F));
            }

            float var42 = (var33 + var34 + var35 + var36) / 4.0F;
            float var43 = (var37 + var38 + var39 + var40) / 4.0F;
            float var44 = (float)vars6[0].getWidth() / (vars6[0].getU1() - vars6[0].getU0());
            float var45 = (float)vars6[0].getHeight() / (vars6[0].getV1() - vars6[0].getV0());
            float var46 = 4.0F / Math.max(var45, var44);
            var33 = Mth.lerp(var46, var33, var42);
            var34 = Mth.lerp(var46, var34, var42);
            var35 = Mth.lerp(var46, var35, var42);
            var36 = Mth.lerp(var46, var36, var42);
            var37 = Mth.lerp(var46, var37, var43);
            var38 = Mth.lerp(var46, var38, var43);
            var39 = Mth.lerp(var46, var39, var43);
            var40 = Mth.lerp(var46, var40, var43);
            int var47 = this.getLightColor(blockAndBiomeGetter, blockPos);
            int var48 = var47 >> 16 & '\uffff';
            int var49 = var47 & '\uffff';
            float var50 = 1.0F * var8;
            float var51 = 1.0F * var9;
            float var52 = 1.0F * var10;
            bufferBuilder.vertex(var26 + 0.0D, var28 + (double)var22, var30 + 0.0D).color(var50, var51, var52, 1.0F).uv((double)var33, (double)var37).uv2(var48, var49).endVertex();
            bufferBuilder.vertex(var26 + 0.0D, var28 + (double)var23, var30 + 1.0D).color(var50, var51, var52, 1.0F).uv((double)var34, (double)var38).uv2(var48, var49).endVertex();
            bufferBuilder.vertex(var26 + 1.0D, var28 + (double)var24, var30 + 1.0D).color(var50, var51, var52, 1.0F).uv((double)var35, (double)var39).uv2(var48, var49).endVertex();
            bufferBuilder.vertex(var26 + 1.0D, var28 + (double)var25, var30 + 0.0D).color(var50, var51, var52, 1.0F).uv((double)var36, (double)var40).uv2(var48, var49).endVertex();
            if(fluidState.shouldRenderBackwardUpFace(blockAndBiomeGetter, blockPos.above())) {
               bufferBuilder.vertex(var26 + 0.0D, var28 + (double)var22, var30 + 0.0D).color(var50, var51, var52, 1.0F).uv((double)var33, (double)var37).uv2(var48, var49).endVertex();
               bufferBuilder.vertex(var26 + 1.0D, var28 + (double)var25, var30 + 0.0D).color(var50, var51, var52, 1.0F).uv((double)var36, (double)var40).uv2(var48, var49).endVertex();
               bufferBuilder.vertex(var26 + 1.0D, var28 + (double)var24, var30 + 1.0D).color(var50, var51, var52, 1.0F).uv((double)var35, (double)var39).uv2(var48, var49).endVertex();
               bufferBuilder.vertex(var26 + 0.0D, var28 + (double)var23, var30 + 1.0D).color(var50, var51, var52, 1.0F).uv((double)var34, (double)var38).uv2(var48, var49).endVertex();
            }
         }

         if(var12) {
            float var33 = vars6[0].getU0();
            float var34 = vars6[0].getU1();
            float var35 = vars6[0].getV0();
            float var36 = vars6[0].getV1();
            int var37 = this.getLightColor(blockAndBiomeGetter, blockPos.below());
            int var38 = var37 >> 16 & '\uffff';
            int var39 = var37 & '\uffff';
            float var40 = 0.5F * var8;
            float var41 = 0.5F * var9;
            float var42 = 0.5F * var10;
            bufferBuilder.vertex(var26, var28, var30 + 1.0D).color(var40, var41, var42, 1.0F).uv((double)var33, (double)var36).uv2(var38, var39).endVertex();
            bufferBuilder.vertex(var26, var28, var30).color(var40, var41, var42, 1.0F).uv((double)var33, (double)var35).uv2(var38, var39).endVertex();
            bufferBuilder.vertex(var26 + 1.0D, var28, var30).color(var40, var41, var42, 1.0F).uv((double)var34, (double)var35).uv2(var38, var39).endVertex();
            bufferBuilder.vertex(var26 + 1.0D, var28, var30 + 1.0D).color(var40, var41, var42, 1.0F).uv((double)var34, (double)var36).uv2(var38, var39).endVertex();
            var17 = true;
         }

         for(int var33 = 0; var33 < 4; ++var33) {
            float var34;
            float var35;
            double var36;
            double var38;
            double var40;
            double var42;
            Direction var44;
            boolean var45;
            if(var33 == 0) {
               var34 = var22;
               var35 = var25;
               var36 = var26;
               var40 = var26 + 1.0D;
               var38 = var30 + 0.0010000000474974513D;
               var42 = var30 + 0.0010000000474974513D;
               var44 = Direction.NORTH;
               var45 = var13;
            } else if(var33 == 1) {
               var34 = var24;
               var35 = var23;
               var36 = var26 + 1.0D;
               var40 = var26;
               var38 = var30 + 1.0D - 0.0010000000474974513D;
               var42 = var30 + 1.0D - 0.0010000000474974513D;
               var44 = Direction.SOUTH;
               var45 = var14;
            } else if(var33 == 2) {
               var34 = var23;
               var35 = var22;
               var36 = var26 + 0.0010000000474974513D;
               var40 = var26 + 0.0010000000474974513D;
               var38 = var30 + 1.0D;
               var42 = var30;
               var44 = Direction.WEST;
               var45 = var15;
            } else {
               var34 = var25;
               var35 = var24;
               var36 = var26 + 1.0D - 0.0010000000474974513D;
               var40 = var26 + 1.0D - 0.0010000000474974513D;
               var38 = var30;
               var42 = var30 + 1.0D;
               var44 = Direction.EAST;
               var45 = var16;
            }

            if(var45 && !isFaceOccluded(blockAndBiomeGetter, blockPos, var44, Math.max(var34, var35))) {
               var17 = true;
               BlockPos var46 = blockPos.relative(var44);
               TextureAtlasSprite var47 = vars6[1];
               if(!var5) {
                  Block var48 = blockAndBiomeGetter.getBlockState(var46).getBlock();
                  if(var48 == Blocks.GLASS || var48 instanceof StainedGlassBlock) {
                     var47 = this.waterOverlay;
                  }
               }

               float var48 = var47.getU(0.0D);
               float var49 = var47.getU(8.0D);
               float var50 = var47.getV((double)((1.0F - var34) * 16.0F * 0.5F));
               float var51 = var47.getV((double)((1.0F - var35) * 16.0F * 0.5F));
               float var52 = var47.getV(8.0D);
               int var53 = this.getLightColor(blockAndBiomeGetter, var46);
               int var54 = var53 >> 16 & '\uffff';
               int var55 = var53 & '\uffff';
               float var56 = var33 < 2?0.8F:0.6F;
               float var57 = 1.0F * var56 * var8;
               float var58 = 1.0F * var56 * var9;
               float var59 = 1.0F * var56 * var10;
               bufferBuilder.vertex(var36, var28 + (double)var34, var38).color(var57, var58, var59, 1.0F).uv((double)var48, (double)var50).uv2(var54, var55).endVertex();
               bufferBuilder.vertex(var40, var28 + (double)var35, var42).color(var57, var58, var59, 1.0F).uv((double)var49, (double)var51).uv2(var54, var55).endVertex();
               bufferBuilder.vertex(var40, var28 + 0.0D, var42).color(var57, var58, var59, 1.0F).uv((double)var49, (double)var52).uv2(var54, var55).endVertex();
               bufferBuilder.vertex(var36, var28 + 0.0D, var38).color(var57, var58, var59, 1.0F).uv((double)var48, (double)var52).uv2(var54, var55).endVertex();
               if(var47 != this.waterOverlay) {
                  bufferBuilder.vertex(var36, var28 + 0.0D, var38).color(var57, var58, var59, 1.0F).uv((double)var48, (double)var52).uv2(var54, var55).endVertex();
                  bufferBuilder.vertex(var40, var28 + 0.0D, var42).color(var57, var58, var59, 1.0F).uv((double)var49, (double)var52).uv2(var54, var55).endVertex();
                  bufferBuilder.vertex(var40, var28 + (double)var35, var42).color(var57, var58, var59, 1.0F).uv((double)var49, (double)var51).uv2(var54, var55).endVertex();
                  bufferBuilder.vertex(var36, var28 + (double)var34, var38).color(var57, var58, var59, 1.0F).uv((double)var48, (double)var50).uv2(var54, var55).endVertex();
               }
            }
         }

         return var17;
      }
   }

   private int getLightColor(BlockAndBiomeGetter blockAndBiomeGetter, BlockPos blockPos) {
      int var3 = blockAndBiomeGetter.getLightColor(blockPos, 0);
      int var4 = blockAndBiomeGetter.getLightColor(blockPos.above(), 0);
      int var5 = var3 & 255;
      int var6 = var4 & 255;
      int var7 = var3 >> 16 & 255;
      int var8 = var4 >> 16 & 255;
      return (var5 > var6?var5:var6) | (var7 > var8?var7:var8) << 16;
   }

   private float getWaterHeight(BlockGetter blockGetter, BlockPos blockPos, Fluid fluid) {
      int var4 = 0;
      float var5 = 0.0F;

      for(int var6 = 0; var6 < 4; ++var6) {
         BlockPos var7 = blockPos.offset(-(var6 & 1), 0, -(var6 >> 1 & 1));
         if(blockGetter.getFluidState(var7.above()).getType().isSame(fluid)) {
            return 1.0F;
         }

         FluidState var8 = blockGetter.getFluidState(var7);
         if(var8.getType().isSame(fluid)) {
            float var9 = var8.getHeight(blockGetter, var7);
            if(var9 >= 0.8F) {
               var5 += var9 * 10.0F;
               var4 += 10;
            } else {
               var5 += var9;
               ++var4;
            }
         } else if(!blockGetter.getBlockState(var7).getMaterial().isSolid()) {
            ++var4;
         }
      }

      return var5 / (float)var4;
   }
}
