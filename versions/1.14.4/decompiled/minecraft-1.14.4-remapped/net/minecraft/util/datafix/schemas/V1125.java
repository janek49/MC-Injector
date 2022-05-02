package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1125 extends NamespacedSchema {
   public V1125(int var1, Schema schema) {
      super(var1, schema);
   }

   public Map registerBlockEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerBlockEntities(schema);
      schema.registerSimple(map, "minecraft:bed");
      return map;
   }

   public void registerTypes(Schema schema, Map var2, Map var3) {
      super.registerTypes(schema, var2, var3);
      schema.registerType(false, References.ADVANCEMENTS, () -> {
         return DSL.optionalFields("minecraft:adventure/adventuring_time", DSL.optionalFields("criteria", DSL.compoundList(References.BIOME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_a_mob", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:adventure/kill_all_mobs", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))), "minecraft:husbandry/bred_all_animals", DSL.optionalFields("criteria", DSL.compoundList(References.ENTITY_NAME.in(schema), DSL.constType(DSL.string()))));
      });
      schema.registerType(false, References.BIOME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
      schema.registerType(false, References.ENTITY_NAME, () -> {
         return DSL.constType(DSL.namespacedString());
      });
   }
}
