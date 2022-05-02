package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class ShulkerBulletModel extends EntityModel {
   private final ModelPart main;

   public ShulkerBulletModel() {
      this.texWidth = 64;
      this.texHeight = 32;
      this.main = new ModelPart(this);
      this.main.texOffs(0, 0).addBox(-4.0F, -4.0F, -1.0F, 8, 8, 2, 0.0F);
      this.main.texOffs(0, 10).addBox(-1.0F, -4.0F, -4.0F, 2, 8, 8, 0.0F);
      this.main.texOffs(20, 0).addBox(-4.0F, -1.0F, -4.0F, 8, 2, 8, 0.0F);
      this.main.setPos(0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(entity, var2, var3, var4, var5, var6, var7);
      this.main.render(var7);
   }

   public void setupAnim(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      super.setupAnim(entity, var2, var3, var4, var5, var6, var7);
      this.main.yRot = var5 * 0.017453292F;
      this.main.xRot = var6 * 0.017453292F;
   }
}
