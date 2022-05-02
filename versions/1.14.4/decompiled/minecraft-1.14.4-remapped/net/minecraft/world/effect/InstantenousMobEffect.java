package net.minecraft.world.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;

public class InstantenousMobEffect extends MobEffect {
   public InstantenousMobEffect(MobEffectCategory mobEffectCategory, int var2) {
      super(mobEffectCategory, var2);
   }

   public boolean isInstantenous() {
      return true;
   }

   public boolean isDurationEffectTick(int var1, int var2) {
      return var1 >= 1;
   }
}
