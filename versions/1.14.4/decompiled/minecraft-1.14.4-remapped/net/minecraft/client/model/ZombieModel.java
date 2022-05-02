package net.minecraft.client.model;

import com.fox2code.repacker.ClientJarOnly;
import net.minecraft.client.model.AbstractZombieModel;
import net.minecraft.world.entity.monster.Zombie;

@ClientJarOnly
public class ZombieModel extends AbstractZombieModel {
   public ZombieModel() {
      this(0.0F, false);
   }

   public ZombieModel(float var1, boolean var2) {
      super(var1, 0.0F, 64, var2?32:64);
   }

   protected ZombieModel(float var1, float var2, int var3, int var4) {
      super(var1, var2, var3, var4);
   }

   public boolean isAggressive(Zombie zombie) {
      return zombie.isAggressive();
   }
}
