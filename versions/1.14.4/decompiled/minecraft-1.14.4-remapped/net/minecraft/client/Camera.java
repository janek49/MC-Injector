package net.minecraft.client;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

@ClientJarOnly
public class Camera {
   private boolean initialized;
   private BlockGetter level;
   private Entity entity;
   private Vec3 position = Vec3.ZERO;
   private final BlockPos.MutableBlockPos blockPosition = new BlockPos.MutableBlockPos();
   private Vec3 forwards;
   private Vec3 up;
   private Vec3 left;
   private float xRot;
   private float yRot;
   private boolean detached;
   private boolean mirror;
   private float eyeHeight;
   private float eyeHeightOld;

   public void setup(BlockGetter level, Entity entity, boolean detached, boolean mirror, float var5) {
      this.initialized = true;
      this.level = level;
      this.entity = entity;
      this.detached = detached;
      this.mirror = mirror;
      this.setRotation(entity.getViewYRot(var5), entity.getViewXRot(var5));
      this.setPosition(Mth.lerp((double)var5, entity.xo, entity.x), Mth.lerp((double)var5, entity.yo, entity.y) + (double)Mth.lerp(var5, this.eyeHeightOld, this.eyeHeight), Mth.lerp((double)var5, entity.zo, entity.z));
      if(detached) {
         if(mirror) {
            this.yRot += 180.0F;
            this.xRot += -this.xRot * 2.0F;
            this.recalculateViewVector();
         }

         this.move(-this.getMaxZoom(4.0D), 0.0D, 0.0D);
      } else if(entity instanceof LivingEntity && ((LivingEntity)entity).isSleeping()) {
         Direction var6 = ((LivingEntity)entity).getBedOrientation();
         this.setRotation(var6 != null?var6.toYRot() - 180.0F:0.0F, 0.0F);
         this.move(0.0D, 0.3D, 0.0D);
      }

      GlStateManager.rotatef(this.xRot, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotatef(this.yRot + 180.0F, 0.0F, 1.0F, 0.0F);
   }

   public void tick() {
      if(this.entity != null) {
         this.eyeHeightOld = this.eyeHeight;
         this.eyeHeight += (this.entity.getEyeHeight() - this.eyeHeight) * 0.5F;
      }

   }

   private double getMaxZoom(double d) {
      for(int var3 = 0; var3 < 8; ++var3) {
         float var4 = (float)((var3 & 1) * 2 - 1);
         float var5 = (float)((var3 >> 1 & 1) * 2 - 1);
         float var6 = (float)((var3 >> 2 & 1) * 2 - 1);
         var4 = var4 * 0.1F;
         var5 = var5 * 0.1F;
         var6 = var6 * 0.1F;
         Vec3 var7 = this.position.add((double)var4, (double)var5, (double)var6);
         Vec3 var8 = new Vec3(this.position.x - this.forwards.x * d + (double)var4 + (double)var6, this.position.y - this.forwards.y * d + (double)var5, this.position.z - this.forwards.z * d + (double)var6);
         HitResult var9 = this.level.clip(new ClipContext(var7, var8, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, this.entity));
         if(var9.getType() != HitResult.Type.MISS) {
            double var10 = var9.getLocation().distanceTo(this.position);
            if(var10 < d) {
               d = var10;
            }
         }
      }

      return d;
   }

   protected void move(double var1, double var3, double var5) {
      double var7 = this.forwards.x * var1 + this.up.x * var3 + this.left.x * var5;
      double var9 = this.forwards.y * var1 + this.up.y * var3 + this.left.y * var5;
      double var11 = this.forwards.z * var1 + this.up.z * var3 + this.left.z * var5;
      this.setPosition(new Vec3(this.position.x + var7, this.position.y + var9, this.position.z + var11));
   }

   protected void recalculateViewVector() {
      float var1 = Mth.cos((this.yRot + 90.0F) * 0.017453292F);
      float var2 = Mth.sin((this.yRot + 90.0F) * 0.017453292F);
      float var3 = Mth.cos(-this.xRot * 0.017453292F);
      float var4 = Mth.sin(-this.xRot * 0.017453292F);
      float var5 = Mth.cos((-this.xRot + 90.0F) * 0.017453292F);
      float var6 = Mth.sin((-this.xRot + 90.0F) * 0.017453292F);
      this.forwards = new Vec3((double)(var1 * var3), (double)var4, (double)(var2 * var3));
      this.up = new Vec3((double)(var1 * var5), (double)var6, (double)(var2 * var5));
      this.left = this.forwards.cross(this.up).scale(-1.0D);
   }

   protected void setRotation(float yRot, float xRot) {
      this.xRot = xRot;
      this.yRot = yRot;
      this.recalculateViewVector();
   }

   protected void setPosition(double var1, double var3, double var5) {
      this.setPosition(new Vec3(var1, var3, var5));
   }

   protected void setPosition(Vec3 position) {
      this.position = position;
      this.blockPosition.set(position.x, position.y, position.z);
   }

   public Vec3 getPosition() {
      return this.position;
   }

   public BlockPos getBlockPosition() {
      return this.blockPosition;
   }

   public float getXRot() {
      return this.xRot;
   }

   public float getYRot() {
      return this.yRot;
   }

   public Entity getEntity() {
      return this.entity;
   }

   public boolean isInitialized() {
      return this.initialized;
   }

   public boolean isDetached() {
      return this.detached;
   }

   public FluidState getFluidInCamera() {
      if(!this.initialized) {
         return Fluids.EMPTY.defaultFluidState();
      } else {
         FluidState fluidState = this.level.getFluidState(this.blockPosition);
         return !fluidState.isEmpty() && this.position.y >= (double)((float)this.blockPosition.getY() + fluidState.getHeight(this.level, this.blockPosition))?Fluids.EMPTY.defaultFluidState():fluidState;
      }
   }

   public final Vec3 getLookVector() {
      return this.forwards;
   }

   public final Vec3 getUpVector() {
      return this.up;
   }

   public void reset() {
      this.level = null;
      this.entity = null;
      this.initialized = false;
   }
}
