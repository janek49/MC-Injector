package net.minecraft.client.renderer.entity;

import com.fox2code.repacker.ClientJarOnly;
import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerModelPart;
import net.minecraft.world.scores.Team;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@ClientJarOnly
public abstract class LivingEntityRenderer extends EntityRenderer implements RenderLayerParent {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DynamicTexture WHITE_TEXTURE = (DynamicTexture)Util.make(new DynamicTexture(16, 16, false), (dynamicTexture) -> {
      dynamicTexture.getPixels().untrack();

      for(int var1 = 0; var1 < 16; ++var1) {
         for(int var2 = 0; var2 < 16; ++var2) {
            dynamicTexture.getPixels().setPixelRGBA(var2, var1, -1);
         }
      }

      dynamicTexture.upload();
   });
   protected EntityModel model;
   protected final FloatBuffer tintBuffer = MemoryTracker.createFloatBuffer(4);
   protected final List layers = Lists.newArrayList();
   protected boolean onlySolidLayers;

   public LivingEntityRenderer(EntityRenderDispatcher entityRenderDispatcher, EntityModel model, float shadowRadius) {
      super(entityRenderDispatcher);
      this.model = model;
      this.shadowRadius = shadowRadius;
   }

   protected final boolean addLayer(RenderLayer renderLayer) {
      return this.layers.add(renderLayer);
   }

   public EntityModel getModel() {
      return this.model;
   }

   public void render(LivingEntity upSolidState, double var2, double var4, double var6, float var8, float var9) {
      GlStateManager.pushMatrix();
      GlStateManager.disableCull();
      this.model.attackTime = this.getAttackAnim(upSolidState, var9);
      this.model.riding = upSolidState.isPassenger();
      this.model.young = upSolidState.isBaby();

      try {
         float var10 = Mth.rotLerp(var9, upSolidState.yBodyRotO, upSolidState.yBodyRot);
         float var11 = Mth.rotLerp(var9, upSolidState.yHeadRotO, upSolidState.yHeadRot);
         float var12 = var11 - var10;
         if(upSolidState.isPassenger() && upSolidState.getVehicle() instanceof LivingEntity) {
            LivingEntity var13 = (LivingEntity)upSolidState.getVehicle();
            var10 = Mth.rotLerp(var9, var13.yBodyRotO, var13.yBodyRot);
            var12 = var11 - var10;
            float var14 = Mth.wrapDegrees(var12);
            if(var14 < -85.0F) {
               var14 = -85.0F;
            }

            if(var14 >= 85.0F) {
               var14 = 85.0F;
            }

            var10 = var11 - var14;
            if(var14 * var14 > 2500.0F) {
               var10 += var14 * 0.2F;
            }

            var12 = var11 - var10;
         }

         float var13 = Mth.lerp(var9, upSolidState.xRotO, upSolidState.xRot);
         this.setupPosition(upSolidState, var2, var4, var6);
         float var14 = this.getBob(upSolidState, var9);
         this.setupRotations(upSolidState, var14, var10, var9);
         float var15 = this.setupScale(upSolidState, var9);
         float var16 = 0.0F;
         float var17 = 0.0F;
         if(!upSolidState.isPassenger() && upSolidState.isAlive()) {
            var16 = Mth.lerp(var9, upSolidState.animationSpeedOld, upSolidState.animationSpeed);
            var17 = upSolidState.animationPosition - upSolidState.animationSpeed * (1.0F - var9);
            if(upSolidState.isBaby()) {
               var17 *= 3.0F;
            }

            if(var16 > 1.0F) {
               var16 = 1.0F;
            }
         }

         GlStateManager.enableAlphaTest();
         this.model.prepareMobModel(upSolidState, var17, var16, var9);
         this.model.setupAnim(upSolidState, var17, var16, var14, var12, var13, var15);
         if(this.solidRender) {
            boolean var18 = this.setupSolidState(upSolidState);
            GlStateManager.enableColorMaterial();
            GlStateManager.setupSolidRenderingTextureCombine(this.getTeamColor(upSolidState));
            if(!this.onlySolidLayers) {
               this.renderModel(upSolidState, var17, var16, var14, var12, var13, var15);
            }

            if(!upSolidState.isSpectator()) {
               this.renderLayers(upSolidState, var17, var16, var9, var14, var12, var13, var15);
            }

            GlStateManager.tearDownSolidRenderingTextureCombine();
            GlStateManager.disableColorMaterial();
            if(var18) {
               this.tearDownSolidState();
            }
         } else {
            boolean var18 = this.setupOverlayColor(upSolidState, var9);
            this.renderModel(upSolidState, var17, var16, var14, var12, var13, var15);
            if(var18) {
               this.teardownOverlayColor();
            }

            GlStateManager.depthMask(true);
            if(!upSolidState.isSpectator()) {
               this.renderLayers(upSolidState, var17, var16, var9, var14, var12, var13, var15);
            }
         }

         GlStateManager.disableRescaleNormal();
      } catch (Exception var19) {
         LOGGER.error("Couldn\'t render entity", var19);
      }

      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableCull();
      GlStateManager.popMatrix();
      super.render(upSolidState, var2, var4, var6, var8, var9);
   }

   public float setupScale(LivingEntity livingEntity, float var2) {
      GlStateManager.enableRescaleNormal();
      GlStateManager.scalef(-1.0F, -1.0F, 1.0F);
      this.scale(livingEntity, var2);
      float var3 = 0.0625F;
      GlStateManager.translatef(0.0F, -1.501F, 0.0F);
      return 0.0625F;
   }

   protected boolean setupSolidState(LivingEntity livingEntity) {
      GlStateManager.disableLighting();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.disableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      return true;
   }

   protected void tearDownSolidState() {
      GlStateManager.enableLighting();
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.enableTexture();
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   protected void renderModel(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7) {
      boolean var8 = this.isVisible(livingEntity);
      boolean var9 = !var8 && !livingEntity.isInvisibleTo(Minecraft.getInstance().player);
      if(var8 || var9) {
         if(!this.bindTexture(livingEntity)) {
            return;
         }

         if(var9) {
            GlStateManager.setProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }

         this.model.render(livingEntity, var2, var3, var4, var5, var6, var7);
         if(var9) {
            GlStateManager.unsetProfile(GlStateManager.Profile.TRANSPARENT_MODEL);
         }
      }

   }

   protected boolean isVisible(LivingEntity livingEntity) {
      return !livingEntity.isInvisible() || this.solidRender;
   }

   protected boolean setupOverlayColor(LivingEntity livingEntity, float var2) {
      return this.setupOverlayColor(livingEntity, var2, true);
   }

   protected boolean setupOverlayColor(LivingEntity livingEntity, float var2, boolean var3) {
      float var4 = livingEntity.getBrightness();
      int var5 = this.getOverlayColor(livingEntity, var4, var2);
      boolean var6 = (var5 >> 24 & 255) > 0;
      boolean var7 = livingEntity.hurtTime > 0 || livingEntity.deathTime > 0;
      if(!var6 && !var7) {
         return false;
      } else if(!var6 && !var3) {
         return false;
      } else {
         GlStateManager.activeTexture(GLX.GL_TEXTURE0);
         GlStateManager.enableTexture();
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(GLX.GL_TEXTURE1);
         GlStateManager.enableTexture();
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, GLX.GL_INTERPOLATE);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_CONSTANT);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE2_RGB, GLX.GL_CONSTANT);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND2_RGB, 770);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         this.tintBuffer.position(0);
         if(var7) {
            this.tintBuffer.put(1.0F);
            this.tintBuffer.put(0.0F);
            this.tintBuffer.put(0.0F);
            this.tintBuffer.put(0.3F);
         } else {
            float var8 = (float)(var5 >> 24 & 255) / 255.0F;
            float var9 = (float)(var5 >> 16 & 255) / 255.0F;
            float var10 = (float)(var5 >> 8 & 255) / 255.0F;
            float var11 = (float)(var5 & 255) / 255.0F;
            this.tintBuffer.put(var9);
            this.tintBuffer.put(var10);
            this.tintBuffer.put(var11);
            this.tintBuffer.put(1.0F - var8);
         }

         this.tintBuffer.flip();
         GlStateManager.texEnv(8960, 8705, this.tintBuffer);
         GlStateManager.activeTexture(GLX.GL_TEXTURE2);
         GlStateManager.enableTexture();
         GlStateManager.bindTexture(WHITE_TEXTURE.getId());
         GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_TEXTURE1);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
         GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 7681);
         GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_PREVIOUS);
         GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
         GlStateManager.activeTexture(GLX.GL_TEXTURE0);
         return true;
      }
   }

   protected void teardownOverlayColor() {
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
      GlStateManager.enableTexture();
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, GLX.GL_TEXTURE0);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PRIMARY_COLOR);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, GLX.GL_TEXTURE0);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_ALPHA, GLX.GL_PRIMARY_COLOR);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_ALPHA, 770);
      GlStateManager.activeTexture(GLX.GL_TEXTURE1);
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.activeTexture(GLX.GL_TEXTURE2);
      GlStateManager.disableTexture();
      GlStateManager.bindTexture(0);
      GlStateManager.texEnv(8960, 8704, GLX.GL_COMBINE);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_RGB, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND1_RGB, 768);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_RGB, 5890);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE1_RGB, GLX.GL_PREVIOUS);
      GlStateManager.texEnv(8960, GLX.GL_COMBINE_ALPHA, 8448);
      GlStateManager.texEnv(8960, GLX.GL_OPERAND0_ALPHA, 770);
      GlStateManager.texEnv(8960, GLX.GL_SOURCE0_ALPHA, 5890);
      GlStateManager.activeTexture(GLX.GL_TEXTURE0);
   }

   protected void setupPosition(LivingEntity livingEntity, double var2, double var4, double var6) {
      if(livingEntity.getPose() == Pose.SLEEPING) {
         Direction var8 = livingEntity.getBedOrientation();
         if(var8 != null) {
            float var9 = livingEntity.getEyeHeight(Pose.STANDING) - 0.1F;
            GlStateManager.translatef((float)var2 - (float)var8.getStepX() * var9, (float)var4, (float)var6 - (float)var8.getStepZ() * var9);
            return;
         }
      }

      GlStateManager.translatef((float)var2, (float)var4, (float)var6);
   }

   private static float sleepDirectionToRotation(Direction direction) {
      switch(direction) {
      case SOUTH:
         return 90.0F;
      case WEST:
         return 0.0F;
      case NORTH:
         return 270.0F;
      case EAST:
         return 180.0F;
      default:
         return 0.0F;
      }
   }

   protected void setupRotations(LivingEntity livingEntity, float var2, float var3, float var4) {
      Pose var5 = livingEntity.getPose();
      if(var5 != Pose.SLEEPING) {
         GlStateManager.rotatef(180.0F - var3, 0.0F, 1.0F, 0.0F);
      }

      if(livingEntity.deathTime > 0) {
         float var6 = ((float)livingEntity.deathTime + var4 - 1.0F) / 20.0F * 1.6F;
         var6 = Mth.sqrt(var6);
         if(var6 > 1.0F) {
            var6 = 1.0F;
         }

         GlStateManager.rotatef(var6 * this.getFlipDegrees(livingEntity), 0.0F, 0.0F, 1.0F);
      } else if(livingEntity.isAutoSpinAttack()) {
         GlStateManager.rotatef(-90.0F - livingEntity.xRot, 1.0F, 0.0F, 0.0F);
         GlStateManager.rotatef(((float)livingEntity.tickCount + var4) * -75.0F, 0.0F, 1.0F, 0.0F);
      } else if(var5 == Pose.SLEEPING) {
         Direction var6 = livingEntity.getBedOrientation();
         GlStateManager.rotatef(var6 != null?sleepDirectionToRotation(var6):var3, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotatef(this.getFlipDegrees(livingEntity), 0.0F, 0.0F, 1.0F);
         GlStateManager.rotatef(270.0F, 0.0F, 1.0F, 0.0F);
      } else if(livingEntity.hasCustomName() || livingEntity instanceof Player) {
         String var6 = ChatFormatting.stripFormatting(livingEntity.getName().getString());
         if(var6 != null && ("Dinnerbone".equals(var6) || "Grumm".equals(var6)) && (!(livingEntity instanceof Player) || ((Player)livingEntity).isModelPartShown(PlayerModelPart.CAPE))) {
            GlStateManager.translatef(0.0F, livingEntity.getBbHeight() + 0.1F, 0.0F);
            GlStateManager.rotatef(180.0F, 0.0F, 0.0F, 1.0F);
         }
      }

   }

   protected float getAttackAnim(LivingEntity livingEntity, float var2) {
      return livingEntity.getAttackAnim(var2);
   }

   protected float getBob(LivingEntity livingEntity, float var2) {
      return (float)livingEntity.tickCount + var2;
   }

   protected void renderLayers(LivingEntity livingEntity, float var2, float var3, float var4, float var5, float var6, float var7, float var8) {
      for(RenderLayer<T, M> var10 : this.layers) {
         boolean var11 = this.setupOverlayColor(livingEntity, var4, var10.colorsOnDamage());
         var10.render(livingEntity, var2, var3, var4, var5, var6, var7, var8);
         if(var11) {
            this.teardownOverlayColor();
         }
      }

   }

   protected float getFlipDegrees(LivingEntity livingEntity) {
      return 90.0F;
   }

   protected int getOverlayColor(LivingEntity livingEntity, float var2, float var3) {
      return 0;
   }

   protected void scale(LivingEntity livingEntity, float var2) {
   }

   public void renderName(LivingEntity livingEntity, double var2, double var4, double var6) {
      if(this.shouldShowName(livingEntity)) {
         double var8 = livingEntity.distanceToSqr(this.entityRenderDispatcher.camera.getPosition());
         float var10 = livingEntity.isVisuallySneaking()?32.0F:64.0F;
         if(var8 < (double)(var10 * var10)) {
            String var11 = livingEntity.getDisplayName().getColoredString();
            GlStateManager.alphaFunc(516, 0.1F);
            this.renderNameTags(livingEntity, var2, var4, var6, var11, var8);
         }
      }
   }

   protected boolean shouldShowName(LivingEntity livingEntity) {
      LocalPlayer var2 = Minecraft.getInstance().player;
      boolean var3 = !livingEntity.isInvisibleTo(var2);
      if(livingEntity != var2) {
         Team var4 = livingEntity.getTeam();
         Team var5 = var2.getTeam();
         if(var4 != null) {
            Team.Visibility var6 = var4.getNameTagVisibility();
            switch(var6) {
            case ALWAYS:
               return var3;
            case NEVER:
               return false;
            case HIDE_FOR_OTHER_TEAMS:
               return var5 == null?var3:var4.isAlliedTo(var5) && (var4.canSeeFriendlyInvisibles() || var3);
            case HIDE_FOR_OWN_TEAM:
               return var5 == null?var3:!var4.isAlliedTo(var5) && var3;
            default:
               return true;
            }
         }
      }

      return Minecraft.renderNames() && livingEntity != this.entityRenderDispatcher.camera.getEntity() && var3 && !livingEntity.isVehicle();
   }

   // $FF: synthetic method
   protected boolean shouldShowName(Entity var1) {
      return this.shouldShowName((LivingEntity)var1);
   }

   // $FF: synthetic method
   public void renderName(Entity var1, double var2, double var4, double var6) {
      this.renderName((LivingEntity)var1, var2, var4, var6);
   }
}
