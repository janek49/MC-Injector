package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class LeashKnotModel extends EntityModel {
   private final ModelPart knot;

   public LeashKnotModel() {
      this(0, 0, 32, 32);
   }

   public LeashKnotModel(int var1, int var2, int texWidth, int texHeight) {
      this.texWidth = texWidth;
      this.texHeight = texHeight;
      this.knot = new ModelPart(this, var1, var2);
      this.knot.addBox(-3.0F, -6.0F, -3.0F, 6, 8, 6, 0.0F);
      this.knot.setPos(0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(entity, var2, var3, var4, var5, var6, var7);
      this.knot.render(var7);
   }

   public void setupAnim(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(entity, var2, var3, var4, var5, var6, var7);
      this.knot.yRot = var5 * 0.017453292F;
      this.knot.xRot = var6 * 0.017453292F;
   }
}
