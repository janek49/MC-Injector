package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;
import net.minecraft.util.datafix.schemas.V100;

public class V1800 extends NamespacedSchema {
   public V1800(int var1, Schema schema) {
      super(var1, schema);
   }

   protected static void registerMob(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return V100.equipment(schema);
      });
   }

   public Map registerEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
      registerMob(schema, map, "minecraft:panda");
      schema.register(map, "minecraft:pillager", (string) -> {
         return DSL.optionalFields("Inventory", DSL.list(References.ITEM_STACK.in(schema)), V100.equipment(schema));
      });
      return map;
   }
}
