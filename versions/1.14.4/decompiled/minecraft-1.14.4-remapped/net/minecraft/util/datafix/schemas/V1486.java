package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1486 extends NamespacedSchema {
   public V1486(int var1, Schema schema) {
      super(var1, schema);
   }

   public Map registerEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
      map.put("minecraft:cod", map.remove("minecraft:cod_mob"));
      map.put("minecraft:salmon", map.remove("minecraft:salmon_mob"));
      return map;
   }
}
