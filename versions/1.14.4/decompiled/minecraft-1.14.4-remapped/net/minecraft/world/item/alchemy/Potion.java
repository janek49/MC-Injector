package net.minecraft.world.item.alchemy;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.UnmodifiableIterator;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffectInstance;

public class Potion {
   private final String name;
   private final ImmutableList effects;

   public static Potion byName(String name) {
      return (Potion)Registry.POTION.get(ResourceLocation.tryParse(name));
   }

   public Potion(MobEffectInstance... mobEffectInstances) {
      this((String)null, mobEffectInstances);
   }

   public Potion(@Nullable String name, MobEffectInstance... mobEffectInstances) {
      this.name = name;
      this.effects = ImmutableList.copyOf(mobEffectInstances);
   }

   public String getName(String string) {
      return string + (this.name == null?Registry.POTION.getKey(this).getPath():this.name);
   }

   public List getEffects() {
      return this.effects;
   }

   public boolean hasInstantEffects() {
      if(!this.effects.isEmpty()) {
         UnmodifiableIterator var1 = this.effects.iterator();

         while(var1.hasNext()) {
            MobEffectInstance var2 = (MobEffectInstance)var1.next();
            if(var2.getEffect().isInstantenous()) {
               return true;
            }
         }
      }

      return false;
   }
}
