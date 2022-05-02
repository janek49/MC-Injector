package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.vehicle.Boat;

@ClientJarOnly
public class BoatModel extends EntityModel {
   private final ModelPart[] cubes = new ModelPart[5];
   private final ModelPart[] paddles = new ModelPart[2];
   private final ModelPart waterPatch;

   public BoatModel() {
      this.cubes[0] = (new ModelPart(this, 0, 0)).setTexSize(128, 64);
      this.cubes[1] = (new ModelPart(this, 0, 19)).setTexSize(128, 64);
      this.cubes[2] = (new ModelPart(this, 0, 27)).setTexSize(128, 64);
      this.cubes[3] = (new ModelPart(this, 0, 35)).setTexSize(128, 64);
      this.cubes[4] = (new ModelPart(this, 0, 43)).setTexSize(128, 64);
      int var1 = 32;
      int var2 = 6;
      int var3 = 20;
      int var4 = 4;
      int var5 = 28;
      this.cubes[0].addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
      this.cubes[0].setPos(0.0F, 3.0F, 1.0F);
      this.cubes[1].addBox(-13.0F, -7.0F, -1.0F, 18, 6, 2, 0.0F);
      this.cubes[1].setPos(-15.0F, 4.0F, 4.0F);
      this.cubes[2].addBox(-8.0F, -7.0F, -1.0F, 16, 6, 2, 0.0F);
      this.cubes[2].setPos(15.0F, 4.0F, 0.0F);
      this.cubes[3].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
      this.cubes[3].setPos(0.0F, 4.0F, -9.0F);
      this.cubes[4].addBox(-14.0F, -7.0F, -1.0F, 28, 6, 2, 0.0F);
      this.cubes[4].setPos(0.0F, 4.0F, 9.0F);
      this.cubes[0].xRot = 1.5707964F;
      this.cubes[1].yRot = 4.712389F;
      this.cubes[2].yRot = 1.5707964F;
      this.cubes[3].yRot = 3.1415927F;
      this.paddles[0] = this.makePaddle(true);
      this.paddles[0].setPos(3.0F, -5.0F, 9.0F);
      this.paddles[1] = this.makePaddle(false);
      this.paddles[1].setPos(3.0F, -5.0F, -9.0F);
      this.paddles[1].yRot = 3.1415927F;
      this.paddles[0].zRot = 0.19634955F;
      this.paddles[1].zRot = 0.19634955F;
      this.waterPatch = (new ModelPart(this, 0, 0)).setTexSize(128, 64);
      this.waterPatch.addBox(-14.0F, -9.0F, -3.0F, 28, 16, 3, 0.0F);
      this.waterPatch.setPos(0.0F, -3.0F, 1.0F);
      this.waterPatch.xRot = 1.5707964F;
   }

   public void render(Boat boat, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      this.setupAnim(boat, var2, var3, var4, var5, var6, var7);

      for(int var8 = 0; var8 < 5; ++var8) {
         this.cubes[var8].render(var7);
      }

      this.animatePaddle(boat, 0, var7, var2);
      this.animatePaddle(boat, 1, var7, var2);
   }

   public void renderSecondPass(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      GlStateManager.rotatef(90.0F, 0.0F, 1.0F, 0.0F);
      GlStateManager.colorMask(false, false, false, false);
      this.waterPatch.render(var7);
      GlStateManager.colorMask(true, true, true, true);
   }

   protected ModelPart makePaddle(boolean b) {
      ModelPart modelPart = (new ModelPart(this, 62, b?0:20)).setTexSize(128, 64);
      int var3 = 20;
      int var4 = 7;
      int var5 = 6;
      float var6 = -5.0F;
      modelPart.addBox(-1.0F, 0.0F, -5.0F, 2, 2, 18);
      modelPart.addBox(b?-1.001F:0.001F, -3.0F, 8.0F, 1, 6, 7);
      return modelPart;
   }

   protected void animatePaddle(Boat boat, int var2, float var3, float var4) {
      float var5 = boat.getRowingTime(var2, var4);
      ModelPart var6 = this.paddles[var2];
      var6.xRot = (float)Mth.clampedLerp(-1.0471975803375244D, -0.2617993950843811D, (double)((Mth.sin(-var5) + 1.0F) / 2.0F));
      var6.yRot = (float)Mth.clampedLerp(-0.7853981852531433D, 0.7853981852531433D, (double)((Mth.sin(-var5 + 1.0F) + 1.0F) / 2.0F));
      if(var2 == 1) {
         var6.yRot = 3.1415927F - var6.yRot;
      }

      var6.render(var3);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((Boat)var1, var2, var3, var4, var5, var6, var7);
   }
}
