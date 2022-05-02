package net.minecraft.client.renderer.debug;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.debug.DebugRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;

@ClientJarOnly
public class SolidFaceRenderer implements DebugRenderer.SimpleDebugRenderer {
   private final Minecraft minecraft;

   public SolidFaceRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
   }

   public void render(long l) {
      Camera var3 = this.minecraft.gameRenderer.getMainCamera();
      double var4 = var3.getPosition().x;
      double var6 = var3.getPosition().y;
      double var8 = var3.getPosition().z;
      BlockGetter var10 = this.minecraft.player.level;
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.lineWidth(2.0F);
      GlStateManager.disableTexture();
      GlStateManager.depthMask(false);
      BlockPos var11 = new BlockPos(var3.getPosition());

      for(BlockPos var13 : BlockPos.betweenClosed(var11.offset(-6, -6, -6), var11.offset(6, 6, 6))) {
         BlockState var14 = var10.getBlockState(var13);
         if(var14.getBlock() != Blocks.AIR) {
            VoxelShape var15 = var14.getShape(var10, var13);

            for(AABB var17 : var15.toAabbs()) {
               AABB var18 = var17.move(var13).inflate(0.002D).move(-var4, -var6, -var8);
               double var19 = var18.minX;
               double var21 = var18.minY;
               double var23 = var18.minZ;
               double var25 = var18.maxX;
               double var27 = var18.maxY;
               double var29 = var18.maxZ;
               float var31 = 1.0F;
               float var32 = 0.0F;
               float var33 = 0.0F;
               float var34 = 0.5F;
               if(var14.isFaceSturdy(var10, var13, Direction.WEST)) {
                  Tesselator var35 = Tesselator.getInstance();
                  BufferBuilder var36 = var35.getBuilder();
                  var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
                  var36.vertex(var19, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var19, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var19, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var19, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.end();
               }

               if(var14.isFaceSturdy(var10, var13, Direction.SOUTH)) {
                  Tesselator var35 = Tesselator.getInstance();
                  BufferBuilder var36 = var35.getBuilder();
                  var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
                  var36.vertex(var19, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var19, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.end();
               }

               if(var14.isFaceSturdy(var10, var13, Direction.EAST)) {
                  Tesselator var35 = Tesselator.getInstance();
                  BufferBuilder var36 = var35.getBuilder();
                  var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
                  var36.vertex(var25, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.end();
               }

               if(var14.isFaceSturdy(var10, var13, Direction.NORTH)) {
                  Tesselator var35 = Tesselator.getInstance();
                  BufferBuilder var36 = var35.getBuilder();
                  var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
                  var36.vertex(var25, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var19, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var19, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.end();
               }

               if(var14.isFaceSturdy(var10, var13, Direction.DOWN)) {
                  Tesselator var35 = Tesselator.getInstance();
                  BufferBuilder var36 = var35.getBuilder();
                  var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
                  var36.vertex(var19, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var21, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var19, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var21, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.end();
               }

               if(var14.isFaceSturdy(var10, var13, Direction.UP)) {
                  Tesselator var35 = Tesselator.getInstance();
                  BufferBuilder var36 = var35.getBuilder();
                  var36.begin(5, DefaultVertexFormat.POSITION_COLOR);
                  var36.vertex(var19, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var19, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var27, var23).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var36.vertex(var25, var27, var29).color(1.0F, 0.0F, 0.0F, 0.5F).endVertex();
                  var35.end();
               }
            }
         }
      }

      GlStateManager.depthMask(true);
      GlStateManager.enableTexture();
      GlStateManager.disableBlend();
   }
}
