package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1451_6 extends NamespacedSchema {
   public V1451_6(int var1, Schema schema) {
      super(var1, schema);
   }

   public void registerTypes(Schema schema, Map var2, Map var3) {
      super.registerTypes(schema, var2, var3);
      Supplier<TypeTemplate> var4 = () -> {
         return DSL.compoundList(References.ITEM_NAME.in(schema), DSL.constType(DSL.intType()));
      };
      schema.registerType(false, References.STATS, () -> {
         return DSL.optionalFields("stats", DSL.optionalFields("minecraft:mined", DSL.compoundList(References.BLOCK_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:crafted", (TypeTemplate)var4.get(), "minecraft:used", (TypeTemplate)var4.get(), "minecraft:broken", (TypeTemplate)var4.get(), "minecraft:picked_up", (TypeTemplate)var4.get(), DSL.optionalFields("minecraft:dropped", (TypeTemplate)var4.get(), "minecraft:killed", DSL.compoundList(References.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:killed_by", DSL.compoundList(References.ENTITY_NAME.in(schema), DSL.constType(DSL.intType())), "minecraft:custom", DSL.compoundList(DSL.constType(DSL.namespacedString()), DSL.constType(DSL.intType())))));
      });
   }
}
