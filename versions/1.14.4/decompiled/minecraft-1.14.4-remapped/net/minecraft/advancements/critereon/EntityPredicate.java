package net.minecraft.advancements.critereon;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.DistancePredicate;
import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityFlagsPredicate;
import net.minecraft.advancements.critereon.EntityTypePredicate;
import net.minecraft.advancements.critereon.LocationPredicate;
import net.minecraft.advancements.critereon.MobEffectsPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.Tag;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Cat;
import net.minecraft.world.phys.Vec3;

public class EntityPredicate {
   public static final EntityPredicate ANY = new EntityPredicate(EntityTypePredicate.ANY, DistancePredicate.ANY, LocationPredicate.ANY, MobEffectsPredicate.ANY, NbtPredicate.ANY, EntityFlagsPredicate.ANY, EntityEquipmentPredicate.ANY, (ResourceLocation)null);
   public static final EntityPredicate[] ANY_ARRAY = new EntityPredicate[0];
   private final EntityTypePredicate entityType;
   private final DistancePredicate distanceToPlayer;
   private final LocationPredicate location;
   private final MobEffectsPredicate effects;
   private final NbtPredicate nbt;
   private final EntityFlagsPredicate flags;
   private final EntityEquipmentPredicate equipment;
   private final ResourceLocation catType;

   private EntityPredicate(EntityTypePredicate entityType, DistancePredicate distanceToPlayer, LocationPredicate location, MobEffectsPredicate effects, NbtPredicate nbt, EntityFlagsPredicate flags, EntityEquipmentPredicate equipment, @Nullable ResourceLocation catType) {
      this.entityType = entityType;
      this.distanceToPlayer = distanceToPlayer;
      this.location = location;
      this.effects = effects;
      this.nbt = nbt;
      this.flags = flags;
      this.equipment = equipment;
      this.catType = catType;
   }

   public boolean matches(ServerPlayer serverPlayer, @Nullable Entity entity) {
      return this.matches(serverPlayer.getLevel(), new Vec3(serverPlayer.x, serverPlayer.y, serverPlayer.z), entity);
   }

   public boolean matches(ServerLevel serverLevel, Vec3 vec3, @Nullable Entity entity) {
      return this == ANY?true:(entity == null?false:(!this.entityType.matches(entity.getType())?false:(!this.distanceToPlayer.matches(vec3.x, vec3.y, vec3.z, entity.x, entity.y, entity.z)?false:(!this.location.matches(serverLevel, entity.x, entity.y, entity.z)?false:(!this.effects.matches(entity)?false:(!this.nbt.matches(entity)?false:(!this.flags.matches(entity)?false:(!this.equipment.matches(entity)?false:this.catType == null || entity instanceof Cat && ((Cat)entity).getResourceLocation().equals(this.catType)))))))));
   }

   public static EntityPredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "entity");
         EntityTypePredicate var2 = EntityTypePredicate.fromJson(var1.get("type"));
         DistancePredicate var3 = DistancePredicate.fromJson(var1.get("distance"));
         LocationPredicate var4 = LocationPredicate.fromJson(var1.get("location"));
         MobEffectsPredicate var5 = MobEffectsPredicate.fromJson(var1.get("effects"));
         NbtPredicate var6 = NbtPredicate.fromJson(var1.get("nbt"));
         EntityFlagsPredicate var7 = EntityFlagsPredicate.fromJson(var1.get("flags"));
         EntityEquipmentPredicate var8 = EntityEquipmentPredicate.fromJson(var1.get("equipment"));
         ResourceLocation var9 = var1.has("catType")?new ResourceLocation(GsonHelper.getAsString(var1, "catType")):null;
         return (new EntityPredicate.Builder()).entityType(var2).distance(var3).located(var4).effects(var5).nbt(var6).flags(var7).equipment(var8).catType(var9).build();
      } else {
         return ANY;
      }
   }

   public static EntityPredicate[] fromJsonArray(@Nullable JsonElement jsonArray) {
      if(jsonArray != null && !jsonArray.isJsonNull()) {
         JsonArray var1 = GsonHelper.convertToJsonArray(jsonArray, "entities");
         EntityPredicate[] vars2 = new EntityPredicate[var1.size()];

         for(int var3 = 0; var3 < var1.size(); ++var3) {
            vars2[var3] = fromJson(var1.get(var3));
         }

         return vars2;
      } else {
         return ANY_ARRAY;
      }
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("type", this.entityType.serializeToJson());
         var1.add("distance", this.distanceToPlayer.serializeToJson());
         var1.add("location", this.location.serializeToJson());
         var1.add("effects", this.effects.serializeToJson());
         var1.add("nbt", this.nbt.serializeToJson());
         var1.add("flags", this.flags.serializeToJson());
         var1.add("equipment", this.equipment.serializeToJson());
         if(this.catType != null) {
            var1.addProperty("catType", this.catType.toString());
         }

         return var1;
      }
   }

   public static JsonElement serializeArrayToJson(EntityPredicate[] entityPredicates) {
      if(entityPredicates == ANY_ARRAY) {
         return JsonNull.INSTANCE;
      } else {
         JsonArray var1 = new JsonArray();

         for(EntityPredicate var5 : entityPredicates) {
            JsonElement var6 = var5.serializeToJson();
            if(!var6.isJsonNull()) {
               var1.add(var6);
            }
         }

         return var1;
      }
   }

   public static class Builder {
      private EntityTypePredicate entityType = EntityTypePredicate.ANY;
      private DistancePredicate distanceToPlayer = DistancePredicate.ANY;
      private LocationPredicate location = LocationPredicate.ANY;
      private MobEffectsPredicate effects = MobEffectsPredicate.ANY;
      private NbtPredicate nbt = NbtPredicate.ANY;
      private EntityFlagsPredicate flags = EntityFlagsPredicate.ANY;
      private EntityEquipmentPredicate equipment = EntityEquipmentPredicate.ANY;
      @Nullable
      private ResourceLocation catType;

      public static EntityPredicate.Builder entity() {
         return new EntityPredicate.Builder();
      }

      public EntityPredicate.Builder of(EntityType entityType) {
         this.entityType = EntityTypePredicate.of(entityType);
         return this;
      }

      public EntityPredicate.Builder of(Tag tag) {
         this.entityType = EntityTypePredicate.of(tag);
         return this;
      }

      public EntityPredicate.Builder of(ResourceLocation catType) {
         this.catType = catType;
         return this;
      }

      public EntityPredicate.Builder entityType(EntityTypePredicate entityType) {
         this.entityType = entityType;
         return this;
      }

      public EntityPredicate.Builder distance(DistancePredicate distanceToPlayer) {
         this.distanceToPlayer = distanceToPlayer;
         return this;
      }

      public EntityPredicate.Builder located(LocationPredicate location) {
         this.location = location;
         return this;
      }

      public EntityPredicate.Builder effects(MobEffectsPredicate effects) {
         this.effects = effects;
         return this;
      }

      public EntityPredicate.Builder nbt(NbtPredicate nbt) {
         this.nbt = nbt;
         return this;
      }

      public EntityPredicate.Builder flags(EntityFlagsPredicate flags) {
         this.flags = flags;
         return this;
      }

      public EntityPredicate.Builder equipment(EntityEquipmentPredicate equipment) {
         this.equipment = equipment;
         return this;
      }

      public EntityPredicate.Builder catType(@Nullable ResourceLocation catType) {
         this.catType = catType;
         return this;
      }

      public EntityPredicate build() {
         return new EntityPredicate(this.entityType, this.distanceToPlayer, this.location, this.effects, this.nbt, this.flags, this.equipment, this.catType);
      }
   }
}
