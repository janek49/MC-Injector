package net.minecraft.world.inventory;

import java.util.Collections;
import javax.annotation.Nullable;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;

public interface RecipeHolder {
   void setRecipeUsed(@Nullable Recipe var1);

   @Nullable
   Recipe getRecipeUsed();

   default void awardAndReset(Player player) {
      Recipe<?> var2 = this.getRecipeUsed();
      if(var2 != null && !var2.isSpecial()) {
         player.awardRecipes(Collections.singleton(var2));
         this.setRecipeUsed((Recipe)null);
      }

   }

   default boolean setRecipeUsed(Level level, ServerPlayer serverPlayer, Recipe recipe) {
      if(!recipe.isSpecial() && level.getGameRules().getBoolean(GameRules.RULE_LIMITED_CRAFTING) && !serverPlayer.getRecipeBook().contains(recipe)) {
         return false;
      } else {
         this.setRecipeUsed(recipe);
         return true;
      }
   }
}
