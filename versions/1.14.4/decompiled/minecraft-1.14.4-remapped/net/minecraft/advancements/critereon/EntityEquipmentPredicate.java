package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.raid.Raid;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;

public class EntityEquipmentPredicate {
   public static final EntityEquipmentPredicate ANY = new EntityEquipmentPredicate(ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   public static final EntityEquipmentPredicate CAPTAIN = new EntityEquipmentPredicate(ItemPredicate.Builder.item().of((ItemLike)Items.WHITE_BANNER).hasNbt(Raid.getLeaderBannerInstance().getTag()).build(), ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY, ItemPredicate.ANY);
   private final ItemPredicate head;
   private final ItemPredicate chest;
   private final ItemPredicate legs;
   private final ItemPredicate feet;
   private final ItemPredicate mainhand;
   private final ItemPredicate offhand;

   public EntityEquipmentPredicate(ItemPredicate head, ItemPredicate chest, ItemPredicate legs, ItemPredicate feet, ItemPredicate mainhand, ItemPredicate offhand) {
      this.head = head;
      this.chest = chest;
      this.legs = legs;
      this.feet = feet;
      this.mainhand = mainhand;
      this.offhand = offhand;
   }

   public boolean matches(@Nullable Entity entity) {
      if(this == ANY) {
         return true;
      } else if(!(entity instanceof LivingEntity)) {
         return false;
      } else {
         LivingEntity var2 = (LivingEntity)entity;
         return !this.head.matches(var2.getItemBySlot(EquipmentSlot.HEAD))?false:(!this.chest.matches(var2.getItemBySlot(EquipmentSlot.CHEST))?false:(!this.legs.matches(var2.getItemBySlot(EquipmentSlot.LEGS))?false:(!this.feet.matches(var2.getItemBySlot(EquipmentSlot.FEET))?false:(!this.mainhand.matches(var2.getItemBySlot(EquipmentSlot.MAINHAND))?false:this.offhand.matches(var2.getItemBySlot(EquipmentSlot.OFFHAND))))));
      }
   }

   public static EntityEquipmentPredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "equipment");
         ItemPredicate var2 = ItemPredicate.fromJson(var1.get("head"));
         ItemPredicate var3 = ItemPredicate.fromJson(var1.get("chest"));
         ItemPredicate var4 = ItemPredicate.fromJson(var1.get("legs"));
         ItemPredicate var5 = ItemPredicate.fromJson(var1.get("feet"));
         ItemPredicate var6 = ItemPredicate.fromJson(var1.get("mainhand"));
         ItemPredicate var7 = ItemPredicate.fromJson(var1.get("offhand"));
         return new EntityEquipmentPredicate(var2, var3, var4, var5, var6, var7);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("head", this.head.serializeToJson());
         var1.add("chest", this.chest.serializeToJson());
         var1.add("legs", this.legs.serializeToJson());
         var1.add("feet", this.feet.serializeToJson());
         var1.add("mainhand", this.mainhand.serializeToJson());
         var1.add("offhand", this.offhand.serializeToJson());
         return var1;
      }
   }
}
