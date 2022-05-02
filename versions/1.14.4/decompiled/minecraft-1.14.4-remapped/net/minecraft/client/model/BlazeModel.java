package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class BlazeModel extends EntityModel {
   private final ModelPart[] upperBodyParts = new ModelPart[12];
   private final ModelPart head;

   public BlazeModel() {
      for(int var1 = 0; var1 < this.upperBodyParts.length; ++var1) {
         this.upperBodyParts[var1] = new ModelPart(this, 0, 16);
         this.upperBodyParts[var1].addBox(0.0F, 0.0F, 0.0F, 2, 8, 2);
      }

      this.head = new ModelPart(this, 0, 0);
      this.head.addBox(-4.0F, -4.0F, -4.0F, 8, 8, 8);
   }

   public void render(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(entity, var2, var3, var4, var5, var6, var7);
      this.head.render(var7);

      for(ModelPart var11 : this.upperBodyParts) {
         var11.render(var7);
      }

   }

   public void setupAnim(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      float var8 = var4 * 3.1415927F * -0.1F;

      for(int var9 = 0; var9 < 4; ++var9) {
         this.upperBodyParts[var9].y = -2.0F + Mth.cos(((float)(var9 * 2) + var4) * 0.25F);
         this.upperBodyParts[var9].x = Mth.cos(var8) * 9.0F;
         this.upperBodyParts[var9].z = Mth.sin(var8) * 9.0F;
         ++var8;
      }

      var8 = 0.7853982F + var4 * 3.1415927F * 0.03F;

      for(int var9 = 4; var9 < 8; ++var9) {
         this.upperBodyParts[var9].y = 2.0F + Mth.cos(((float)(var9 * 2) + var4) * 0.25F);
         this.upperBodyParts[var9].x = Mth.cos(var8) * 7.0F;
         this.upperBodyParts[var9].z = Mth.sin(var8) * 7.0F;
         ++var8;
      }

      var8 = 0.47123894F + var4 * 3.1415927F * -0.05F;

      for(int var9 = 8; var9 < 12; ++var9) {
         this.upperBodyParts[var9].y = 11.0F + Mth.cos(((float)var9 * 1.5F + var4) * 0.5F);
         this.upperBodyParts[var9].x = Mth.cos(var8) * 5.0F;
         this.upperBodyParts[var9].z = Mth.sin(var8) * 5.0F;
         ++var8;
      }

      this.head.yRot = var5 * 0.017453292F;
      this.head.xRot = var6 * 0.017453292F;
   }
}
