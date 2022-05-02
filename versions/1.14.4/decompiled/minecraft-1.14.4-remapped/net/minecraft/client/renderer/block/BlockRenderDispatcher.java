package net.minecraft.client.renderer.block;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Random;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.block.AnimatedEntityBlockRenderer;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.renderer.block.LiquidBlockRenderer;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.SimpleBakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.world.level.BlockAndBiomeGetter;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.FluidState;

@ClientJarOnly
public class BlockRenderDispatcher implements ResourceManagerReloadListener {
   private final BlockModelShaper blockModelShaper;
   private final ModelBlockRenderer modelRenderer;
   private final AnimatedEntityBlockRenderer entityBlockRenderer = new AnimatedEntityBlockRenderer();
   private final LiquidBlockRenderer liquidBlockRenderer;
   private final Random random = new Random();

   public BlockRenderDispatcher(BlockModelShaper blockModelShaper, BlockColors blockColors) {
      this.blockModelShaper = blockModelShaper;
      this.modelRenderer = new ModelBlockRenderer(blockColors);
      this.liquidBlockRenderer = new LiquidBlockRenderer();
   }

   public BlockModelShaper getBlockModelShaper() {
      return this.blockModelShaper;
   }

   public void renderBreakingTexture(BlockState blockState, BlockPos blockPos, TextureAtlasSprite textureAtlasSprite, BlockAndBiomeGetter blockAndBiomeGetter) {
      if(blockState.getRenderShape() == RenderShape.MODEL) {
         BakedModel var5 = this.blockModelShaper.getBlockModel(blockState);
         long var6 = blockState.getSeed(blockPos);
         BakedModel var8 = (new SimpleBakedModel.Builder(blockState, var5, textureAtlasSprite, this.random, var6)).build();
         this.modelRenderer.tesselateBlock(blockAndBiomeGetter, var8, blockState, blockPos, Tesselator.getInstance().getBuilder(), true, this.random, var6);
      }
   }

   public boolean renderBatched(BlockState blockState, BlockPos blockPos, BlockAndBiomeGetter blockAndBiomeGetter, BufferBuilder bufferBuilder, Random random) {
      try {
         RenderShape var6 = blockState.getRenderShape();
         if(var6 == RenderShape.INVISIBLE) {
            return false;
         } else {
            switch(var6) {
            case MODEL:
               return this.modelRenderer.tesselateBlock(blockAndBiomeGetter, this.getBlockModel(blockState), blockState, blockPos, bufferBuilder, true, random, blockState.getSeed(blockPos));
            case ENTITYBLOCK_ANIMATED:
               return false;
            default:
               return false;
            }
         }
      } catch (Throwable var9) {
         CrashReport var7 = CrashReport.forThrowable(var9, "Tesselating block in world");
         CrashReportCategory var8 = var7.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(var8, blockPos, blockState);
         throw new ReportedException(var7);
      }
   }

   public boolean renderLiquid(BlockPos blockPos, BlockAndBiomeGetter blockAndBiomeGetter, BufferBuilder bufferBuilder, FluidState fluidState) {
      try {
         return this.liquidBlockRenderer.tesselate(blockAndBiomeGetter, blockPos, bufferBuilder, fluidState);
      } catch (Throwable var8) {
         CrashReport var6 = CrashReport.forThrowable(var8, "Tesselating liquid in world");
         CrashReportCategory var7 = var6.addCategory("Block being tesselated");
         CrashReportCategory.populateBlockDetails(var7, blockPos, (BlockState)null);
         throw new ReportedException(var6);
      }
   }

   public ModelBlockRenderer getModelRenderer() {
      return this.modelRenderer;
   }

   public BakedModel getBlockModel(BlockState blockState) {
      return this.blockModelShaper.getBlockModel(blockState);
   }

   public void renderSingleBlock(BlockState blockState, float var2) {
      RenderShape var3 = blockState.getRenderShape();
      if(var3 != RenderShape.INVISIBLE) {
         switch(var3) {
         case MODEL:
            BakedModel var4 = this.getBlockModel(blockState);
            this.modelRenderer.renderSingleBlock(var4, blockState, var2, true);
            break;
         case ENTITYBLOCK_ANIMATED:
            this.entityBlockRenderer.renderSingleBlock(blockState.getBlock(), var2);
         }

      }
   }

   public void onResourceManagerReload(ResourceManager resourceManager) {
      this.liquidBlockRenderer.setupSprites();
   }
}
