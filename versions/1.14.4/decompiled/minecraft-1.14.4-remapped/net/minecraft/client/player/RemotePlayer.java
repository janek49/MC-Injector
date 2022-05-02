package net.minecraft.client.player;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.MultiPlayerLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;

@ClientJarOnly
public class RemotePlayer extends AbstractClientPlayer {
   public RemotePlayer(MultiPlayerLevel multiPlayerLevel, GameProfile gameProfile) {
      super(multiPlayerLevel, gameProfile);
      this.maxUpStep = 1.0F;
      this.noPhysics = true;
   }

   public boolean shouldRenderAtSqrDistance(double d) {
      double var3 = this.getBoundingBox().getSize() * 10.0D;
      if(Double.isNaN(var3)) {
         var3 = 1.0D;
      }

      var3 = var3 * 64.0D * getViewScale();
      return d < var3 * var3;
   }

   public boolean hurt(DamageSource damageSource, float var2) {
      return true;
   }

   public void tick() {
      super.tick();
      this.animationSpeedOld = this.animationSpeed;
      double var1 = this.x - this.xo;
      double var3 = this.z - this.zo;
      float var5 = Mth.sqrt(var1 * var1 + var3 * var3) * 4.0F;
      if(var5 > 1.0F) {
         var5 = 1.0F;
      }

      this.animationSpeed += (var5 - this.animationSpeed) * 0.4F;
      this.animationPosition += this.animationSpeed;
   }

   public void aiStep() {
      if(this.lerpSteps > 0) {
         double var1 = this.x + (this.lerpX - this.x) / (double)this.lerpSteps;
         double var3 = this.y + (this.lerpY - this.y) / (double)this.lerpSteps;
         double var5 = this.z + (this.lerpZ - this.z) / (double)this.lerpSteps;
         this.yRot = (float)((double)this.yRot + Mth.wrapDegrees(this.lerpYRot - (double)this.yRot) / (double)this.lerpSteps);
         this.xRot = (float)((double)this.xRot + (this.lerpXRot - (double)this.xRot) / (double)this.lerpSteps);
         --this.lerpSteps;
         this.setPos(var1, var3, var5);
         this.setRot(this.yRot, this.xRot);
      }

      if(this.lerpHeadSteps > 0) {
         this.yHeadRot = (float)((double)this.yHeadRot + Mth.wrapDegrees(this.lyHeadRot - (double)this.yHeadRot) / (double)this.lerpHeadSteps);
         --this.lerpHeadSteps;
      }

      this.oBob = this.bob;
      this.updateSwingTime();
      float var1;
      if(this.onGround && this.getHealth() > 0.0F) {
         var1 = Math.min(0.1F, Mth.sqrt(getHorizontalDistanceSqr(this.getDeltaMovement())));
      } else {
         var1 = 0.0F;
      }

      if(!this.onGround && this.getHealth() > 0.0F) {
         float var2 = (float)Math.atan(-this.getDeltaMovement().y * 0.20000000298023224D) * 15.0F;
      } else {
         float var2 = 0.0F;
      }

      this.bob += (var1 - this.bob) * 0.4F;
      this.level.getProfiler().push("push");
      this.pushEntities();
      this.level.getProfiler().pop();
   }

   protected void updatePlayerPose() {
   }

   public void sendMessage(Component component) {
      Minecraft.getInstance().gui.getChat().addMessage(component);
   }
}
