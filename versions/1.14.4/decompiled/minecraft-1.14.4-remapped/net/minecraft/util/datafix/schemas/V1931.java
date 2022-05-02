package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V100;

public class V1931 extends NamespacedSchema {
   public V1931(int var1, Schema schema) {
      super(var1, schema);
   }

   protected static void registerMob(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return V100.equipment(schema);
      });
   }

   public Map registerEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
      registerMob(schema, map, "minecraft:fox");
      return map;
   }
}
