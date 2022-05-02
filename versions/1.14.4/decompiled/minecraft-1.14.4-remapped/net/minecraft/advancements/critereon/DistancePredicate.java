package net.minecraft.advancements.critereon;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;

public class DistancePredicate {
   public static final DistancePredicate ANY = new DistancePredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY);
   private final MinMaxBounds.Floats x;
   private final MinMaxBounds.Floats y;
   private final MinMaxBounds.Floats z;
   private final MinMaxBounds.Floats horizontal;
   private final MinMaxBounds.Floats absolute;

   public DistancePredicate(MinMaxBounds.Floats x, MinMaxBounds.Floats y, MinMaxBounds.Floats z, MinMaxBounds.Floats horizontal, MinMaxBounds.Floats absolute) {
      this.x = x;
      this.y = y;
      this.z = z;
      this.horizontal = horizontal;
      this.absolute = absolute;
   }

   public static DistancePredicate horizontal(MinMaxBounds.Floats minMaxBounds$Floats) {
      return new DistancePredicate(MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, minMaxBounds$Floats, MinMaxBounds.Floats.ANY);
   }

   public static DistancePredicate vertical(MinMaxBounds.Floats minMaxBounds$Floats) {
      return new DistancePredicate(MinMaxBounds.Floats.ANY, minMaxBounds$Floats, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY, MinMaxBounds.Floats.ANY);
   }

   public boolean matches(double var1, double var3, double var5, double var7, double var9, double var11) {
      float var13 = (float)(var1 - var7);
      float var14 = (float)(var3 - var9);
      float var15 = (float)(var5 - var11);
      return this.x.matches(Mth.abs(var13)) && this.y.matches(Mth.abs(var14)) && this.z.matches(Mth.abs(var15))?(!this.horizontal.matchesSqr((double)(var13 * var13 + var15 * var15))?false:this.absolute.matchesSqr((double)(var13 * var13 + var14 * var14 + var15 * var15))):false;
   }

   public static DistancePredicate fromJson(@Nullable JsonElement json) {
      if(json != null && !json.isJsonNull()) {
         JsonObject var1 = GsonHelper.convertToJsonObject(json, "distance");
         MinMaxBounds.Floats var2 = MinMaxBounds.Floats.fromJson(var1.get("x"));
         MinMaxBounds.Floats var3 = MinMaxBounds.Floats.fromJson(var1.get("y"));
         MinMaxBounds.Floats var4 = MinMaxBounds.Floats.fromJson(var1.get("z"));
         MinMaxBounds.Floats var5 = MinMaxBounds.Floats.fromJson(var1.get("horizontal"));
         MinMaxBounds.Floats var6 = MinMaxBounds.Floats.fromJson(var1.get("absolute"));
         return new DistancePredicate(var2, var3, var4, var5, var6);
      } else {
         return ANY;
      }
   }

   public JsonElement serializeToJson() {
      if(this == ANY) {
         return JsonNull.INSTANCE;
      } else {
         JsonObject var1 = new JsonObject();
         var1.add("x", this.x.serializeToJson());
         var1.add("y", this.y.serializeToJson());
         var1.add("z", this.z.serializeToJson());
         var1.add("horizontal", this.horizontal.serializeToJson());
         var1.add("absolute", this.absolute.serializeToJson());
         return var1;
      }
   }
}
