package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SmeltItemFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogManager.getLogger();

   private SmeltItemFunction(LootItemCondition[] lootItemConditions) {
      super(lootItemConditions);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      if(var1.isEmpty()) {
         return var1;
      } else {
         Optional<SmeltingRecipe> var3 = lootContext.getLevel().getRecipeManager().getRecipeFor(RecipeType.SMELTING, new SimpleContainer(new ItemStack[]{var1}), lootContext.getLevel());
         if(var3.isPresent()) {
            ItemStack var4 = ((SmeltingRecipe)var3.get()).getResultItem();
            if(!var4.isEmpty()) {
               ItemStack var5 = var4.copy();
               var5.setCount(var1.getCount());
               return var5;
            }
         }

         LOGGER.warn("Couldn\'t smelt {} because there is no smelting recipe", var1);
         return var1;
      }
   }

   public static LootItemConditionalFunction.Builder smelted() {
      return simpleBuilder(SmeltItemFunction::<init>);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("furnace_smelt"), SmeltItemFunction.class);
      }

      public SmeltItemFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         return new SmeltItemFunction(lootItemConditions);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
