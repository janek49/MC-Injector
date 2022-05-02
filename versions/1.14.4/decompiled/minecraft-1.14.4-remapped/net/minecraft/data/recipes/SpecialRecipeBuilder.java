package net.minecraft.data.recipes;

import com.google.gson.JsonObject;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;

public class SpecialRecipeBuilder {
   private final SimpleRecipeSerializer serializer;

   public SpecialRecipeBuilder(SimpleRecipeSerializer serializer) {
      this.serializer = serializer;
   }

   public static SpecialRecipeBuilder special(SimpleRecipeSerializer simpleRecipeSerializer) {
      return new SpecialRecipeBuilder(simpleRecipeSerializer);
   }

   public void save(Consumer consumer, final String string) {
      consumer.accept(new FinishedRecipe() {
         public void serializeRecipeData(JsonObject jsonObject) {
         }

         public RecipeSerializer getType() {
            return SpecialRecipeBuilder.this.serializer;
         }

         public ResourceLocation getId() {
            return new ResourceLocation(string);
         }

         @Nullable
         public JsonObject serializeAdvancement() {
            return null;
         }

         public ResourceLocation getAdvancementId() {
            return new ResourceLocation("");
         }
      });
   }
}
