package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.base.MoreObjects;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.Tesselator;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.MapItem;
import net.minecraft.world.level.BlockLayer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.saveddata.maps.MapItemSavedData;

@ClientJarOnly
public class ItemInHandRenderer {
   private static final ResourceLocation MAP_BACKGROUND_LOCATION = new ResourceLocation("textures/map/map_background.png");
   private static final ResourceLocation UNDERWATER_LOCATION = new ResourceLocation("textures/misc/underwater.png");
   private final Minecraft minecraft;
   private ItemStack mainHandItem = ItemStack.EMPTY;
   private ItemStack offHandItem = ItemStack.EMPTY;
   private float mainHandHeight;
   private float oMainHandHeight;
   private float offHandHeight;
   private float oOffHandHeight;
   private final EntityRenderDispatcher entityRenderDispatcher;
   private final ItemRenderer itemRenderer;

   public ItemInHandRenderer(Minecraft minecraft) {
      this.minecraft = minecraft;
      this.entityRenderDispatcher = minecraft.getEntityRenderDispatcher();
      this.itemRenderer = minecraft.getItemRenderer();
   }

   public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType itemTransforms$TransformType) {
      this.renderItem(livingEntity, itemStack, itemTransforms$TransformType, false);
   }

   public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType itemTransforms$TransformType, boolean var4) {
      if(!itemStack.isEmpty()) {
         Item var5 = itemStack.getItem();
         Block var6 = Block.byItem(var5);
         GlStateManager.pushMatrix();
         boolean var7 = this.itemRenderer.isGui3d(itemStack) && var6.getRenderLayer() == BlockLayer.TRANSLUCENT;
         if(var7) {
            GlStateManager.depthMask(false);
         }

         this.itemRenderer.renderWithMobState(itemStack, livingEntity, itemTransforms$TransformType, var4);
         if(var7) {
            GlStateManager.depthMask(true);
         }

         GlStateManager.popMatrix();
      }
   }

   private void enableLight(float var1, float var2) {
      GlStateManager.pushMatrix();
      GlStateManager.rotatef(var1, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(var2, 0.0F, 1.0F, 0.0F);
      Lighting.turnOn();
      GlStateManager.popMatrix();
   }

   private void setLightValue() {
      AbstractClientPlayer var1 = this.minecraft.player;
      int var2 = this.minecraft.level.getLightColor(new BlockPos(var1.x, var1.y + (double)var1.getEyeHeight(), var1.z), 0);
      float var3 = (float)(var2 & '\uffff');
      float var4 = (float)(var2 >> 16);
      GLX.glMultiTexCoord2f(GLX.GL_TEXTURE1, var3, var4);
   }

   private void setPlayerBob(float playerBob) {
      LocalPlayer var2 = this.minecraft.player;
      float var3 = Mth.lerp(playerBob, var2.xBobO, var2.xBob);
      float var4 = Mth.lerp(playerBob, var2.yBobO, var2.yBob);
      GlStateManager.rotatef((var2.getViewXRot(playerBob) - var3) * 0.1F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((var2.getViewYRot(playerBob) - var4) * 0.1F, 0.0F, 1.0F, 0.0F);
   }

   private float calculateMapTilt(float f) {
      float var2 = 1.0F - f / 45.0F + 0.1F;
      var2 = Mth.clamp(var2, 0.0F, 1.0F);
      var2 = -Mth.cos(var2 * 3.1415927F) * 0.5F + 0.5F;
      return var2;
   }

   private void renderMapHands() {
      if(!this.minecraft.player.isInvisible()) {
         GlStateManager.disableCull();
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
         this.renderMapHand(HumanoidArm.RIGHT);
         this.renderMapHand(HumanoidArm.LEFT);
         GlStateManager.popMatrix();
         GlStateManager.enableCull();
      }
   }

   private void renderMapHand(HumanoidArm humanoidArm) {
      this.minecraft.getTextureManager().bind(this.minecraft.player.getSkinTextureLocation());
      EntityRenderer<AbstractClientPlayer> var2 = this.entityRenderDispatcher.getRenderer((Entity)this.minecraft.player);
      PlayerRenderer var3 = (PlayerRenderer)var2;
      GlStateManager.pushMatrix();
      float var4 = humanoidArm == HumanoidArm.RIGHT?1.0F:-1.0F;
      GlStateManager.rotatef(92.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(var4 * -41.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.translatef(var4 * 0.3F, -1.1F, 0.45F);
      if(humanoidArm == HumanoidArm.RIGHT) {
         var3.renderRightHand(this.minecraft.player);
      } else {
         var3.renderLeftHand(this.minecraft.player);
      }

      GlStateManager.popMatrix();
   }

   private void renderOneHandedMap(float var1, HumanoidArm humanoidArm, float var3, ItemStack itemStack) {
      float var5 = humanoidArm == HumanoidArm.RIGHT?1.0F:-1.0F;
      GlStateManager.translatef(var5 * 0.125F, -0.125F, 0.0F);
      if(!this.minecraft.player.isInvisible()) {
         GlStateManager.pushMatrix();
         GlStateManager.rotatef(var5 * 10.0F, 0.0F, 0.0F, 1.0F);
         this.renderPlayerArm(var1, var3, humanoidArm);
         GlStateManager.popMatrix();
      }

      GlStateManager.pushMatrix();
      GlStateManager.translatef(var5 * 0.51F, -0.08F + var1 * -1.2F, -0.75F);
      float var6 = Mth.sqrt(var3);
      float var7 = Mth.sin(var6 * 3.1415927F);
      float var8 = -0.5F * var7;
      float var9 = 0.4F * Mth.sin(var6 * 6.2831855F);
      float var10 = -0.3F * Mth.sin(var3 * 3.1415927F);
      GlStateManager.translatef(var5 * var8, var9 - 0.3F * var7, var10);
      GlStateManager.rotatef(var7 * -45.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(var5 * var7 * -30.0F, 0.0F, 1.0F, 0.0F);
      this.renderMap(itemStack);
      GlStateManager.popMatrix();
   }

   private void renderTwoHandedMap(float var1, float var2, float var3) {
      float var4 = Mth.sqrt(var3);
      float var5 = -0.2F * Mth.sin(var3 * 3.1415927F);
      float var6 = -0.4F * Mth.sin(var4 * 3.1415927F);
      GlStateManager.translatef(0.0F, -var5 / 2.0F, var6);
      float var7 = this.calculateMapTilt(var1);
      GlStateManager.translatef(0.0F, 0.04F + var2 * -1.2F + var7 * -0.5F, -0.72F);
      GlStateManager.rotatef(var7 * -85.0F, 1.0F, 0.0F, 0.0F);
      this.renderMapHands();
      float var8 = Mth.sin(var4 * 3.1415927F);
      GlStateManager.rotatef(var8 * 20.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.scalef(2.0F, 2.0F, 2.0F);
      this.renderMap(this.mainHandItem);
   }

   private void renderMap(ItemStack itemStack) {
      GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.scalef(0.38F, 0.38F, 0.38F);
      GlStateManager.disableLighting();
      this.minecraft.getTextureManager().bind(MAP_BACKGROUND_LOCATION);
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      GlStateManager.translatef(-0.5F, -0.5F, 0.0F);
      GlStateManager.scalef(0.0078125F, 0.0078125F, 0.0078125F);
      var3.begin(7, DefaultVertexFormat.POSITION_TEX);
      var3.vertex(-7.0D, 135.0D, 0.0D).uv(0.0D, 1.0D).endVertex();
      var3.vertex(135.0D, 135.0D, 0.0D).uv(1.0D, 1.0D).endVertex();
      var3.vertex(135.0D, -7.0D, 0.0D).uv(1.0D, 0.0D).endVertex();
      var3.vertex(-7.0D, -7.0D, 0.0D).uv(0.0D, 0.0D).endVertex();
      var2.end();
      MapItemSavedData var4 = MapItem.getOrCreateSavedData(itemStack, this.minecraft.level);
      if(var4 != null) {
         this.minecraft.gameRenderer.getMapRenderer().render(var4, false);
      }

      GlStateManager.enableLighting();
   }

   private void renderPlayerArm(float var1, float var2, HumanoidArm humanoidArm) {
      boolean var4 = humanoidArm != HumanoidArm.LEFT;
      float var5 = var4?1.0F:-1.0F;
      float var6 = Mth.sqrt(var2);
      float var7 = -0.3F * Mth.sin(var6 * 3.1415927F);
      float var8 = 0.4F * Mth.sin(var6 * 6.2831855F);
      float var9 = -0.4F * Mth.sin(var2 * 3.1415927F);
      GlStateManager.translatef(var5 * (var7 + 0.64000005F), var8 + -0.6F + var1 * -0.6F, var9 + -0.71999997F);
      GlStateManager.rotatef(var5 * 45.0F, 0.0F, 1.0F, 0.0F);
      float var10 = Mth.sin(var2 * var2 * 3.1415927F);
      float var11 = Mth.sin(var6 * 3.1415927F);
      GlStateManager.rotatef(var5 * var11 * 70.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var5 * var10 * -20.0F, 0.0F, 0.0F, 1.0F);
      AbstractClientPlayer var12 = this.minecraft.player;
      this.minecraft.getTextureManager().bind(var12.getSkinTextureLocation());
      GlStateManager.translatef(var5 * -1.0F, 3.6F, 3.5F);
      GlStateManager.rotatef(var5 * 120.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(200.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(var5 * -135.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.translatef(var5 * 5.6F, 0.0F, 0.0F);
      PlayerRenderer var13 = (PlayerRenderer)this.entityRenderDispatcher.getRenderer((Entity)var12);
      GlStateManager.disableCull();
      if(var4) {
         var13.renderRightHand(var12);
      } else {
         var13.renderLeftHand(var12);
      }

      GlStateManager.enableCull();
   }

   private void applyEatTransform(float var1, HumanoidArm humanoidArm, ItemStack itemStack) {
      float var4 = (float)this.minecraft.player.getUseItemRemainingTicks() - var1 + 1.0F;
      float var5 = var4 / (float)itemStack.getUseDuration();
      if(var5 < 0.8F) {
         float var6 = Mth.abs(Mth.cos(var4 / 4.0F * 3.1415927F) * 0.1F);
         GlStateManager.translatef(0.0F, var6, 0.0F);
      }

      float var6 = 1.0F - (float)Math.pow((double)var5, 27.0D);
      int var7 = humanoidArm == HumanoidArm.RIGHT?1:-1;
      GlStateManager.translatef(var6 * 0.6F * (float)var7, var6 * -0.5F, var6 * 0.0F);
      GlStateManager.rotatef((float)var7 * var6 * 90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotatef(var6 * 10.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((float)var7 * var6 * 30.0F, 0.0F, 0.0F, 1.0F);
   }

   private void applyItemArmAttackTransform(HumanoidArm humanoidArm, float var2) {
      int var3 = humanoidArm == HumanoidArm.RIGHT?1:-1;
      float var4 = Mth.sin(var2 * var2 * 3.1415927F);
      GlStateManager.rotatef((float)var3 * (45.0F + var4 * -20.0F), 0.0F, 1.0F, 0.0F);
      float var5 = Mth.sin(Mth.sqrt(var2) * 3.1415927F);
      GlStateManager.rotatef((float)var3 * var5 * -20.0F, 0.0F, 0.0F, 1.0F);
      GlStateManager.rotatef(var5 * -80.0F, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef((float)var3 * -45.0F, 0.0F, 1.0F, 0.0F);
   }

   private void applyItemArmTransform(HumanoidArm humanoidArm, float var2) {
      int var3 = humanoidArm == HumanoidArm.RIGHT?1:-1;
      GlStateManager.translatef((float)var3 * 0.56F, -0.52F + var2 * -0.6F, -0.72F);
   }

   public void render(float playerBob) {
      AbstractClientPlayer var2 = this.minecraft.player;
      float var3 = var2.getAttackAnim(playerBob);
      InteractionHand var4 = (InteractionHand)MoreObjects.firstNonNull(var2.swingingArm, InteractionHand.MAIN_HAND);
      float var5 = Mth.lerp(playerBob, var2.xRotO, var2.xRot);
      float var6 = Mth.lerp(playerBob, var2.yRotO, var2.yRot);
      boolean var7 = true;
      boolean var8 = true;
      if(var2.isUsingItem()) {
         ItemStack var9 = var2.getUseItem();
         if(var9.getItem() == Items.BOW || var9.getItem() == Items.CROSSBOW) {
            var7 = var2.getUsedItemHand() == InteractionHand.MAIN_HAND;
            var8 = !var7;
         }

         InteractionHand var10 = var2.getUsedItemHand();
         if(var10 == InteractionHand.MAIN_HAND) {
            ItemStack var11 = var2.getOffhandItem();
            if(var11.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(var11)) {
               var8 = false;
            }
         }
      } else {
         ItemStack var9 = var2.getMainHandItem();
         ItemStack var10 = var2.getOffhandItem();
         if(var9.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(var9)) {
            var8 = !var7;
         }

         if(var10.getItem() == Items.CROSSBOW && CrossbowItem.isCharged(var10)) {
            var7 = !var9.isEmpty();
            var8 = !var7;
         }
      }

      this.enableLight(var5, var6);
      this.setLightValue();
      this.setPlayerBob(playerBob);
      GlStateManager.enableRescaleNormal();
      if(var7) {
         float var9 = var4 == InteractionHand.MAIN_HAND?var3:0.0F;
         float var10 = 1.0F - Mth.lerp(playerBob, this.oMainHandHeight, this.mainHandHeight);
         this.renderArmWithItem(var2, playerBob, var5, InteractionHand.MAIN_HAND, var9, this.mainHandItem, var10);
      }

      if(var8) {
         float var9 = var4 == InteractionHand.OFF_HAND?var3:0.0F;
         float var10 = 1.0F - Mth.lerp(playerBob, this.oOffHandHeight, this.offHandHeight);
         this.renderArmWithItem(var2, playerBob, var5, InteractionHand.OFF_HAND, var9, this.offHandItem, var10);
      }

      GlStateManager.disableRescaleNormal();
      Lighting.turnOff();
   }

   public void renderArmWithItem(AbstractClientPlayer abstractClientPlayer, float var2, float var3, InteractionHand interactionHand, float var5, ItemStack itemStack, float var7) {
      boolean var8 = interactionHand == InteractionHand.MAIN_HAND;
      HumanoidArm var9 = var8?abstractClientPlayer.getMainArm():abstractClientPlayer.getMainArm().getOpposite();
      GlStateManager.pushMatrix();
      if(itemStack.isEmpty()) {
         if(var8 && !abstractClientPlayer.isInvisible()) {
            this.renderPlayerArm(var7, var5, var9);
         }
      } else if(itemStack.getItem() == Items.FILLED_MAP) {
         if(var8 && this.offHandItem.isEmpty()) {
            this.renderTwoHandedMap(var3, var7, var5);
         } else {
            this.renderOneHandedMap(var7, var9, var5, itemStack);
         }
      } else if(itemStack.getItem() == Items.CROSSBOW) {
         boolean var10 = CrossbowItem.isCharged(itemStack);
         boolean var11 = var9 == HumanoidArm.RIGHT;
         int var12 = var11?1:-1;
         if(abstractClientPlayer.isUsingItem() && abstractClientPlayer.getUseItemRemainingTicks() > 0 && abstractClientPlayer.getUsedItemHand() == interactionHand) {
            this.applyItemArmTransform(var9, var7);
            GlStateManager.translatef((float)var12 * -0.4785682F, -0.094387F, 0.05731531F);
            GlStateManager.rotatef(-11.935F, 1.0F, 0.0F, 0.0F);
            GlStateManager.rotatef((float)var12 * 65.3F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef((float)var12 * -9.785F, 0.0F, 0.0F, 1.0F);
            float var13 = (float)itemStack.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - var2 + 1.0F);
            float var14 = var13 / (float)CrossbowItem.getChargeDuration(itemStack);
            if(var14 > 1.0F) {
               var14 = 1.0F;
            }

            if(var14 > 0.1F) {
               float var15 = Mth.sin((var13 - 0.1F) * 1.3F);
               float var16 = var14 - 0.1F;
               float var17 = var15 * var16;
               GlStateManager.translatef(var17 * 0.0F, var17 * 0.004F, var17 * 0.0F);
            }

            GlStateManager.translatef(var14 * 0.0F, var14 * 0.0F, var14 * 0.04F);
            GlStateManager.scalef(1.0F, 1.0F, 1.0F + var14 * 0.2F);
            GlStateManager.rotatef((float)var12 * 45.0F, 0.0F, -1.0F, 0.0F);
         } else {
            float var13 = -0.4F * Mth.sin(Mth.sqrt(var5) * 3.1415927F);
            float var14 = 0.2F * Mth.sin(Mth.sqrt(var5) * 6.2831855F);
            float var15 = -0.2F * Mth.sin(var5 * 3.1415927F);
            GlStateManager.translatef((float)var12 * var13, var14, var15);
            this.applyItemArmTransform(var9, var7);
            this.applyItemArmAttackTransform(var9, var5);
            if(var10 && var5 < 0.001F) {
               GlStateManager.translatef((float)var12 * -0.641864F, 0.0F, 0.0F);
               GlStateManager.rotatef((float)var12 * 10.0F, 0.0F, 1.0F, 0.0F);
            }
         }

         this.renderItem(abstractClientPlayer, itemStack, var11?ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND:ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !var11);
      } else {
         boolean var10 = var9 == HumanoidArm.RIGHT;
         if(abstractClientPlayer.isUsingItem() && abstractClientPlayer.getUseItemRemainingTicks() > 0 && abstractClientPlayer.getUsedItemHand() == interactionHand) {
            int var11 = var10?1:-1;
            switch(itemStack.getUseAnimation()) {
            case NONE:
               this.applyItemArmTransform(var9, var7);
               break;
            case EAT:
            case DRINK:
               this.applyEatTransform(var2, var9, itemStack);
               this.applyItemArmTransform(var9, var7);
               break;
            case BLOCK:
               this.applyItemArmTransform(var9, var7);
               break;
            case BOW:
               this.applyItemArmTransform(var9, var7);
               GlStateManager.translatef((float)var11 * -0.2785682F, 0.18344387F, 0.15731531F);
               GlStateManager.rotatef(-13.935F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotatef((float)var11 * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef((float)var11 * -9.785F, 0.0F, 0.0F, 1.0F);
               float var12 = (float)itemStack.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - var2 + 1.0F);
               float var13 = var12 / 20.0F;
               var13 = (var13 * var13 + var13 * 2.0F) / 3.0F;
               if(var13 > 1.0F) {
                  var13 = 1.0F;
               }

               if(var13 > 0.1F) {
                  float var14 = Mth.sin((var12 - 0.1F) * 1.3F);
                  float var15 = var13 - 0.1F;
                  float var16 = var14 * var15;
                  GlStateManager.translatef(var16 * 0.0F, var16 * 0.004F, var16 * 0.0F);
               }

               GlStateManager.translatef(var13 * 0.0F, var13 * 0.0F, var13 * 0.04F);
               GlStateManager.scalef(1.0F, 1.0F, 1.0F + var13 * 0.2F);
               GlStateManager.rotatef((float)var11 * 45.0F, 0.0F, -1.0F, 0.0F);
               break;
            case SPEAR:
               this.applyItemArmTransform(var9, var7);
               GlStateManager.translatef((float)var11 * -0.5F, 0.7F, 0.1F);
               GlStateManager.rotatef(-55.0F, 1.0F, 0.0F, 0.0F);
               GlStateManager.rotatef((float)var11 * 35.3F, 0.0F, 1.0F, 0.0F);
               GlStateManager.rotatef((float)var11 * -9.785F, 0.0F, 0.0F, 1.0F);
               float var12 = (float)itemStack.getUseDuration() - ((float)this.minecraft.player.getUseItemRemainingTicks() - var2 + 1.0F);
               float var13 = var12 / 10.0F;
               if(var13 > 1.0F) {
                  var13 = 1.0F;
               }

               if(var13 > 0.1F) {
                  float var14 = Mth.sin((var12 - 0.1F) * 1.3F);
                  float var15 = var13 - 0.1F;
                  float var16 = var14 * var15;
                  GlStateManager.translatef(var16 * 0.0F, var16 * 0.004F, var16 * 0.0F);
               }

               GlStateManager.translatef(0.0F, 0.0F, var13 * 0.2F);
               GlStateManager.scalef(1.0F, 1.0F, 1.0F + var13 * 0.2F);
               GlStateManager.rotatef((float)var11 * 45.0F, 0.0F, -1.0F, 0.0F);
            }
         } else if(abstractClientPlayer.isAutoSpinAttack()) {
            this.applyItemArmTransform(var9, var7);
            int var11 = var10?1:-1;
            GlStateManager.translatef((float)var11 * -0.4F, 0.8F, 0.3F);
            GlStateManager.rotatef((float)var11 * 65.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotatef((float)var11 * -85.0F, 0.0F, 0.0F, 1.0F);
         } else {
            float var11 = -0.4F * Mth.sin(Mth.sqrt(var5) * 3.1415927F);
            float var12 = 0.2F * Mth.sin(Mth.sqrt(var5) * 6.2831855F);
            float var13 = -0.2F * Mth.sin(var5 * 3.1415927F);
            int var14 = var10?1:-1;
            GlStateManager.translatef((float)var14 * var11, var12, var13);
            this.applyItemArmTransform(var9, var7);
            this.applyItemArmAttackTransform(var9, var5);
         }

         this.renderItem(abstractClientPlayer, itemStack, var10?ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND:ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !var10);
      }

      GlStateManager.popMatrix();
   }

   public void renderScreenEffect(float f) {
      GlStateManager.disableAlphaTest();
      if(this.minecraft.player.isInWall()) {
         BlockState var2 = this.minecraft.level.getBlockState(new BlockPos(this.minecraft.player));
         Player var3 = this.minecraft.player;

         for(int var4 = 0; var4 < 8; ++var4) {
            double var5 = var3.x + (double)(((float)((var4 >> 0) % 2) - 0.5F) * var3.getBbWidth() * 0.8F);
            double var7 = var3.y + (double)(((float)((var4 >> 1) % 2) - 0.5F) * 0.1F);
            double var9 = var3.z + (double)(((float)((var4 >> 2) % 2) - 0.5F) * var3.getBbWidth() * 0.8F);
            BlockPos var11 = new BlockPos(var5, var7 + (double)var3.getEyeHeight(), var9);
            BlockState var12 = this.minecraft.level.getBlockState(var11);
            if(var12.isViewBlocking(this.minecraft.level, var11)) {
               var2 = var12;
            }
         }

         if(var2.getRenderShape() != RenderShape.INVISIBLE) {
            this.renderTex(this.minecraft.getBlockRenderer().getBlockModelShaper().getParticleIcon(var2));
         }
      }

      if(!this.minecraft.player.isSpectator()) {
         if(this.minecraft.player.isUnderLiquid(FluidTags.WATER)) {
            this.renderWater(f);
         }

         if(this.minecraft.player.isOnFire()) {
            this.renderFire();
         }
      }

      GlStateManager.enableAlphaTest();
   }

   private void renderTex(TextureAtlasSprite textureAtlasSprite) {
      this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      float var4 = 0.1F;
      GlStateManager.color4f(0.1F, 0.1F, 0.1F, 0.5F);
      GlStateManager.pushMatrix();
      float var5 = -1.0F;
      float var6 = 1.0F;
      float var7 = -1.0F;
      float var8 = 1.0F;
      float var9 = -0.5F;
      float var10 = textureAtlasSprite.getU0();
      float var11 = textureAtlasSprite.getU1();
      float var12 = textureAtlasSprite.getV0();
      float var13 = textureAtlasSprite.getV1();
      var3.begin(7, DefaultVertexFormat.POSITION_TEX);
      var3.vertex(-1.0D, -1.0D, -0.5D).uv((double)var11, (double)var13).endVertex();
      var3.vertex(1.0D, -1.0D, -0.5D).uv((double)var10, (double)var13).endVertex();
      var3.vertex(1.0D, 1.0D, -0.5D).uv((double)var10, (double)var12).endVertex();
      var3.vertex(-1.0D, 1.0D, -0.5D).uv((double)var11, (double)var12).endVertex();
      var2.end();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
   }

   private void renderWater(float f) {
      this.minecraft.getTextureManager().bind(UNDERWATER_LOCATION);
      Tesselator var2 = Tesselator.getInstance();
      BufferBuilder var3 = var2.getBuilder();
      float var4 = this.minecraft.player.getBrightness();
      GlStateManager.color4f(var4, var4, var4, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      GlStateManager.pushMatrix();
      float var5 = 4.0F;
      float var6 = -1.0F;
      float var7 = 1.0F;
      float var8 = -1.0F;
      float var9 = 1.0F;
      float var10 = -0.5F;
      float var11 = -this.minecraft.player.yRot / 64.0F;
      float var12 = this.minecraft.player.xRot / 64.0F;
      var3.begin(7, DefaultVertexFormat.POSITION_TEX);
      var3.vertex(-1.0D, -1.0D, -0.5D).uv((double)(4.0F + var11), (double)(4.0F + var12)).endVertex();
      var3.vertex(1.0D, -1.0D, -0.5D).uv((double)(0.0F + var11), (double)(4.0F + var12)).endVertex();
      var3.vertex(1.0D, 1.0D, -0.5D).uv((double)(0.0F + var11), (double)(0.0F + var12)).endVertex();
      var3.vertex(-1.0D, 1.0D, -0.5D).uv((double)(4.0F + var11), (double)(0.0F + var12)).endVertex();
      var2.end();
      GlStateManager.popMatrix();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
   }

   private void renderFire() {
      Tesselator var1 = Tesselator.getInstance();
      BufferBuilder var2 = var1.getBuilder();
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 0.9F);
      GlStateManager.depthFunc(519);
      GlStateManager.depthMask(false);
      GlStateManager.enableBlend();
      GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
      float var3 = 1.0F;

      for(int var4 = 0; var4 < 2; ++var4) {
         GlStateManager.pushMatrix();
         TextureAtlasSprite var5 = this.minecraft.getTextureAtlas().getSprite(ModelBakery.FIRE_1);
         this.minecraft.getTextureManager().bind(TextureAtlas.LOCATION_BLOCKS);
         float var6 = var5.getU0();
         float var7 = var5.getU1();
         float var8 = var5.getV0();
         float var9 = var5.getV1();
         float var10 = -0.5F;
         float var11 = 0.5F;
         float var12 = -0.5F;
         float var13 = 0.5F;
         float var14 = -0.5F;
         GlStateManager.translatef((float)(-(var4 * 2 - 1)) * 0.24F, -0.3F, 0.0F);
         GlStateManager.rotatef((float)(var4 * 2 - 1) * 10.0F, 0.0F, 1.0F, 0.0F);
         var2.begin(7, DefaultVertexFormat.POSITION_TEX);
         var2.vertex(-0.5D, -0.5D, -0.5D).uv((double)var7, (double)var9).endVertex();
         var2.vertex(0.5D, -0.5D, -0.5D).uv((double)var6, (double)var9).endVertex();
         var2.vertex(0.5D, 0.5D, -0.5D).uv((double)var6, (double)var8).endVertex();
         var2.vertex(-0.5D, 0.5D, -0.5D).uv((double)var7, (double)var8).endVertex();
         var1.end();
         GlStateManager.popMatrix();
      }

      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableBlend();
      GlStateManager.depthMask(true);
      GlStateManager.depthFunc(515);
   }

   public void tick() {
      this.oMainHandHeight = this.mainHandHeight;
      this.oOffHandHeight = this.offHandHeight;
      LocalPlayer var1 = this.minecraft.player;
      ItemStack var2 = var1.getMainHandItem();
      ItemStack var3 = var1.getOffhandItem();
      if(var1.isHandsBusy()) {
         this.mainHandHeight = Mth.clamp(this.mainHandHeight - 0.4F, 0.0F, 1.0F);
         this.offHandHeight = Mth.clamp(this.offHandHeight - 0.4F, 0.0F, 1.0F);
      } else {
         float var4 = var1.getAttackStrengthScale(1.0F);
         this.mainHandHeight += Mth.clamp((Objects.equals(this.mainHandItem, var2)?var4 * var4 * var4:0.0F) - this.mainHandHeight, -0.4F, 0.4F);
         this.offHandHeight += Mth.clamp((float)(Objects.equals(this.offHandItem, var3)?1:0) - this.offHandHeight, -0.4F, 0.4F);
      }

      if(this.mainHandHeight < 0.1F) {
         this.mainHandItem = var2;
      }

      if(this.offHandHeight < 0.1F) {
         this.offHandItem = var3;
      }

   }

   public void itemUsed(InteractionHand interactionHand) {
      if(interactionHand == InteractionHand.MAIN_HAND) {
         this.mainHandHeight = 0.0F;
      } else {
         this.offHandHeight = 0.0F;
      }

   }
}
