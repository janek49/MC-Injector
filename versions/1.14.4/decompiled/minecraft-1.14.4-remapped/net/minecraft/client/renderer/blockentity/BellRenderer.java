package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BellModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BellBlockEntity;

@ClientJarOnly
public class BellRenderer extends BlockEntityRenderer {
   private static final ResourceLocation BELL_RESOURCE_LOCATION = new ResourceLocation("textures/entity/bell/bell_body.png");
   private final BellModel bellModel = new BellModel();

   public void render(BellBlockEntity bellBlockEntity, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.pushMatrix();
      GlStateManager.enableRescaleNormal();
      this.bindTexture(BELL_RESOURCE_LOCATION);
      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
      float var10 = (float)bellBlockEntity.ticks + var8;
      float var11 = 0.0F;
      float var12 = 0.0F;
      if(bellBlockEntity.shaking) {
         float var13 = Mth.sin(var10 / 3.1415927F) / (4.0F + var10 / 3.0F);
         if(bellBlockEntity.clickDirection == Direction.NORTH) {
            var11 = -var13;
         } else if(bellBlockEntity.clickDirection == Direction.SOUTH) {
            var11 = var13;
         } else if(bellBlockEntity.clickDirection == Direction.EAST) {
            var12 = -var13;
         } else if(bellBlockEntity.clickDirection == Direction.WEST) {
            var12 = var13;
         }
      }

      this.bellModel.render(var11, var12, 0.0625F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.popMatrix();
   }
}
