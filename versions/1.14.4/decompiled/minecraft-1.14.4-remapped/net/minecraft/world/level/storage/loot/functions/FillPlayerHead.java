package net.minecraft.world.level.storage.loot.functions;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.mojang.authlib.GameProfile;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class FillPlayerHead extends LootItemConditionalFunction {
   private final LootContext.EntityTarget entityTarget;

   public FillPlayerHead(LootItemCondition[] lootItemConditions, LootContext.EntityTarget entityTarget) {
      super(lootItemConditions);
      this.entityTarget = entityTarget;
   }

   public Set getReferencedContextParams() {
      return ImmutableSet.of(this.entityTarget.getParam());
   }

   public ItemStack run(ItemStack var1, LootContext lootContext) {
      if(var1.getItem() == Items.PLAYER_HEAD) {
         Entity var3 = (Entity)lootContext.getParamOrNull(this.entityTarget.getParam());
         if(var3 instanceof Player) {
            GameProfile var4 = ((Player)var3).getGameProfile();
            var1.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), var4));
         }
      }

      return var1;
   }

   public static class Serializer extends LootItemConditionalFunction.Serializer {
      public Serializer() {
         super(new ResourceLocation("fill_player_head"), FillPlayerHead.class);
      }

      public void serialize(JsonObject jsonObject, FillPlayerHead fillPlayerHead, JsonSerializationContext jsonSerializationContext) {
         super.serialize(jsonObject, (LootItemConditionalFunction)fillPlayerHead, jsonSerializationContext);
         jsonObject.add("entity", jsonSerializationContext.serialize(fillPlayerHead.entityTarget));
      }

      public FillPlayerHead deserialize(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootItemCondition[] lootItemConditions) {
         LootContext.EntityTarget var4 = (LootContext.EntityTarget)GsonHelper.getAsObject(jsonObject, "entity", jsonDeserializationContext, LootContext.EntityTarget.class);
         return new FillPlayerHead(lootItemConditions, var4);
      }

      // $FF: synthetic method
      public LootItemConditionalFunction deserialize(JsonObject var1, JsonDeserializationContext var2, LootItemCondition[] var3) {
         return this.deserialize(var1, var2, var3);
      }
   }
}
