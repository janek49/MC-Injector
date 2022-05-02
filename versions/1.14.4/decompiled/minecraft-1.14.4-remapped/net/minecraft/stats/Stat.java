package net.minecraft.stats;

import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.StatFormatter;
import net.minecraft.stats.StatType;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class Stat extends ObjectiveCriteria {
   private final StatFormatter formatter;
   private final Object value;
   private final StatType type;

   protected Stat(StatType type, Object value, StatFormatter formatter) {
      super(buildName(type, value));
      this.type = type;
      this.formatter = formatter;
      this.value = value;
   }

   public static String buildName(StatType statType, Object object) {
      return locationToKey(Registry.STAT_TYPE.getKey(statType)) + ":" + locationToKey(statType.getRegistry().getKey(object));
   }

   private static String locationToKey(@Nullable ResourceLocation resourceLocation) {
      return resourceLocation.toString().replace(':', '.');
   }

   public StatType getType() {
      return this.type;
   }

   public Object getValue() {
      return this.value;
   }

   public String format(int i) {
      return this.formatter.format(i);
   }

   public boolean equals(Object object) {
      return this == object || object instanceof Stat && Objects.equals(this.getName(), ((Stat)object).getName());
   }

   public int hashCode() {
      return this.getName().hashCode();
   }

   public String toString() {
      return "Stat{name=" + this.getName() + ", formatter=" + this.formatter + '}';
   }
}
