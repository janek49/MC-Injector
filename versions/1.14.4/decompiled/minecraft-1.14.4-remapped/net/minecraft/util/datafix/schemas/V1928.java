package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1928 extends NamespacedSchema {
   public V1928(int var1, Schema schema) {
      super(var1, schema);
   }

   protected static TypeTemplate equipment(Schema schema) {
      return DSL.optionalFields("ArmorItems", DSL.list(References.ITEM_STACK.in(schema)), "HandItems", DSL.list(References.ITEM_STACK.in(schema)));
   }

   protected static void registerMob(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return equipment(schema);
      });
   }

   public Map registerEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
      map.remove("minecraft:illager_beast");
      registerMob(schema, map, "minecraft:ravager");
      return map;
   }
}
