package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.V100;

public class V703 extends Schema {
   public V703(int var1, Schema schema) {
      super(var1, schema);
   }

   public Map registerEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
      map.remove("EntityHorse");
      schema.register(map, "Horse", () -> {
         return DSL.optionalFields("ArmorItem", References.ITEM_STACK.in(schema), "SaddleItem", References.ITEM_STACK.in(schema), V100.equipment(schema));
      });
      schema.register(map, "Donkey", () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema)), "SaddleItem", References.ITEM_STACK.in(schema), V100.equipment(schema));
      });
      schema.register(map, "Mule", () -> {
         return DSL.optionalFields("Items", DSL.list(References.ITEM_STACK.in(schema)), "SaddleItem", References.ITEM_STACK.in(schema), V100.equipment(schema));
      });
      schema.register(map, "ZombieHorse", () -> {
         return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(schema), V100.equipment(schema));
      });
      schema.register(map, "SkeletonHorse", () -> {
         return DSL.optionalFields("SaddleItem", References.ITEM_STACK.in(schema), V100.equipment(schema));
      });
      return map;
   }
}
