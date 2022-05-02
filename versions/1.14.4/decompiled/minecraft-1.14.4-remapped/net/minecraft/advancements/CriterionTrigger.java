package net.minecraft.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;

public interface CriterionTrigger {
   ResourceLocation getId();

   void addPlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener var2);

   void removePlayerListener(PlayerAdvancements var1, CriterionTrigger.Listener var2);

   void removePlayerListeners(PlayerAdvancements var1);

   CriterionTriggerInstance createInstance(JsonObject var1, JsonDeserializationContext var2);

   public static class Listener {
      private final CriterionTriggerInstance trigger;
      private final Advancement advancement;
      private final String criterion;

      public Listener(CriterionTriggerInstance trigger, Advancement advancement, String criterion) {
         this.trigger = trigger;
         this.advancement = advancement;
         this.criterion = criterion;
      }

      public CriterionTriggerInstance getTriggerInstance() {
         return this.trigger;
      }

      public void run(PlayerAdvancements playerAdvancements) {
         playerAdvancements.award(this.advancement, this.criterion);
      }

      public boolean equals(Object object) {
         if(this == object) {
            return true;
         } else if(object != null && this.getClass() == object.getClass()) {
            CriterionTrigger.Listener<?> var2 = (CriterionTrigger.Listener)object;
            return !this.trigger.equals(var2.trigger)?false:(!this.advancement.equals(var2.advancement)?false:this.criterion.equals(var2.criterion));
         } else {
            return false;
         }
      }

      public int hashCode() {
         int var1 = this.trigger.hashCode();
         var1 = 31 * var1 + this.advancement.hashCode();
         var1 = 31 * var1 + this.criterion.hashCode();
         return var1;
      }
   }
}
