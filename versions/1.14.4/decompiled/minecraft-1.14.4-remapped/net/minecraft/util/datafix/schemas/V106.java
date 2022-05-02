package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V106 extends Schema {
   public V106(int var1, Schema schema) {
      super(var1, schema);
   }

   public void registerTypes(Schema schema, Map var2, Map var3) {
      super.registerTypes(schema, var2, var3);
      schema.registerType(true, References.UNTAGGED_SPAWNER, () -> {
         return DSL.optionalFields("SpawnPotentials", DSL.list(DSL.fields("Entity", References.ENTITY_TREE.in(schema))), "SpawnData", References.ENTITY_TREE.in(schema));
      });
   }
}
