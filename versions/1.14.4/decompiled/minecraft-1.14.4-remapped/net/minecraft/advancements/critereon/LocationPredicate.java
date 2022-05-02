package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;

public class LocationPredicate {
   public static final LocationPredicate ANY = new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (Biome)null, (StructureFeature)null, (DimensionType)null);
   private final MinMaxBounds.Floats x;
   private final MinMaxBounds.Floats y;
   private final MinMaxBounds.Floats z;
   @Nullable
   private final Biome biome;
   @Nullable
   private final StructureFeature feature;
   @Nullable
   private final DimensionType dimension;

   public LocationPredicate(MinMaxBounds.Floats x, MinMaxBounds.Floats y, MinMaxBounds.Floats z, @Nullable Biome biome, @Nullable StructureFeature feature, @Nullable DimensionType dimension) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.biome = biome;
      this.feature = feature;
      this.dimension = dimension;
   }

   public static LocationPredicate inBiome(Biome biome) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, biome, (StructureFeature)null, (DimensionType)null);
   }

   public static LocationPredicate inDimension(DimensionType dimensionType) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (Biome)null, (StructureFeature)null, dimensionType);
   }

   public static LocationPredicate inFeature(StructureFeature structureFeature) {
      return new LocationPredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, (Biome)null, structureFeature, (DimensionType)null);
   }

   public boolean matches(ServerLevel serverLevel, double var2, double var4, double var6) {
      return this.matches(serverLevel, (float)var2, (float)var4, (float)var6);
   }

   public boolean matches(ServerLevel serverLevel, float var2, float var3, float var4) {
      if(!this.x.matches(var2)) {
         return false;
      } else if(!this.y.matches(var3)) {
         return false;
      } else if(!this.z.matches(var4)) {
         return false;
      } else if(this.dimension != null && this.dimension != serverLevel.dimension.getType()) {
         return false;
      } else {
         BlockPos var5 = new BlockPos((double)var2, (double)var3, (double)var4);
         return this.biome != null && this.biome != serverLevel.getBiome(var5)?false:this.feature == null || this.feature.isInsideFeature(serverLevel, var5);
      }
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         if(!this.x.isAny() || !this.y.isAny() || !this.z.isAny()) {
            JsonObject var2 = new JsonObject();
            var2.add("x", this.x.serializeToJson());
            var2.add("y", this.y.serializeToJson());
            var2.add("z", this.z.serializeToJson());
            var1.add("position", var2);
         }

         if(this.dimension != null) {
            var1.addProperty("dimension", DimensionType.getName(this.dimension).toString());
         }

         if(this.feature != null) {
            var1.addProperty("feature", (String)Feature.STRUCTURES_REGISTRY.inverse().get(this.feature));
         }

         if(this.biome != null) {
            var1.addProperty("biome", Registry.BIOME.getKey(this.biome).toString());
         }

         return var1;
      }
   }

   public static LocationPredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "location");
         JsonObject var2 = GsonHelper.getAsJsonObject(var1, "position", new JsonObject());
         MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(var2.get("x"));
         MinMaxBounds.Floats var4 = MinMaxBounds.Floats.fromJson(var2.get("y"));
         MinMaxBounds.Floats var5 = MinMaxBounds.Floats.fromJson(var2.get("z"));
         DimensionType var6 = var1.has("dimension")?DimensionType.getByName(new ResourceLocation(GsonHelper.getAsString(var1, "dimension"))):null;
         StructureFeature<?> var7 = var1.has("feature")?(StructureFeature)Feature.STRUCTURES_REGISTRY.get(GsonHelper.getAsString(var1, "feature")):null;
         Biome var8 = null;
         if(var1.has("biome")) {
            ResourceLocation var9 = new ResourceLocation(GsonHelper.getAsString(var1, "biome"));
            var8 = (Biome)Registry.BIOME.getOptional(var9).orElseThrow(() -> {
               return new JsonSyntaxException("Unknown biome \'" + var9 + "\'");
            });
         }

         return new LocationPredicate(var3, var4, var5, var8, var7, var6);
      } else {
         return ANY;
      }
   }

   public static class Builder {
      private MinMaxBounds.Floats x = MinMaxBounds.Floats.ANY;
      private MinMaxBounds.Floats y = MinMaxBounds.Floats.ANY;
      private MinMaxBounds.Floats z = MinMaxBounds.Floats.ANY;
      @Nullable
      private Biome biome;
      @Nullable
      private StructureFeature feature;
      @Nullable
      private DimensionType dimension;

      public LocationPredicate.Builder setBiome(@Nullable Biome biome) {
         this.biome = biome;
         return this;
      }

      public LocationPredicate build() {
         return new LocationPredicate(this.x, this.y, this.z, this.biome, this.feature, this.dimension);
      }
   }
}
