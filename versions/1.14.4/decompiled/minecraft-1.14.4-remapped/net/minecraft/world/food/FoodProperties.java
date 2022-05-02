package net.minecraft.world.food;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.world.effect.MobEffectInstance;
import org.apache.commons.lang3.tuple.Pair;

public class FoodProperties {
   private final int nutrition;
   private final float saturationModifier;
   private final boolean isMeat;
   private final boolean canAlwaysEat;
   private final boolean fastFood;
   private final List effects;

   private FoodProperties(int nutrition, float saturationModifier, boolean isMeat, boolean canAlwaysEat, boolean fastFood, List effects) {
      this.nutrition = nutrition;
      this.saturationModifier = saturationModifier;
      this.isMeat = isMeat;
      this.canAlwaysEat = canAlwaysEat;
      this.fastFood = fastFood;
      this.effects = effects;
   }

   public int getNutrition() {
      return this.nutrition;
   }

   public float getSaturationModifier() {
      return this.saturationModifier;
   }

   public boolean isMeat() {
      return this.isMeat;
   }

   public boolean canAlwaysEat() {
      return this.canAlwaysEat;
   }

   public boolean isFastFood() {
      return this.fastFood;
   }

   public List getEffects() {
      return this.effects;
   }

   public static class Builder {
      private int nutrition;
      private float saturationModifier;
      private boolean isMeat;
      private boolean canAlwaysEat;
      private boolean fastFood;
      private final List effects = Lists.newArrayList();

      public FoodProperties.Builder nutrition(int nutrition) {
         this.nutrition = nutrition;
         return this;
      }

      public FoodProperties.Builder saturationMod(float saturationModifier) {
         this.saturationModifier = saturationModifier;
         return this;
      }

      public FoodProperties.Builder meat() {
         this.isMeat = true;
         return this;
      }

      public FoodProperties.Builder alwaysEat() {
         this.canAlwaysEat = true;
         return this;
      }

      public FoodProperties.Builder fast() {
         this.fastFood = true;
         return this;
      }

      public FoodProperties.Builder effect(MobEffectInstance mobEffectInstance, float var2) {
         this.effects.add(Pair.of(mobEffectInstance, Float.valueOf(var2)));
         return this;
      }

      public FoodProperties build() {
         return new FoodProperties(this.nutrition, this.saturationModifier, this.isMeat, this.canAlwaysEat, this.fastFood, this.effects);
      }
   }
}
