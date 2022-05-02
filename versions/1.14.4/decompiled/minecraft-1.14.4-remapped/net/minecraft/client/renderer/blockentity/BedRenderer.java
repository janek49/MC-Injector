package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Arrays;
import java.util.Comparator;
import java.util.function.Function;
import java.util.function.IntFunction;
import java.util.function.ToIntFunction;
import net.minecraft.client.model.BedModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.BedBlock;
import net.minecraft.world.level.block.entity.BedBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BedPart;

@ClientJarOnly
public class BedRenderer extends BlockEntityRenderer {
   private static final ResourceLocation[] TEXTURES = (ResourceLocation[])Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map((dyeColor) -> {
      return new ResourceLocation("textures/entity/bed/" + dyeColor.getName() + ".png");
   }).toArray((i) -> {
      return new ResourceLocation[i];
   });
   private final BedModel bedModel = new BedModel();

   public void render(BedBlockEntity bedBlockEntity, double var2, double var4, double var6, float var8, int var9) {
      if(var9 >= 0) {
         this.bindTexture(BREAKING_LOCATIONS[var9]);
         GlStateManager.matrixMode(5890);
         GlStateManager.pushMatrix();
         GlStateManager.scalef(4.0F, 4.0F, 1.0F);
         GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
         GlStateManager.matrixMode(5888);
      } else {
         ResourceLocation var10 = TEXTURES[bedBlockEntity.getColor().getId()];
         if(var10 != null) {
            this.bindTexture(var10);
         }
      }

      if(bedBlockEntity.hasLevel()) {
         BlockState var10 = bedBlockEntity.getBlockState();
         this.renderPiece(var10.getValue(BedBlock.PART) == BedPart.HEAD, var2, var4, var6, (Direction)var10.getValue(BedBlock.FACING));
      } else {
         this.renderPiece(true, var2, var4, var6, Direction.SOUTH);
         this.renderPiece(false, var2, var4, var6 - 1.0D, Direction.SOUTH);
      }

      if(var9 >= 0) {
         GlStateManager.matrixMode(5890);
         GlStateManager.popMatrix();
         GlStateManager.matrixMode(5888);
      }

   }

   private void renderPiece(boolean var1, double var2, double var4, double var6, Direction direction) {
      this.bedModel.preparePiece(var1);
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2, (float)var4 + 0.5625F, (float)var6);
      GlStateManager.rotatef(90.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.translatef(0.5F, 0.5F, 0.5F);
      GlStateManager.rotatef(180.0F + direction.toYRot(), 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
      GlStateManager.enableRescaleNormal();
      this.bedModel.render();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}
