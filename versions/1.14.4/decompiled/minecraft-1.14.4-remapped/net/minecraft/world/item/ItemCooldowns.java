package net.minecraft.world.item;

import com.google.common.collect.Maps;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;

public class ItemCooldowns {
   private final Map cooldowns = Maps.newHashMap();
   private int tickCount;

   public boolean isOnCooldown(Item item) {
      return this.getCooldownPercent(item, 0.0F) > 0.0F;
   }

   public float getCooldownPercent(Item item, float var2) {
      ItemCooldowns.CooldownInstance var3 = (ItemCooldowns.CooldownInstance)this.cooldowns.get(item);
      if(var3 != null) {
         float var4 = (float)(var3.endTime - var3.startTime);
         float var5 = (float)var3.endTime - ((float)this.tickCount + var2);
         return Mth.clamp(var5 / var4, 0.0F, 1.0F);
      } else {
         return 0.0F;
      }
   }

   public void tick() {
      ++this.tickCount;
      if(!this.cooldowns.isEmpty()) {
         Iterator<Entry<Item, ItemCooldowns.CooldownInstance>> var1 = this.cooldowns.entrySet().iterator();

         while(var1.hasNext()) {
            Entry<Item, ItemCooldowns.CooldownInstance> var2 = (Entry)var1.next();
            if(((ItemCooldowns.CooldownInstance)var2.getValue()).endTime <= this.tickCount) {
               var1.remove();
               this.onCooldownEnded((Item)var2.getKey());
            }
         }
      }

   }

   public void addCooldown(Item item, int var2) {
      this.cooldowns.put(item, new ItemCooldowns.CooldownInstance(this.tickCount, this.tickCount + var2));
      this.onCooldownStarted(item, var2);
   }

   public void removeCooldown(Item item) {
      this.cooldowns.remove(item);
      this.onCooldownEnded(item);
   }

   protected void onCooldownStarted(Item item, int var2) {
   }

   protected void onCooldownEnded(Item item) {
   }

   class CooldownInstance {
      private final int startTime;
      private final int endTime;

      private CooldownInstance(int startTime, int endTime) {
         this.startTime = startTime;
         this.endTime = endTime;
      }
   }
}
