package net.minecraft.world.level.block.state;

import com.google.common.collect.ImmutableMap;
import java.util.Optional;
import net.minecraft.world.level.block.state.properties.Property;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface StateHolder {
   Logger LOGGER = LogManager.getLogger();

   Comparable getValue(Property var1);

   Object setValue(Property var1, Comparable var2);

   ImmutableMap getValues();

   static default String getName(Property property, Comparable comparable) {
      return property.getName(comparable);
   }

   static default StateHolder setValueHelper(StateHolder var0, Property property, String var2, String var3, String var4) {
      Optional<T> var5 = property.getValue(var4);
      if(var5.isPresent()) {
         return (StateHolder)var0.setValue(property, (Comparable)var5.get());
      } else {
         LOGGER.warn("Unable to read property: {} with value: {} for input: {}", var2, var4, var3);
         return var0;
      }
   }
}
