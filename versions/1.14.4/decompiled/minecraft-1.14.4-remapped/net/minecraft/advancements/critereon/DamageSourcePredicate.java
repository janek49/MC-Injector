package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.phys.Vec3;

public class DamageSourcePredicate {
   public static final DamageSourcePredicate ANY = DamageSourcePredicate.Builder.damageType().build();
   private final Boolean isProjectile;
   private final Boolean isExplosion;
   private final Boolean bypassesArmor;
   private final Boolean bypassesInvulnerability;
   private final Boolean bypassesMagic;
   private final Boolean isFire;
   private final Boolean isMagic;
   private final Boolean isLightning;
   private final EntityPredicate directEntity;
   private final EntityPredicate sourceEntity;

   public DamageSourcePredicate(@Nullable Boolean isProjectile, @Nullable Boolean isExplosion, @Nullable Boolean bypassesArmor, @Nullable Boolean bypassesInvulnerability, @Nullable Boolean bypassesMagic, @Nullable Boolean isFire, @Nullable Boolean isMagic, @Nullable Boolean isLightning, EntityPredicate directEntity, EntityPredicate sourceEntity) {
      this.isProjectile = isProjectile;
      this.isExplosion = isExplosion;
      this.bypassesArmor = bypassesArmor;
      this.bypassesInvulnerability = bypassesInvulnerability;
      this.bypassesMagic = bypassesMagic;
      this.isFire = isFire;
      this.isMagic = isMagic;
      this.isLightning = isLightning;
      this.directEntity = directEntity;
      this.sourceEntity = sourceEntity;
   }

   public boolean matches(ServerPlayer serverPlayer, DamageSource damageSource) {
      return this.matches(serverPlayer.getLevel(), new Vec3(serverPlayer.x, serverPlayer.y, serverPlayer.z), damageSource);
   }

   public boolean matches(ServerLevel serverLevel, Vec3 vec3, DamageSource damageSource) {
      return this == ANY?true:(this.isProjectile != null && this.isProjectile.booleanValue() != damageSource.isProjectile()?false:(this.isExplosion != null && this.isExplosion.booleanValue() != damageSource.isExplosion()?false:(this.bypassesArmor != null && this.bypassesArmor.booleanValue() != damageSource.isBypassArmor()?false:(this.bypassesInvulnerability != null && this.bypassesInvulnerability.booleanValue() != damageSource.isBypassInvul()?false:(this.bypassesMagic != null && this.bypassesMagic.booleanValue() != damageSource.isBypassMagic()?false:(this.isFire != null && this.isFire.booleanValue() != damageSource.isFire()?false:(this.isMagic != null && this.isMagic.booleanValue() != damageSource.isMagic()?false:(this.isLightning != null && this.isLightning.booleanValue() != (damageSource == DamageSource.LIGHTNING_BOLT)?false:(!this.directEntity.matches(serverLevel, vec3, damageSource.getDirectEntity())?false:this.sourceEntity.matches(serverLevel, vec3, damageSource.getEntity()))))))))));
   }

   public static DamageSourcePredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "damage type");
         Boolean var2 = getOptionalBoolean(var1, "is_projectile");
         Boolean var3 = getOptionalBoolean(var1, "is_explosion");
         Boolean var4 = getOptionalBoolean(var1, "bypasses_armor");
         Boolean var5 = getOptionalBoolean(var1, "bypasses_invulnerability");
         Boolean var6 = getOptionalBoolean(var1, "bypasses_magic");
         Boolean var7 = getOptionalBoolean(var1, "is_fire");
         Boolean var8 = getOptionalBoolean(var1, "is_magic");
         Boolean var9 = getOptionalBoolean(var1, "is_lightning");
         EntityPredicate var10 = EntityPredicate.fromJson(var1.get("direct_entity"));
         EntityPredicate var11 = EntityPredicate.fromJson(var1.get("source_entity"));
         return new DamageSourcePredicate(var2, var3, var4, var5, var6, var7, var8, var9, var10, var11);
      } else {
         return ANY;
      }
   }

   @Nullable
   private static Boolean getOptionalBoolean(JsonObject jsonObject, String string) {
      return jsonObject.has(string)?Boolean.valueOf(GsonHelper.getAsBoolean(jsonObject, string)):null;
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         this.addOptionally(var1, "is_projectile", this.isProjectile);
         this.addOptionally(var1, "is_explosion", this.isExplosion);
         this.addOptionally(var1, "bypasses_armor", this.bypassesArmor);
         this.addOptionally(var1, "bypasses_invulnerability", this.bypassesInvulnerability);
         this.addOptionally(var1, "bypasses_magic", this.bypassesMagic);
         this.addOptionally(var1, "is_fire", this.isFire);
         this.addOptionally(var1, "is_magic", this.isMagic);
         this.addOptionally(var1, "is_lightning", this.isLightning);
         var1.add("direct_entity", this.directEntity.serializeToJson());
         var1.add("source_entity", this.sourceEntity.serializeToJson());
         return var1;
      }
   }

   private void addOptionally(JsonObject jsonObject, String string, @Nullable Boolean boolean) {
      if(boolean != null) {
         jsonObject.addProperty(string, boolean);
      }

   }

   public static class Builder {
      private Boolean isProjectile;
      private Boolean isExplosion;
      private Boolean bypassesArmor;
      private Boolean bypassesInvulnerability;
      private Boolean bypassesMagic;
      private Boolean isFire;
      private Boolean isMagic;
      private Boolean isLightning;
      private EntityPredicate directEntity = EntityPredicate.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;

      public static DamageSourcePredicate.Builder damageType() {
         return new DamageSourcePredicate.Builder();
      }

      public DamageSourcePredicate.Builder isProjectile(Boolean isProjectile) {
         this.isProjectile = isProjectile;
         return this;
      }

      public DamageSourcePredicate.Builder isLightning(Boolean isLightning) {
         this.isLightning = isLightning;
         return this;
      }

      public DamageSourcePredicate.Builder direct(EntityPredicate.Builder entityPredicate$Builder) {
         this.directEntity = entityPredicate$Builder.build();
         return this;
      }

      public DamageSourcePredicate build() {
         return new DamageSourcePredicate(this.isProjectile, this.isExplosion, this.bypassesArmor, this.bypassesInvulnerability, this.bypassesMagic, this.isFire, this.isMagic, this.isLightning, this.directEntity, this.sourceEntity);
      }
   }
}
