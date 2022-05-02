package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.piston.PistonHeadBlock;
import net.minecraft.world.level.block.piston.PistonMovingBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.PistonType;

@ClientJarOnly
public class PistonHeadRenderer extends BlockEntityRenderer {
   private final BlockRenderDispatcher blockRenderer = Minecraft.getInstance().getBlockRenderer();

   public void render(PistonMovingBlockEntity pistonMovingBlockEntity, double var2, double var4, double var6, float var8, int var9) {
      BlockPos var10 = pistonMovingBlockEntity.getBlockPos().relative(pistonMovingBlockEntity.getMovementDirection().getOpposite());
      BlockState var11 = pistonMovingBlockEntity.getMovedState();
      if(!var11.isAir() && pistonMovingBlockEntity.getProgress(var8) < 1.0F) {
         Tesselator var12 = Tesselator.getInstance();
         BufferBuilder var13 = var12.getBuilder();
         this.bindTexture(TextureAtlas.LOCATION_BLOCKS);
         Lighting.turnOff();
         GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
         GlStateManager.enableBlend();
         GlStateManager.disableCull();
         if(Minecraft.useAmbientOcclusion()) {
            GlStateManager.shadeModel(7425);
         } else {
            GlStateManager.shadeModel(7424);
         }

         ModelBlockRenderer.enableCaching();
         var13.begin(7, DefaultVertexFormat.BLOCK);
         var13.offset(var2 - (double)var10.getX() + (double)pistonMovingBlockEntity.getXOff(var8), var4 - (double)var10.getY() + (double)pistonMovingBlockEntity.getYOff(var8), var6 - (double)var10.getZ() + (double)pistonMovingBlockEntity.getZOff(var8));
         Level var14 = this.getLevel();
         if(var11.getBlock() == Blocks.PISTON_HEAD && pistonMovingBlockEntity.getProgress(var8) <= 4.0F) {
            var11 = (BlockState)var11.setValue(PistonHeadBlock.SHORT, Boolean.valueOf(true));
            this.renderBlock(var10, var11, var13, var14, false);
         } else if(pistonMovingBlockEntity.isSourcePiston() && !pistonMovingBlockEntity.isExtending()) {
            PistonType var15 = var11.getBlock() == Blocks.STICKY_PISTON?PistonType.STICKY:PistonType.DEFAULT;
            BlockState var16 = (BlockState)((BlockState)Blocks.PISTON_HEAD.defaultBlockState().setValue(PistonHeadBlock.TYPE, var15)).setValue(PistonHeadBlock.FACING, var11.getValue(PistonBaseBlock.FACING));
            var16 = (BlockState)var16.setValue(PistonHeadBlock.SHORT, Boolean.valueOf(pistonMovingBlockEntity.getProgress(var8) >= 0.5F));
            this.renderBlock(var10, var16, var13, var14, false);
            BlockPos var17 = var10.relative(pistonMovingBlockEntity.getMovementDirection());
            var13.offset(var2 - (double)var17.getX(), var4 - (double)var17.getY(), var6 - (double)var17.getZ());
            var11 = (BlockState)var11.setValue(PistonBaseBlock.EXTENDED, Boolean.valueOf(true));
            this.renderBlock(var17, var11, var13, var14, true);
         } else {
            this.renderBlock(var10, var11, var13, var14, false);
         }

         var13.offset(0.0D, 0.0D, 0.0D);
         var12.end();
         ModelBlockRenderer.clearCache();
         Lighting.turnOn();
      }
   }

   private boolean renderBlock(BlockPos blockPos, BlockState blockState, BufferBuilder bufferBuilder, Level level, boolean var5) {
      return this.blockRenderer.getModelRenderer().tesselateBlock(level, this.blockRenderer.getBlockModel(blockState), blockState, blockPos, bufferBuilder, var5, new Random(), blockState.getSeed(blockPos));
   }
}
