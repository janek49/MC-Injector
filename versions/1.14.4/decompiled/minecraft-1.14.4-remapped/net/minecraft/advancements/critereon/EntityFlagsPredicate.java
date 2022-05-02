package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class EntityFlagsPredicate {
   public static final EntityFlagsPredicate ANY = (new EntityFlagsPredicate.Builder()).build();
   @Nullable
   private final Boolean isOnFire;
   @Nullable
   private final Boolean isSneaking;
   @Nullable
   private final Boolean isSprinting;
   @Nullable
   private final Boolean isSwimming;
   @Nullable
   private final Boolean isBaby;

   public EntityFlagsPredicate(@Nullable Boolean isOnFire, @Nullable Boolean isSneaking, @Nullable Boolean isSprinting, @Nullable Boolean isSwimming, @Nullable Boolean isBaby) {
      this.isOnFire = isOnFire;
      this.isSneaking = isSneaking;
      this.isSprinting = isSprinting;
      this.isSwimming = isSwimming;
      this.isBaby = isBaby;
   }

   public boolean matches(Entity entity) {
      return this.isOnFire != null && entity.isOnFire() != this.isOnFire.booleanValue()?false:(this.isSneaking != null && entity.isSneaking() != this.isSneaking.booleanValue()?false:(this.isSprinting != null && entity.isSprinting() != this.isSprinting.booleanValue()?false:(this.isSwimming != null && entity.isSwimming() != this.isSwimming.booleanValue()?false:this.isBaby == null || !(entity instanceof LivingEntity) || ((LivingEntity)entity).isBaby() == this.isBaby.booleanValue())));
   }

   @Nullable
   private static Boolean getOptionalBoolean(JsonObject jsonObject, String string) {
      return jsonObject.has(string)?Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, string)):null;
   }

   public static EntityFlagsPredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "entity flags");
         Boolean var2 = getOptionalBoolean(var1, "is_on_fire");
         Boolean var3 = getOptionalBoolean(var1, "is_sneaking");
         Boolean var4 = getOptionalBoolean(var1, "is_sprinting");
         Boolean var5 = getOptionalBoolean(var1, "is_swimming");
         Boolean var6 = getOptionalBoolean(var1, "is_baby");
         return new EntityFlagsPredicate(var2, var3, var4, var5, var6);
      } else {
         return ANY;
      }
   }

   private void addOptionalBoolean(JsonObject jsonObject, String string, @Nullable Boolean boolean) {
      if(boolean != null) {
         jsonObject.addProperty(string, boolean);
      }

   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         this.addOptionalBoolean(var1, "is_on_fire", this.isOnFire);
         this.addOptionalBoolean(var1, "is_sneaking", this.isSneaking);
         this.addOptionalBoolean(var1, "is_sprinting", this.isSprinting);
         this.addOptionalBoolean(var1, "is_swimming", this.isSwimming);
         this.addOptionalBoolean(var1, "is_baby", this.isBaby);
         return var1;
      }
   }

   public static class Builder {
      @Nullable
      private Boolean isOnFire;
      @Nullable
      private Boolean isSneaking;
      @Nullable
      private Boolean isSprinting;
      @Nullable
      private Boolean isSwimming;
      @Nullable
      private Boolean isBaby;

      public static EntityFlagsPredicate.Builder flags() {
         return new EntityFlagsPredicate.Builder();
      }

      public EntityFlagsPredicate.Builder setOnFire(@Nullable Boolean onFire) {
         this.isOnFire = onFire;
         return this;
      }

      public EntityFlagsPredicate build() {
         return new EntityFlagsPredicate(this.isOnFire, this.isSneaking, this.isSprinting, this.isSwimming, this.isBaby);
      }
   }
}
