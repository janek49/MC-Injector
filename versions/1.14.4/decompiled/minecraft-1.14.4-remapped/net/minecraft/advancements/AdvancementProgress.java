package net.minecraft.advancements;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriterionProgress;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.util.GsonHelper;

public class AdvancementProgress implements Comparable {
   private final Map criteria = Maps.newHashMap();
   private String[][] requirements = new String[0][];

   public void update(Map map, String[][] requirements) {
      Set<String> var3 = map.keySet();
      this.criteria.entrySet().removeIf((map$Entry) -> {
         return !var3.contains(map$Entry.getKey());
      });

      for(String var5 : var3) {
         if(!this.criteria.containsKey(var5)) {
            this.criteria.put(var5, new CriterionProgress());
         }
      }

      this.requirements = requirements;
   }

   public boolean isDone() {
      if(this.requirements.length == 0) {
         return false;
      } else {
         for(String[] vars4 : this.requirements) {
            boolean var5 = false;

            for(String var9 : vars4) {
               CriterionProgress var10 = this.getCriterion(var9);
               if(var10 != null && var10.isDone()) {
                  var5 = true;
                  break;
               }
            }

            if(!var5) {
               return false;
            }
         }

         return true;
      }
   }

   public boolean hasProgress() {
      for(CriterionProgress var2 : this.criteria.values()) {
         if(var2.isDone()) {
            return true;
         }
      }

      return false;
   }

   public boolean grantProgress(String string) {
      CriterionProgress var2 = (CriterionProgress)this.criteria.get(string);
      if(var2 != null && !var2.isDone()) {
         var2.grant();
         return true;
      } else {
         return false;
      }
   }

   public boolean revokeProgress(String string) {
      CriterionProgress var2 = (CriterionProgress)this.criteria.get(string);
      if(var2 != null && var2.isDone()) {
         var2.revoke();
         return true;
      } else {
         return false;
      }
   }

   public String toString() {
      return "AdvancementProgress{criteria=" + this.criteria + ", requirements=" + Arrays.deepToString(this.requirements) + '}';
   }

   public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeVarInt(this.criteria.size());

      for(Entry<String, CriterionProgress> var3 : this.criteria.entrySet()) {
         friendlyByteBuf.writeUtf((String)var3.getKey());
         ((CriterionProgress)var3.getValue()).serializeToNetwork(friendlyByteBuf);
      }

   }

   public static AdvancementProgress fromNetwork(FriendlyByteBuf network) {
      AdvancementProgress advancementProgress = new AdvancementProgress();
      int var2 = network.readVarInt();

      for(int var3 = 0; var3 < var2; ++var3) {
         advancementProgress.criteria.put(network.readUtf(32767), CriterionProgress.fromNetwork(network));
      }

      return advancementProgress;
   }

   @Nullable
   public CriterionProgress getCriterion(String string) {
      return (CriterionProgress)this.criteria.get(string);
   }

   public float getPercent() {
      if(this.criteria.isEmpty()) {
         return 0.0F;
      } else {
         float var1 = (float)this.requirements.length;
         float var2 = (float)this.countCompletedRequirements();
         return var2 / var1;
      }
   }

   @Nullable
   public String getProgressText() {
      if(this.criteria.isEmpty()) {
         return null;
      } else {
         int var1 = this.requirements.length;
         if(var1 <= 1) {
            return null;
         } else {
            int var2 = this.countCompletedRequirements();
            return var2 + "/" + var1;
         }
      }
   }

   private int countCompletedRequirements() {
      int var1 = 0;

      for(String[] vars5 : this.requirements) {
         boolean var6 = false;

         for(String var10 : vars5) {
            CriterionProgress var11 = this.getCriterion(var10);
            if(var11 != null && var11.isDone()) {
               var6 = true;
               break;
            }
         }

         if(var6) {
            ++var1;
         }
      }

      return var1;
   }

   public Iterable getRemainingCriteria() {
      List<String> var1 = Lists.newArrayList();

      for(Entry<String, CriterionProgress> var3 : this.criteria.entrySet()) {
         if(!((CriterionProgress)var3.getValue()).isDone()) {
            var1.add(var3.getKey());
         }
      }

      return var1;
   }

   public Iterable getCompletedCriteria() {
      List<String> var1 = Lists.newArrayList();

      for(Entry<String, CriterionProgress> var3 : this.criteria.entrySet()) {
         if(((CriterionProgress)var3.getValue()).isDone()) {
            var1.add(var3.getKey());
         }
      }

      return var1;
   }

   @Nullable
   public Date getFirstProgressDate() {
      Date date = null;

      for(CriterionProgress var3 : this.criteria.values()) {
         if(var3.isDone() && (date == null || var3.getObtained().before(date))) {
            date = var3.getObtained();
         }
      }

      return date;
   }

   public int compareTo(AdvancementProgress advancementProgress) {
      Date var2 = this.getFirstProgressDate();
      Date var3 = advancementProgress.getFirstProgressDate();
      return var2 == null && var3 != null?1:(var2 != null && var3 == null?-1:(var2 == null && var3 == null?0:var2.compareTo(var3)));
   }

   // $FF: synthetic method
   public int compareTo(Object var1) {
      return this.compareTo((AdvancementProgress)var1);
   }

   public static class Serializer implements JsonDeserializer, JsonSerializer {
      public JsonElement serialize(AdvancementProgress advancementProgress, Type type, JsonSerializationContext jsonSerializationContext) {
         JsonObject var4 = new JsonObject();
         JsonObject var5 = new JsonObject();

         for(Entry<String, CriterionProgress> var7 : advancementProgress.criteria.entrySet()) {
            CriterionProgress var8 = (CriterionProgress)var7.getValue();
            if(var8.isDone()) {
               var5.add((String)var7.getKey(), var8.serializeToJson());
            }
         }

         if(!var5.entrySet().isEmpty()) {
            var4.add("criteria", var5);
         }

         var4.addProperty("done", Boolean.valueOf(advancementProgress.isDone()));
         return var4;
      }

      public AdvancementProgress deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
         JsonObject var4 = GsonHelper.convertToJsonObject(jsonElement, "advancement");
         JsonObject var5 = GsonHelper.getAsJsonObject(var4, "criteria", new JsonObject());
         AdvancementProgress var6 = new AdvancementProgress();

         for(Entry<String, JsonElement> var8 : var5.entrySet()) {
            String var9 = (String)var8.getKey();
            var6.criteria.put(var9, CriterionProgress.fromJson(GsonHelper.convertToString((JsonElement)var8.getValue(), var9)));
         }

         return var6;
      }

      // $FF: synthetic method
      public Object deserialize(JsonElement var1, Type var2, JsonDeserializationContext var3) throws JsonParseException {
         return this.deserialize(var1, var2, var3);
      }

      // $FF: synthetic method
      public JsonElement serialize(Object var1, Type var2, JsonSerializationContext var3) {
         return this.serialize((AdvancementProgress)var1, var2, var3);
      }
   }
}
