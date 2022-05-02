package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1906 extends NamespacedSchema {
   public V1906(int var1, Schema schema) {
      super(var1, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
      registerInventory(schema, map, "minecraft:barrel");
      registerInventory(schema, map, "minecraft:smoker");
      registerInventory(schema, map, "minecraft:blast_furnace");
      schema.register(map, "minecraft:lectern", (string) -> {
         return DSL.optionalFields("Book", References.ITEM_STACK.in(schema));
      });
      schema.registerSimple(map, "minecraft:bell");
      return map;
   }

   protected static void registerInventory(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema)));
      });
   }
}
