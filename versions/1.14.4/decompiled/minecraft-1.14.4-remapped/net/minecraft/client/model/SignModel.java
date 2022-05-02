package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;

@ClientJarOnly
public class SignModel extends Model {
   private final ModelPart sign = new ModelPart(this, 0, 0);
   private final ModelPart stick;

   public SignModel() {
      this.sign.addBox(-12.0F, -14.0F, -1.0F, 24, 12, 2, 0.0F);
      this.stick = new ModelPart(this, 0, 14);
      this.stick.addBox(-1.0F, -2.0F, -1.0F, 2, 14, 2, 0.0F);
   }

   public void render() {
      this.sign.render(0.0625F);
      this.stick.render(0.0625F);
   }

   public ModelPart getStick() {
      return this.stick;
   }
}
