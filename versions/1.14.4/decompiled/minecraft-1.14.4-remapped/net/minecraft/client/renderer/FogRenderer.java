package net.minecraft.client.renderer;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GLX;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.platform.MemoryTracker;
import java.nio.FloatBuffer;
import net.minecraft.Util;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class FogRenderer {
   private final FloatBuffer blackBuffer = MemoryTracker.createFloatBuffer(16);
   private final FloatBuffer colorBuffer = MemoryTracker.createFloatBuffer(16);
   private float fogRed;
   private float fogGreen;
   private float fogBlue;
   private float oldRed = -1.0F;
   private float oldGreen = -1.0F;
   private float oldBlue = -1.0F;
   private int targetBiomeFog = -1;
   private int previousBiomeFog = -1;
   private long biomeChangedTime = -1L;
   private final GameRenderer renderer;
   private final Minecraft minecraft;

   public FogRenderer(GameRenderer renderer) {
      this.renderer = renderer;
      this.minecraft = renderer.getMinecraft();
      this.blackBuffer.put(0.0F).put(0.0F).put(0.0F).put(1.0F).flip();
   }

   public void setupClearColor(Camera camera, float var2) {
      Level var3 = this.minecraft.level;
      FluidState var4 = camera.getFluidInCamera();
      if(var4.is(FluidTags.WATER)) {
         this.setWaterFogColor(camera, var3);
      } else if(var4.is(FluidTags.LAVA)) {
         this.fogRed = 0.6F;
         this.fogGreen = 0.1F;
         this.fogBlue = 0.0F;
         this.biomeChangedTime = -1L;
      } else {
         this.setLandFogColor(camera, var3, var2);
         this.biomeChangedTime = -1L;
      }

      double var5 = camera.getPosition().y * var3.dimension.getClearColorScale();
      if(camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
         int var7 = ((LivingEntity)camera.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
         if(var7 < 20) {
            var5 *= (double)(1.0F - (float)var7 / 20.0F);
         } else {
            var5 = 0.0D;
         }
      }

      if(var5 < 1.0D) {
         if(var5 < 0.0D) {
            var5 = 0.0D;
         }

         var5 = var5 * var5;
         this.fogRed = (float)((double)this.fogRed * var5);
         this.fogGreen = (float)((double)this.fogGreen * var5);
         this.fogBlue = (float)((double)this.fogBlue * var5);
      }

      if(this.renderer.getDarkenWorldAmount(var2) > 0.0F) {
         float var7 = this.renderer.getDarkenWorldAmount(var2);
         this.fogRed = this.fogRed * (1.0F - var7) + this.fogRed * 0.7F * var7;
         this.fogGreen = this.fogGreen * (1.0F - var7) + this.fogGreen * 0.6F * var7;
         this.fogBlue = this.fogBlue * (1.0F - var7) + this.fogBlue * 0.6F * var7;
      }

      if(var4.is(FluidTags.WATER)) {
         float var7 = 0.0F;
         if(camera.getEntity() instanceof LocalPlayer) {
            LocalPlayer var8 = (LocalPlayer)camera.getEntity();
            var7 = var8.getWaterVision();
         }

         float var8 = 1.0F / this.fogRed;
         if(var8 > 1.0F / this.fogGreen) {
            var8 = 1.0F / this.fogGreen;
         }

         if(var8 > 1.0F / this.fogBlue) {
            var8 = 1.0F / this.fogBlue;
         }

         this.fogRed = this.fogRed * (1.0F - var7) + this.fogRed * var8 * var7;
         this.fogGreen = this.fogGreen * (1.0F - var7) + this.fogGreen * var8 * var7;
         this.fogBlue = this.fogBlue * (1.0F - var7) + this.fogBlue * var8 * var7;
      } else if(camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).hasEffect(MobEffects.NIGHT_VISION)) {
         float var7 = this.renderer.getNightVisionScale((LivingEntity)camera.getEntity(), var2);
         float var8 = 1.0F / this.fogRed;
         if(var8 > 1.0F / this.fogGreen) {
            var8 = 1.0F / this.fogGreen;
         }

         if(var8 > 1.0F / this.fogBlue) {
            var8 = 1.0F / this.fogBlue;
         }

         this.fogRed = this.fogRed * (1.0F - var7) + this.fogRed * var8 * var7;
         this.fogGreen = this.fogGreen * (1.0F - var7) + this.fogGreen * var8 * var7;
         this.fogBlue = this.fogBlue * (1.0F - var7) + this.fogBlue * var8 * var7;
      }

      GlStateManager.clearColor(this.fogRed, this.fogGreen, this.fogBlue, 0.0F);
   }

   private void setLandFogColor(Camera camera, Level level, float var3) {
      float var4 = 0.25F + 0.75F * (float)this.minecraft.options.renderDistance / 32.0F;
      var4 = 1.0F - (float)Math.pow((double)var4, 0.25D);
      Vec3 var5 = level.getSkyColor(camera.getBlockPosition(), var3);
      float var6 = (float)var5.x;
      float var7 = (float)var5.y;
      float var8 = (float)var5.z;
      Vec3 var9 = level.getFogColor(var3);
      this.fogRed = (float)var9.x;
      this.fogGreen = (float)var9.y;
      this.fogBlue = (float)var9.z;
      if(this.minecraft.options.renderDistance >= 4) {
         double var10 = Mth.sin(level.getSunAngle(var3)) > 0.0F?-1.0D:1.0D;
         Vec3 var12 = new Vec3(var10, 0.0D, 0.0D);
         float var13 = (float)camera.getLookVector().dot(var12);
         if(var13 < 0.0F) {
            var13 = 0.0F;
         }

         if(var13 > 0.0F) {
            float[] vars14 = level.dimension.getSunriseColor(level.getTimeOfDay(var3), var3);
            if(vars14 != null) {
               var13 = var13 * vars14[3];
               this.fogRed = this.fogRed * (1.0F - var13) + vars14[0] * var13;
               this.fogGreen = this.fogGreen * (1.0F - var13) + vars14[1] * var13;
               this.fogBlue = this.fogBlue * (1.0F - var13) + vars14[2] * var13;
            }
         }
      }

      this.fogRed += (var6 - this.fogRed) * var4;
      this.fogGreen += (var7 - this.fogGreen) * var4;
      this.fogBlue += (var8 - this.fogBlue) * var4;
      float var10 = level.getRainLevel(var3);
      if(var10 > 0.0F) {
         float var11 = 1.0F - var10 * 0.5F;
         float var12 = 1.0F - var10 * 0.4F;
         this.fogRed *= var11;
         this.fogGreen *= var11;
         this.fogBlue *= var12;
      }

      float var11 = level.getThunderLevel(var3);
      if(var11 > 0.0F) {
         float var12 = 1.0F - var11 * 0.5F;
         this.fogRed *= var12;
         this.fogGreen *= var12;
         this.fogBlue *= var12;
      }

   }

   private void setWaterFogColor(Camera camera, LevelReader levelReader) {
      long var3 = Util.getMillis();
      int var5 = levelReader.getBiome(new BlockPos(camera.getPosition())).getWaterFogColor();
      if(this.biomeChangedTime < 0L) {
         this.targetBiomeFog = var5;
         this.previousBiomeFog = var5;
         this.biomeChangedTime = var3;
      }

      int var6 = this.targetBiomeFog >> 16 & 255;
      int var7 = this.targetBiomeFog >> 8 & 255;
      int var8 = this.targetBiomeFog & 255;
      int var9 = this.previousBiomeFog >> 16 & 255;
      int var10 = this.previousBiomeFog >> 8 & 255;
      int var11 = this.previousBiomeFog & 255;
      float var12 = Mth.clamp((float)(var3 - this.biomeChangedTime) / 5000.0F, 0.0F, 1.0F);
      float var13 = Mth.lerp(var12, (float)var9, (float)var6);
      float var14 = Mth.lerp(var12, (float)var10, (float)var7);
      float var15 = Mth.lerp(var12, (float)var11, (float)var8);
      this.fogRed = var13 / 255.0F;
      this.fogGreen = var14 / 255.0F;
      this.fogBlue = var15 / 255.0F;
      if(this.targetBiomeFog != var5) {
         this.targetBiomeFog = var5;
         this.previousBiomeFog = Mth.floor(var13) << 16 | Mth.floor(var14) << 8 | Mth.floor(var15);
         this.biomeChangedTime = var3;
      }

   }

   public void setupFog(Camera camera, int var2) {
      this.resetFogColor(false);
      GlStateManager.normal3f(0.0F, -1.0F, 0.0F);
      GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      FluidState var3 = camera.getFluidInCamera();
      if(camera.getEntity() instanceof LivingEntity && ((LivingEntity)camera.getEntity()).hasEffect(MobEffects.BLINDNESS)) {
         float var4 = 5.0F;
         int var5 = ((LivingEntity)camera.getEntity()).getEffect(MobEffects.BLINDNESS).getDuration();
         if(var5 < 20) {
            var4 = Mth.lerp(1.0F - (float)var5 / 20.0F, 5.0F, this.renderer.getRenderDistance());
         }

         GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
         if(var2 == -1) {
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(var4 * 0.8F);
         } else {
            GlStateManager.fogStart(var4 * 0.25F);
            GlStateManager.fogEnd(var4);
         }

         GLX.setupNvFogDistance();
      } else if(var3.is(FluidTags.WATER)) {
         GlStateManager.fogMode(GlStateManager.FogMode.EXP2);
         if(camera.getEntity() instanceof LivingEntity) {
            if(camera.getEntity() instanceof LocalPlayer) {
               LocalPlayer var4 = (LocalPlayer)camera.getEntity();
               float var5 = 0.05F - var4.getWaterVision() * var4.getWaterVision() * 0.03F;
               Biome var6 = var4.level.getBiome(new BlockPos(var4));
               if(var6 == Biomes.SWAMP || var6 == Biomes.SWAMP_HILLS) {
                  var5 += 0.005F;
               }

               GlStateManager.fogDensity(var5);
            } else {
               GlStateManager.fogDensity(0.05F);
            }
         } else {
            GlStateManager.fogDensity(0.1F);
         }
      } else if(var3.is(FluidTags.LAVA)) {
         GlStateManager.fogMode(GlStateManager.FogMode.EXP);
         GlStateManager.fogDensity(2.0F);
      } else {
         float var4 = this.renderer.getRenderDistance();
         GlStateManager.fogMode(GlStateManager.FogMode.LINEAR);
         if(var2 == -1) {
            GlStateManager.fogStart(0.0F);
            GlStateManager.fogEnd(var4);
         } else {
            GlStateManager.fogStart(var4 * 0.75F);
            GlStateManager.fogEnd(var4);
         }

         GLX.setupNvFogDistance();
         if(this.minecraft.level.dimension.isFoggyAt(Mth.floor(camera.getPosition().x), Mth.floor(camera.getPosition().z)) || this.minecraft.gui.getBossOverlay().shouldCreateWorldFog()) {
            GlStateManager.fogStart(var4 * 0.05F);
            GlStateManager.fogEnd(Math.min(var4, 192.0F) * 0.5F);
         }
      }

      GlStateManager.enableColorMaterial();
      GlStateManager.enableFog();
      GlStateManager.colorMaterial(1028, 4608);
   }

   public void resetFogColor(boolean b) {
      if(b) {
         GlStateManager.fog(2918, this.blackBuffer);
      } else {
         GlStateManager.fog(2918, this.updateColorBuffer());
      }

   }

   private FloatBuffer updateColorBuffer() {
      if(this.oldRed != this.fogRed || this.oldGreen != this.fogGreen || this.oldBlue != this.fogBlue) {
         this.colorBuffer.clear();
         this.colorBuffer.put(this.fogRed).put(this.fogGreen).put(this.fogBlue).put(1.0F);
         this.colorBuffer.flip();
         this.oldRed = this.fogRed;
         this.oldGreen = this.fogGreen;
         this.oldBlue = this.fogBlue;
      }

      return this.colorBuffer;
   }
}
