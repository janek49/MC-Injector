package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.LecternBlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@ClientJarOnly
public class LecternRenderer extends BlockEntityRenderer {
   private static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final BookModel bookModel = new BookModel();

   public void render(LecternBlockEntity lecternBlockEntity, double var2, double var4, double var6, float var8, int var9) {
      BlockState var10 = lecternBlockEntity.getBlockState();
      if(((Boolean)var10.getValue(LecternBlock.HAS_BOOK)).booleanValue()) {
         GlStateManager.pushMatrix();
         GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 1.0F + 0.0625F, (float)var6 + 0.5F);
         float var11 = ((Direction)var10.getValue(LecternBlock.FACING)).getClockWise().toYRot();
         GlStateManager.rotatef(-var11, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(67.5F, 0.0F, 0.0F, 1.0F);
         GlStateManager.translatef(0.0F, -0.125F, 0.0F);
         this.bindTexture(BOOK_LOCATION);
         GlStateManager.enableCull();
         this.bookModel.render(0.0F, 0.1F, 0.9F, 1.2F, 0.0F, 0.0625F);
         GlStateManager.popMatrix();
      }
   }
}
