package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class SquidModel extends EntityModel {
   private final ModelPart body;
   private final ModelPart[] tentacles = new ModelPart[8];

   public SquidModel() {
      int var1 = -16;
      this.body = new ModelPart(this, 0, 0);
      this.body.addBox(-6.0F, -8.0F, -6.0F, 12, 16, 12);
      this.body.y += 8.0F;

      for(int var2 = 0; var2 < this.tentacles.length; ++var2) {
         this.tentacles[var2] = new ModelPart(this, 48, 0);
         double var3 = (double)var2 * 3.141592653589793D * 2.0D / (double)this.tentacles.length;
         float var5 = (float)Math.cos(var3) * 5.0F;
         float var6 = (float)Math.sin(var3) * 5.0F;
         this.tentacles[var2].addBox(-1.0F, 0.0F, -1.0F, 2, 18, 2);
         this.tentacles[var2].x = var5;
         this.tentacles[var2].z = var6;
         this.tentacles[var2].y = 15.0F;
         var3 = (double)var2 * 3.141592653589793D * -2.0D / (double)this.tentacles.length + 1.5707963267948966D;
         this.tentacles[var2].yRot = (float)var3;
      }

   }

   public void setupAnim(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      for(ModelPart var11 : this.tentacles) {
         var11.xRot = var4;
      }

   }

   public void render(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(entity, var2, var3, var4, var5, var6, var7);
      this.body.render(var7);

      for(ModelPart var11 : this.tentacles) {
         var11.render(var7);
      }

   }
}
