package net.minecraft.world;

import javax.annotation.concurrent.Immutable;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;

@Immutable
public class DifficultyInstance {
   private final Difficulty base;
   private final float effectiveDifficulty;

   public DifficultyInstance(Difficulty base, long var2, long var4, float var6) {
      this.base = base;
      this.effectiveDifficulty = this.calculateDifficulty(base, var2, var4, var6);
   }

   public Difficulty getDifficulty() {
      return this.base;
   }

   public float getEffectiveDifficulty() {
      return this.effectiveDifficulty;
   }

   public boolean isHarderThan(float f) {
      return this.effectiveDifficulty > f;
   }

   public float getSpecialMultiplier() {
      return this.effectiveDifficulty < 2.0F?0.0F:(this.effectiveDifficulty > 4.0F?1.0F:(this.effectiveDifficulty - 2.0F) / 2.0F);
   }

   private float calculateDifficulty(Difficulty difficulty, long var2, long var4, float var6) {
      if(difficulty == Difficulty.PEACEFUL) {
         return 0.0F;
      } else {
         boolean var7 = difficulty == Difficulty.HARD;
         float var8 = 0.75F;
         float var9 = Mth.clamp(((float)var2 + -72000.0F) / 1440000.0F, 0.0F, 1.0F) * 0.25F;
         var8 = var8 + var9;
         float var10 = 0.0F;
         var10 = var10 + Mth.clamp((float)var4 / 3600000.0F, 0.0F, 1.0F) * (var7?1.0F:0.75F);
         var10 = var10 + Mth.clamp(var6 * 0.25F, 0.0F, var9);
         if(difficulty == Difficulty.EASY) {
            var10 *= 0.5F;
         }

         var8 = var8 + var10;
         return (float)difficulty.getId() * var8;
      }
   }
}
