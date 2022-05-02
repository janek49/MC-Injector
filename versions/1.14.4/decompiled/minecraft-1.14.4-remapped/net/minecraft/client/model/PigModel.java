package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.QuadrupedModel;

@ClientJarOnly
public class PigModel extends QuadrupedModel {
   public PigModel() {
      this(0.0F);
   }

   public PigModel(float f) {
      super(6, f);
      this.head.texOffs(16, 16).addBox(-2.0F, 0.0F, -9.0F, 4, 3, 1, f);
      this.yHeadOffs = 4.0F;
   }
}
