package net.minecraft.world.item.crafting;

import com.google.gson.JsonObject;
import java.util.function.Function;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.ArmorDyeRecipe;
import net.minecraft.world.item.crafting.BannerDuplicateRecipe;
import net.minecraft.world.item.crafting.BlastingRecipe;
import net.minecraft.world.item.crafting.BookCloningRecipe;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.FireworkRocketRecipe;
import net.minecraft.world.item.crafting.FireworkStarFadeRecipe;
import net.minecraft.world.item.crafting.FireworkStarRecipe;
import net.minecraft.world.item.crafting.MapCloningRecipe;
import net.minecraft.world.item.crafting.MapExtendingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RepairItemRecipe;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.ShapelessRecipe;
import net.minecraft.world.item.crafting.ShieldDecorationRecipe;
import net.minecraft.world.item.crafting.ShulkerBoxColoring;
import net.minecraft.world.item.crafting.SimpleCookingSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.item.crafting.SingleItemRecipe;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.item.crafting.SuspiciousStewRecipe;
import net.minecraft.world.item.crafting.TippedArrowRecipe;

public interface RecipeSerializer {
   RecipeSerializer SHAPED_RECIPE = register("crafting_shaped", new ShapedRecipe.Serializer());
   RecipeSerializer SHAPELESS_RECIPE = register("crafting_shapeless", new ShapelessRecipe.Serializer());
   SimpleRecipeSerializer ARMOR_DYE = (SimpleRecipeSerializer)register("crafting_special_armordye", new SimpleRecipeSerializer(ArmorDyeRecipe::<init>));
   SimpleRecipeSerializer BOOK_CLONING = (SimpleRecipeSerializer)register("crafting_special_bookcloning", new SimpleRecipeSerializer(BookCloningRecipe::<init>));
   SimpleRecipeSerializer MAP_CLONING = (SimpleRecipeSerializer)register("crafting_special_mapcloning", new SimpleRecipeSerializer(MapCloningRecipe::<init>));
   SimpleRecipeSerializer MAP_EXTENDING = (SimpleRecipeSerializer)register("crafting_special_mapextending", new SimpleRecipeSerializer(MapExtendingRecipe::<init>));
   SimpleRecipeSerializer FIREWORK_ROCKET = (SimpleRecipeSerializer)register("crafting_special_firework_rocket", new SimpleRecipeSerializer(FireworkRocketRecipe::<init>));
   SimpleRecipeSerializer FIREWORK_STAR = (SimpleRecipeSerializer)register("crafting_special_firework_star", new SimpleRecipeSerializer(FireworkStarRecipe::<init>));
   SimpleRecipeSerializer FIREWORK_STAR_FADE = (SimpleRecipeSerializer)register("crafting_special_firework_star_fade", new SimpleRecipeSerializer(FireworkStarFadeRecipe::<init>));
   SimpleRecipeSerializer TIPPED_ARROW = (SimpleRecipeSerializer)register("crafting_special_tippedarrow", new SimpleRecipeSerializer(TippedArrowRecipe::<init>));
   SimpleRecipeSerializer BANNER_DUPLICATE = (SimpleRecipeSerializer)register("crafting_special_bannerduplicate", new SimpleRecipeSerializer(BannerDuplicateRecipe::<init>));
   SimpleRecipeSerializer SHIELD_DECORATION = (SimpleRecipeSerializer)register("crafting_special_shielddecoration", new SimpleRecipeSerializer(ShieldDecorationRecipe::<init>));
   SimpleRecipeSerializer SHULKER_BOX_COLORING = (SimpleRecipeSerializer)register("crafting_special_shulkerboxcoloring", new SimpleRecipeSerializer(ShulkerBoxColoring::<init>));
   SimpleRecipeSerializer SUSPICIOUS_STEW = (SimpleRecipeSerializer)register("crafting_special_suspiciousstew", new SimpleRecipeSerializer(SuspiciousStewRecipe::<init>));
   SimpleRecipeSerializer REPAIR_ITEM = (SimpleRecipeSerializer)register("crafting_special_repairitem", new SimpleRecipeSerializer(RepairItemRecipe::<init>));
   SimpleCookingSerializer SMELTING_RECIPE = (SimpleCookingSerializer)register("smelting", new SimpleCookingSerializer(SmeltingRecipe::<init>, 200));
   SimpleCookingSerializer BLASTING_RECIPE = (SimpleCookingSerializer)register("blasting", new SimpleCookingSerializer(BlastingRecipe::<init>, 100));
   SimpleCookingSerializer SMOKING_RECIPE = (SimpleCookingSerializer)register("smoking", new SimpleCookingSerializer(SmokingRecipe::<init>, 100));
   SimpleCookingSerializer CAMPFIRE_COOKING_RECIPE = (SimpleCookingSerializer)register("campfire_cooking", new SimpleCookingSerializer(CampfireCookingRecipe::<init>, 100));
   RecipeSerializer STONECUTTER = register("stonecutting", new SingleItemRecipe.Serializer(StonecutterRecipe::<init>));

   Recipe fromJson(ResourceLocation var1, JsonObject var2);

   Recipe fromNetwork(ResourceLocation var1, FriendlyByteBuf var2);

   void toNetwork(FriendlyByteBuf var1, Recipe var2);

   static default RecipeSerializer register(String string, RecipeSerializer var1) {
      return (RecipeSerializer)Registry.register(Registry.RECIPE_SERIALIZER, (String)string, var1);
   }
}
