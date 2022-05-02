package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.DamageSourcePredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.damagesource.DamageSource;

public class DamagePredicate {
   public static final DamagePredicate ANY = DamagePredicate.Builder.damageInstance().build();
   private final MinMaxBounds.Floats dealtDamage;
   private final MinMaxBounds.Floats takenDamage;
   private final EntityPredicate sourceEntity;
   private final Boolean blocked;
   private final DamageSourcePredicate type;

   public DamagePredicate() {
      this.dealtDamage = MinMaxBounds.Floats.ANY;
      this.takenDamage = MinMaxBounds.Floats.ANY;
      this.sourceEntity = EntityPredicate.ANY;
      this.blocked = null;
      this.type = DamageSourcePredicate.ANY;
   }

   public DamagePredicate(MinMaxBounds.Floats dealtDamage, MinMaxBounds.Floats takenDamage, EntityPredicate sourceEntity, @Nullable Boolean blocked, DamageSourcePredicate type) {
      this.dealtDamage = dealtDamage;
      this.takenDamage = takenDamage;
      this.sourceEntity = sourceEntity;
      this.blocked = blocked;
      this.type = type;
   }

   public boolean matches(ServerPlayer serverPlayer, DamageSource damageSource, float var3, float var4, boolean var5) {
      return this == ANY?true:(!this.dealtDamage.matches(var3)?false:(!this.takenDamage.matches(var4)?false:(!this.sourceEntity.matches(serverPlayer, damageSource.getEntity())?false:(this.blocked != null && this.blocked.booleanValue() != var5?false:this.type.matches(serverPlayer, damageSource)))));
   }

   public static DamagePredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "damage");
         MinMaxBounds.Floats var2 = MinMaxBounds.Floats.fromJson(var1.get("dealt"));
         MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(var1.get("taken"));
         Boolean var4 = var1.has("blocked")?Boolean.valueOf(GsonHelper.getAsBoolean(var1, "blocked")):null;
         EntityPredicate var5 = EntityPredicate.fromJson(var1.get("source_entity"));
         DamageSourcePredicate var6 = DamageSourcePredicate.fromJson(var1.get("type"));
         return new DamagePredicate(var2, var3, var5, var4, var6);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("dealt", this.dealtDamage.serializeToJson());
         var1.add("taken", this.takenDamage.serializeToJson());
         var1.add("source_entity", this.sourceEntity.serializeToJson());
         var1.add("type", this.type.serializeToJson());
         if(this.blocked != null) {
            var1.addProperty("blocked", this.blocked);
         }

         return var1;
      }
   }

   public static class Builder {
      private MinMaxBounds.Floats dealtDamage = MinMaxBounds.Floats.ANY;
      private MinMaxBounds.Floats takenDamage = MinMaxBounds.Floats.ANY;
      private EntityPredicate sourceEntity = EntityPredicate.ANY;
      private Boolean blocked;
      private DamageSourcePredicate type = DamageSourcePredicate.ANY;

      public static DamagePredicate.Builder damageInstance() {
         return new DamagePredicate.Builder();
      }

      public DamagePredicate.Builder blocked(Boolean blocked) {
         this.blocked = blocked;
         return this;
      }

      public DamagePredicate.Builder type(DamageSourcePredicate.Builder damageSourcePredicate$Builder) {
         this.type = damageSourcePredicate$Builder.build();
         return this;
      }

      public DamagePredicate build() {
         return new DamagePredicate(this.dealtDamage, this.takenDamage, this.sourceEntity, this.blocked, this.type);
      }
   }
}
