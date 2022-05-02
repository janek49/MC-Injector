package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1920 extends NamespacedSchema {
   public V1920(int var1, Schema schema) {
      super(var1, schema);
   }

   protected static void registerInventory(Schema schema, Map map, String string) {
      schema.register(map, string, () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema)));
      });
   }

   public Map registerBlockEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
      registerInventory(schema, map, "minecraft:campfire");
      return map;
   }
}
