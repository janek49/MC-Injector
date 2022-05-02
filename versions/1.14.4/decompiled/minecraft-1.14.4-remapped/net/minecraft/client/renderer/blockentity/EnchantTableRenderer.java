package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.BookModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.EnchantmentTableBlockEntity;

@ClientJarOnly
public class EnchantTableRenderer extends BlockEntityRenderer {
   private static final ResourceLocation BOOK_LOCATION = new ResourceLocation("textures/entity/enchanting_table_book.png");
   private final BookModel bookModel = new BookModel();

   public void render(EnchantmentTableBlockEntity enchantmentTableBlockEntity, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.pushMatrix();
      GlStateManager.translatef((float)var2 + 0.5F, (float)var4 + 0.75F, (float)var6 + 0.5F);
      float var10 = (float)enchantmentTableBlockEntity.time + var8;
      GlStateManager.translatef(0.0F, 0.1F + Mth.sin(var10 * 0.1F) * 0.01F, 0.0F);

      float var11;
      for(var11 = enchantmentTableBlockEntity.rot - enchantmentTableBlockEntity.oRot; var11 >= 3.1415927F; var11 -= 6.2831855F) {
         ;
      }

      while(var11 < -3.1415927F) {
         var11 += 6.2831855F;
      }

      float var12 = enchantmentTableBlockEntity.oRot + var11 * var8;
      GlStateManager.rotatef(-var12 * 57.295776F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(80.0F, 0.0F, 0.0F, 1.0F);
      this.bindTexture(BOOK_LOCATION);
      float var13 = Mth.lerp(var8, enchantmentTableBlockEntity.oFlip, enchantmentTableBlockEntity.flip) + 0.25F;
      float var14 = Mth.lerp(var8, enchantmentTableBlockEntity.oFlip, enchantmentTableBlockEntity.flip) + 0.75F;
      var13 = (var13 - (float)Mth.fastFloor((double)var13)) * 1.6F - 0.3F;
      var14 = (var14 - (float)Mth.fastFloor((double)var14)) * 1.6F - 0.3F;
      if(var13 < 0.0F) {
         var13 = 0.0F;
      }

      if(var14 < 0.0F) {
         var14 = 0.0F;
      }

      if(var13 > 1.0F) {
         var13 = 1.0F;
      }

      if(var14 > 1.0F) {
         var14 = 1.0F;
      }

      float var15 = Mth.lerp(var8, enchantmentTableBlockEntity.oOpen, enchantmentTableBlockEntity.open);
      GlStateManager.enableCull();
      this.bookModel.render(var10, var13, var14, var15, 0.0F, 0.0625F);
      GlStateManager.popMatrix();
   }
}
