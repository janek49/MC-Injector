package net.minecraft.stats;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntMaps;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.world.entity.player.Player;

public class StatsCounter {
   protected final Object2IntMap stats = Object2IntMaps.synchronize(new Object2IntOpenHashMap());

   public StatsCounter() {
      this.stats.defaultReturnValue(0);
   }

   public void increment(Player player, Stat stat, int var3) {
      this.setValue(player, stat, this.getValue(stat) + var3);
   }

   public void setValue(Player player, Stat stat, int var3) {
      this.stats.put(stat, var3);
   }

   public int getValue(StatType statType, Object object) {
      return statType.contains(object)?this.getValue(statType.get(object)):0;
   }

   public int getValue(Stat stat) {
      return this.stats.getInt(stat);
   }
}
