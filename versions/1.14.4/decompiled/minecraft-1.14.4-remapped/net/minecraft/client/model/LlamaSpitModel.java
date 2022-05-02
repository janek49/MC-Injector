package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public class LlamaSpitModel extends EntityModel {
   private final ModelPart main;

   public LlamaSpitModel() {
      this(0.0F);
   }

   public LlamaSpitModel(float f) {
      this.main = new ModelPart(this);
      int var2 = 2;
      this.main.texOffs(0, 0).addBox(-4.0F, 0.0F, 0.0F, 2, 2, 2, f);
      this.main.texOffs(0, 0).addBox(0.0F, -4.0F, 0.0F, 2, 2, 2, f);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, -4.0F, 2, 2, 2, f);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, 0.0F, 2, 2, 2, f);
      this.main.texOffs(0, 0).addBox(2.0F, 0.0F, 0.0F, 2, 2, 2, f);
      this.main.texOffs(0, 0).addBox(0.0F, 2.0F, 0.0F, 2, 2, 2, f);
      this.main.texOffs(0, 0).addBox(0.0F, 0.0F, 2.0F, 2, 2, 2, f);
      this.main.setPos(0.0F, 0.0F, 0.0F);
   }

   public void render(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
      this.setupAnim(entity, var2, var3, var4, var5, var6, var7);
      this.main.render(var7);
   }
}
