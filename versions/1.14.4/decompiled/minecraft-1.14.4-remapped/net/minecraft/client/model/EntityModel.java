package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.Model;
import net.minecraft.world.entity.Entity;

@ClientJarOnly
public abstract class EntityModel extends Model {
   public float attackTime;
   public boolean riding;
   public boolean young = true;

   public void render(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void setupAnim(Entity entity, float var2, float var3, float var4, float var5, float var6, float var7) {
   }

   public void prepareMobModel(Entity entity, float var2, float var3, float var4) {
   }

   public void copyPropertiesTo(EntityModel entityModel) {
      entityModel.attackTime = this.attackTime;
      entityModel.riding = this.riding;
      entityModel.young = this.young;
   }
}
