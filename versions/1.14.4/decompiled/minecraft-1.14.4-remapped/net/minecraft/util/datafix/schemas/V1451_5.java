package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1451_5 extends NamespacedSchema {
   public V1451_5(int var1, Schema schema) {
      super(var1, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
      map.remove("minecraft:flower_pot");
      map.remove("minecraft:noteblock");
      return map;
   }
}
