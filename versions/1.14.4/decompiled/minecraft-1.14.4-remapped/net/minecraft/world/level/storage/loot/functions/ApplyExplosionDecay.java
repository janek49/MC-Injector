package net.minecraft.world.level.storage.loot.functions;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import java.util.Random;
import java.util.function.Function;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class ApplyExplosionDecay extends LootItemConditionalFunction {
   private ApplyExplosionDecay(LootItemCondition[] lootItemConditions) {
      super(lootItemConditions);
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      Float var3 = (Float)lootContext.getParamOrNull(LootContextParams.EXPLOSION_RADIUS);
      if(var3 != null) {
         Random var4 = lootContext.getRandom();
         float var5 = 1.0F / var3.floatValue();
         int var6 = var1.getCount();
         int var7 = 0;

         for(int var8 = 0; var8 < var6; ++var8) {
            if(var4.nextFloat() <= var5) {
               ++var7;
            }
         }

         var1.setCount(var7);
      }

      return var1;
   }

   public static LootItemConditionalFunction.Builder explosionDecay() {
      return simpleBuilder(ApplyExplosionDecay::<init>);
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      protected Serializer() {
         super(new ResourceLocation("explosion_decay"), ApplyExplosionDecay.class);
      }

      public ApplyExplosionDecay deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         return new ApplyExplosionDecay(lootItemConditions);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
