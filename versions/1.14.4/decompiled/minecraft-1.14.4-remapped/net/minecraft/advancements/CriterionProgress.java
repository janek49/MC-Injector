package net.minecraft.advancements;

import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import net.minecraft.network.FriendlyByteBuf;

public class CriterionProgress {
   private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z");
   private Date obtained;

   public boolean isDone() {
      return this.obtained != null;
   }

   public void grant() {
      this.obtained = new Date();
   }

   public void revoke() {
      this.obtained = null;
   }

   public Date getObtained() {
      return this.obtained;
   }

   public String toString() {
      return "CriterionProgress{obtained=" + (this.obtained == null?"false":this.obtained) + '}';
   }

   public void serializeToNetwork(FriendlyByteBuf friendlyByteBuf) {
      friendlyByteBuf.writeBoolean(this.obtained != null);
      if(this.obtained != null) {
         friendlyByteBuf.writeDate(this.obtained);
      }

   }

   public JsonElement serializeToJson() {
      return (JsonElement)(this.obtained != null?new JsonPrimitive(DATE_FORMAT.format(this.obtained)):JsonNull.INSTANCE);
   }

   public static CriterionProgress fromNetwork(FriendlyByteBuf network) {
      CriterionProgress criterionProgress = new CriterionProgress();
      if(network.readBoolean()) {
         criterionProgress.obtained = network.readDate();
      }

      return criterionProgress;
   }

   public static CriterionProgress fromJson(String json) {
      CriterionProgress criterionProgress = new CriterionProgress();

      try {
         criterionProgress.obtained = DATE_FORMAT.parse(json);
         return criterionProgress;
      } catch (ParseException var3) {
         throw new JsonSyntaxException("Invalid datetime: " + json, var3);
      }
   }
}
