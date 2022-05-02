package net.minecraft.client.renderer.blockentity;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Calendar;
import net.minecraft.client.model.ChestModel;
import net.minecraft.client.model.LargeChestModel;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ChestBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.EnderChestBlockEntity;
import net.minecraft.world.level.block.entity.LidBlockEntity;
import net.minecraft.world.level.block.entity.TrappedChestBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.ChestType;

@ClientJarOnly
public class ChestRenderer extends BlockEntityRenderer {
   private static final ResourceLocation CHEST_LARGE_TRAP_LOCATION = new ResourceLocation("textures/entity/chest/trapped_double.png");
   private static final ResourceLocation CHEST_LARGE_XMAS_LOCATION = new ResourceLocation("textures/entity/chest/christmas_double.png");
   private static final ResourceLocation CHEST_LARGE_LOCATION = new ResourceLocation("textures/entity/chest/normal_double.png");
   private static final ResourceLocation CHEST_TRAP_LOCATION = new ResourceLocation("textures/entity/chest/trapped.png");
   private static final ResourceLocation CHEST_XMAS_LOCATION = new ResourceLocation("textures/entity/chest/christmas.png");
   private static final ResourceLocation CHEST_LOCATION = new ResourceLocation("textures/entity/chest/normal.png");
   private static final ResourceLocation ENDER_CHEST_LOCATION = new ResourceLocation("textures/entity/chest/ender.png");
   private final ChestModel chestModel = new ChestModel();
   private final ChestModel largeChestModel = new LargeChestModel();
   private boolean xmasTextures;

   public ChestRenderer() {
      Calendar var1 = Calendar.getInstance();
      if(var1.get(2) + 1 == 12 && var1.get(5) >= 24 && var1.get(5) <= 26) {
         this.xmasTextures = true;
      }

   }

   public void render(BlockEntity blockEntity, double var2, double var4, double var6, float var8, int var9) {
      GlStateManager.enableDepthTest();
      GlStateManager.depthFunc(515);
      GlStateManager.depthMask(true);
      BlockState var10 = blockEntity.hasLevel()?blockEntity.getBlockState():(BlockState)Blocks.CHEST.defaultBlockState().setValue(ChestBlock.FACING, Direction.SOUTH);
      ChestType var11 = var10.hasProperty(ChestBlock.TYPE)?(ChestType)var10.getValue(ChestBlock.TYPE):ChestType.SINGLE;
      if(var11 != ChestType.LEFT) {
         boolean var12 = var11 != ChestType.SINGLE;
         ChestModel var13 = this.getChestModelAndBindTexture(blockEntity, var9, var12);
         if(var9 >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scalef(var12?8.0F:4.0F, 4.0F, 1.0F);
            GlStateManager.translatef(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
         } else {
            GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         }

         GlStateManager.pushMatrix();
         GlStateManager.enableRescaleNormal();
         GlStateManager.translatef((float)var2, (float)var4 + 1.0F, (float)var6 + 1.0F);
         GlStateManager.scalef(1.0F, -1.0F, -1.0F);
         float var14 = ((Direction)var10.getValue(ChestBlock.FACING)).toYRot();
         if((double)Math.abs(var14) > 1.0E-5D) {
            GlStateManager.translatef(0.5F, 0.5F, 0.5F);
            GlStateManager.rotatef(var14, 0.0F, 1.0F, 0.0F);
            GlStateManager.translatef(-0.5F, -0.5F, -0.5F);
         }

         this.rotateLid(blockEntity, var8, var13);
         var13.render();
         GlStateManager.disableRescaleNormal();
         GlStateManager.popMatrix();
         GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         if(var9 >= 0) {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
         }

      }
   }

   private ChestModel getChestModelAndBindTexture(BlockEntity blockEntity, int var2, boolean var3) {
      ResourceLocation var4;
      if(var2 >= 0) {
         var4 = BREAKING_LOCATIONS[var2];
      } else if(this.xmasTextures) {
         var4 = var3?CHEST_LARGE_XMAS_LOCATION:CHEST_XMAS_LOCATION;
      } else if(blockEntity instanceof TrappedChestBlockEntity) {
         var4 = var3?CHEST_LARGE_TRAP_LOCATION:CHEST_TRAP_LOCATION;
      } else if(blockEntity instanceof EnderChestBlockEntity) {
         var4 = ENDER_CHEST_LOCATION;
      } else {
         var4 = var3?CHEST_LARGE_LOCATION:CHEST_LOCATION;
      }

      this.bindTexture(var4);
      return var3?this.largeChestModel:this.chestModel;
   }

   private void rotateLid(BlockEntity blockEntity, float var2, ChestModel chestModel) {
      float var4 = ((LidBlockEntity)blockEntity).getOpenNess(var2);
      var4 = 1.0F - var4;
      var4 = 1.0F - var4 * var4 * var4;
      chestModel.getLid().xRot = -(var4 * 1.5707964F);
   }
}
