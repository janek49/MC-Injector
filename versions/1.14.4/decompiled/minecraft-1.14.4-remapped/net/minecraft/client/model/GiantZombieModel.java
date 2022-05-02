package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.world.entity.monster.Giant;

@ClientJarOnly
public class GiantZombieModel extends AbstractZombieModel {
   public GiantZombieModel() {
      this(0.0F, false);
   }

   public GiantZombieModel(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2?32:64);
   }

   public boolean isAggressive(Giant giant) {
      return false;
   }
}
