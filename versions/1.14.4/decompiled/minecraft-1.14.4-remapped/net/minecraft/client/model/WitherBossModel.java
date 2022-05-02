package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.boss.wither.WitherBoss;

@ClientJarOnly
public class WitherBossModel extends EntityModel {
   private final ModelPart[] upperBodyParts;
   private final ModelPart[] heads;

   public WitherBossModel(float f) {
      this.texWidth = 64;
      this.texHeight = 64;
      this.upperBodyParts = new ModelPart[3];
      this.upperBodyParts[0] = new ModelPart(this, 0, 16);
      this.upperBodyParts[0].addBox(-10.0F, 3.9F, -0.5F, 20, 3, 3, f);
      this.upperBodyParts[1] = (new ModelPart(this)).setTexSize(this.texWidth, this.texHeight);
      this.upperBodyParts[1].setPos(-2.0F, 6.9F, -0.5F);
      this.upperBodyParts[1].texOffs(0, 22).addBox(0.0F, 0.0F, 0.0F, 3, 10, 3, f);
      this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0F, 1.5F, 0.5F, 11, 2, 2, f);
      this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0F, 4.0F, 0.5F, 11, 2, 2, f);
      this.upperBodyParts[1].texOffs(24, 22).addBox(-4.0F, 6.5F, 0.5F, 11, 2, 2, f);
      this.upperBodyParts[2] = new ModelPart(this, 12, 22);
      this.upperBodyParts[2].addBox(0.0F, 0.0F, 0.0F, 3, 6, 3, f);
      this.heads = new ModelPart[3];
      this.heads[0] = new ModelPart(this, 0, 0);
      this.heads[0].addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8, f);
      this.heads[1] = new ModelPart(this, 32, 0);
      this.heads[1].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
      this.heads[1].x = -8.0F;
      this.heads[1].y = 4.0F;
      this.heads[2] = new ModelPart(this, 32, 0);
      this.heads[2].addBox(-4.0F, -4.0F, -4.0F, 6, 6, 6, f);
      this.heads[2].x = 10.0F;
      this.heads[2].y = 4.0F;
   }

   public void render(WitherBoss witherBoss, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(witherBoss, var2, var3, var4, var5, var6, var7);

      for(ModelPart var11 : this.heads) {
         var11.render(var7);
      }

      for(ModelPart var11 : this.upperBodyParts) {
         var11.render(var7);
      }

   }

   public void setupAnim(WitherBoss witherBoss, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = Mth.cos(var4 * 0.1F);
      this.upperBodyParts[1].xRot = (0.065F + 0.05F * var8) * 3.1415927F;
      this.upperBodyParts[2].setPos(-2.0F, 6.9F + Mth.cos(this.upperBodyParts[1].xRot) * 10.0F, -0.5F + Mth.sin(this.upperBodyParts[1].xRot) * 10.0F);
      this.upperBodyParts[2].xRot = (0.265F + 0.1F * var8) * 3.1415927F;
      this.heads[0].yRot = var5 * 0.017453292F;
      this.heads[0].xRot = var6 * 0.017453292F;
   }

   public void prepareMobModel(WitherBoss witherBoss, float var2, float var3, float var4) {
      for(int var5 = 1; var5 < 3; ++var5) {
         this.heads[var5].yRot = (witherBoss.getHeadYRot(var5 - 1) - witherBoss.yBodyRot) * 0.017453292F;
         this.heads[var5].xRot = witherBoss.getHeadXRot(var5 - 1) * 0.017453292F;
      }

   }

   // $FF: synthetic method
   public void setupAnim(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim((WitherBoss)var1, var2, var3, var4, var5, var6, var7);
   }

   // $FF: synthetic method
   public void render(Entity var1, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.render((WitherBoss)var1, var2, var3, var4, var5, var6, var7);
   }
}
