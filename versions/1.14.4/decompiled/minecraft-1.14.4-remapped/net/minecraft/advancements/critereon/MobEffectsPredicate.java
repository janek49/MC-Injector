package net.minecraft.advancements.critereon;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.Collections;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

public class MobEffectsPredicate {
   public static final MobEffectsPredicate ANY = new MobEffectsPredicate(Collections.emptyMap());
   private final Map effects;

   public MobEffectsPredicate(Map effects) {
      this.effects = effects;
   }

   public static MobEffectsPredicate effects() {
      return new MobEffectsPredicate(Maps.newHashMap());
   }

   public MobEffectsPredicate and(MobEffect mobEffect) {
      this.effects.put(mobEffect, new MobEffectsPredicate.MobEffectInstancePredicate());
      return this;
   }

   public boolean matches(Entity entity) {
      return this == ANY?true:(entity instanceof LivingEntity?this.matches(((LivingEntity)entity).getActiveEffectsMap()):false);
   }

   public boolean matches(LivingEntity livingEntity) {
      return this == ANY?true:this.matches(livingEntity.getActiveEffectsMap());
   }

   public boolean matches(Map map) {
      if(this == ANY) {
         return true;
      } else {
         for(Entry<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> var3 : this.effects.entrySet()) {
            MobEffectInstance var4 = (MobEffectInstance)map.get(var3.getKey());
            if(!((MobEffectsPredicate.MobEffectInstancePredicate)var3.getValue()).matches(var4)) {
               return false;
            }
         }

         return true;
      }
   }

   public static MobEffectsPredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "effects");
         Map<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> var2 = Maps.newHashMap();

         for(Entry<String, JsonElement> var4 : var1.entrySet()) {
            ResourceLocation var5 = new ResourceLocation((String)var4.getKey());
            MobEffect var6 = (MobEffect)Registry.MOB_EFFECT.getOptional(var5).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown effect \'" + var5 + "\'");
            });
            MobEffectsPredicate.MobEffectInstancePredicate var7 = MobEffectsPredicate.MobEffectInstancePredicate.fromJson(GsonHelper.convertToJsonObject((JsonElement)var4.getValue(), (String)var4.getKey()));
            var2.put(var6, var7);
         }

         return new MobEffectsPredicate(var2);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();

         for(Entry<MobEffect, MobEffectsPredicate.MobEffectInstancePredicate> var3 : this.effects.entrySet()) {
            var1.add(Registry.MOB_EFFECT.getKey(var3.getKey()).toString(), ((MobEffectsPredicate.MobEffectInstancePredicate)var3.getValue()).serializeToJson());
         }

         return var1;
      }
   }

   public static class MobEffectInstancePredicate {
      private final MinMaxBounds.Ints amplifier;
      private final MinMaxBounds.Ints duration;
      @Nullable
      private final Boolean ambient;
      @Nullable
      private final Boolean visible;

      public MobEffectInstancePredicate(MinMaxBounds.Ints amplifier, MinMaxBounds.Ints duration, @Nullable Boolean ambient, @Nullable Boolean visible) {
         this.amplifier = amplifier;
         this.duration = duration;
         this.ambient = ambient;
         this.visible = visible;
      }

      public MobEffectInstancePredicate() {
         this(MinMaxBounds.Ints.ANY, MinMaxBounds.Ints.ANY, (Boolean)null, (Boolean)null);
      }

      public boolean matches(@Nullable MobEffectInstance mobEffectInstance) {
         return mobEffectInstance == null?false:(!this.amplifier.matches(mobEffectInstance.getAmplifier())?false:(!this.duration.matches(mobEffectInstance.getDuration())?false:(this.ambient != null && this.ambient.booleanValue() != mobEffectInstance.isAmbient()?false:this.visible == null || this.visible.booleanValue() == mobEffectInstance.isVisible())));
      }

      public JsonElement serializeToJson() {
         JsonObject var1 = new JsonObject();
         var1.add("amplifier", this.amplifier.serializeToJson());
         var1.add("duration", this.duration.serializeToJson());
         var1.addProperty("ambient", this.ambient);
         var1.addProperty("visible", this.visible);
         return var1;
      }

      public static MobEffectsPredicate.MobEffectInstancePredicate fromJson(JsonObject json) {
         MinMaxBounds.Ints var1 = MinMaxBounds.Ints.fromJson(json.get("amplifier"));
         MinMaxBounds.Ints var2 = MinMaxBounds.Ints.fromJson(json.get("duration"));
         Boolean var3 = json.has("ambient")?Boolean.valueOf(GsonHelper.getAsBoolean(json, "ambient")):null;
         Boolean var4 = json.has("visible")?Boolean.valueOf(GsonHelper.getAsBoolean(json, "visible")):null;
         return new MobEffectsPredicate.MobEffectInstancePredicate(var1, var2, var3, var4);
      }
   }
}
