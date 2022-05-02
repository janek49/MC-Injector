package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.Set;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class SetNameFunction extends LootItemConditionalFunction {
   private static final Logger LOGGER = LogManager.getLogger();
   private final Component name;
   @Nullable
   private final LootContext.EntityTarget resolutionContext;

   private SetNameFunction(LootItemCondition[] lootItemConditions, @Nullable Component name, @Nullable LootContext.EntityTarget resolutionContext) {
      super(lootItemConditions);
      this.name = name;
      this.resolutionContext = resolutionContext;
   }

   public Set getReferencedContextParams() {
      return this.resolutionContext != null?ImmutableSet.of(this.resolutionContext.getParam()):ImmutableSet.of();
   }

   public static UnaryOperator createResolver(LootContext lootContext, @Nullable LootContext.EntityTarget lootContext$EntityTarget) {
      if(lootContext$EntityTarget != null) {
         Entity var2 = (Entity)lootContext.getParamOrNull(lootContext$EntityTarget.getParam());
         if(var2 != null) {
            CommandSourceStack var3 = var2.createCommandSourceStack().withPermission(2);
            return (var2x) -> {
               try {
                  return ComponentUtils.updateForEntity(var3, var2x, var2, 0);
               } catch (CommandSyntaxException var4) {
                  LOGGER.warn("Failed to resolve text component", var4);
                  return var2x;
               }
            };
         }
      }

      return (component) -> {
         return component;
      };
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      if(this.name != null) {
         var1.setHoverName((Component)createResolver(lootContext, this.resolutionContext).apply(this.name));
      }

      return var1;
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("set_name"), SetNameFunction.class);
      }

      public void serialize(JsonObject jsonObject, SetNameFunction setNameFunction, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)setNameFunction, jsonSerializationContext);
         if(setNameFunction.name != null) {
            jsonObject.add("name", Component.Serializer.toJsonTree(setNameFunction.name));
         }

         if(setNameFunction.resolutionContext != null) {
            jsonObject.add("entity", jsonSerializationContext.serialize(setNameFunction.resolutionContext));
         }

      }

      public SetNameFunction deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         Component var4 = Component.Serializer.fromJson(jsonObject.get("name"));
         LootContext.EntityTarget var5 = (LootContext.EntityTarget)GsonHelper.getAsObject(jsonObject, "entity", (Object)null, jsonDeserializationContext, LootContext.EntityTarget.class);
         return new SetNameFunction(lootItemConditions, var4, var5);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
