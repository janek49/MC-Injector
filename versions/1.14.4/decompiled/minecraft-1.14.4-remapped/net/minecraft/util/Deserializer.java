package net.minecraft.util;

import com.mojang.datafixers.Dynamic;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public interface Deserializer {
   Logger LOGGER = LogManager.getLogger();

   Object deserialize(Dynamic var1);

   static default Object deserialize(Dynamic dynamic, Registry registry, String string, Object var3) {
      U var4 = (Deserializer)registry.get(new ResourceLocation(dynamic.get(string).asString("")));
      V var5;
      if(var4 != null) {
         var5 = var4.deserialize(dynamic);
      } else {
         LOGGER.error("Unknown type {}, replacing with {}", dynamic.get(string).asString(""), var3);
         var5 = var3;
      }

      return var5;
   }
}
