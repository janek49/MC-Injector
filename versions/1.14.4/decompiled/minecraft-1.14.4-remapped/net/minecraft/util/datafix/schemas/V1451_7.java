package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class V1451_7 extends NamespacedSchema {
   public V1451_7(int var1, Schema schema) {
      super(var1, schema);
   }

   public void registerTypes(Schema schema, Map var2, Map var3) {
      super.registerTypes(schema, var2, var3);
      schema.registerType(false, References.STRUCTURE_FEATURE, () -> {
         return DSL.optionalFields("Children", DSL.list(DSL.optionalFields("CA", References.BLOCK_STATE.in(schema), "CB", References.BLOCK_STATE.in(schema), "CC", References.BLOCK_STATE.in(schema), "CD", References.BLOCK_STATE.in(schema))));
      });
   }
}
