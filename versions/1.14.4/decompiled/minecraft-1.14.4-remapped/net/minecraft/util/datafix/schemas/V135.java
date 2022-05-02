package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.schemas.Schema;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.util.datafix.fixes.References;

public class V135 extends Schema {
   public V135(int var1, Schema schema) {
      super(var1, schema);
   }

   public void registerTypes(Schema schema, Map var2, Map var3) {
      super.registerTypes(schema, var2, var3);
      schema.registerType(false, References.PLAYER, () -> {
         return DSL.optionalFields("RootVehicle", DSL.optionalFields("Entity", References.ENTITY_TREE.in(schema)), "Inventory", DSL.list(References.ITEM_STACK.in(schema)), "EnderItems", DSL.list(References.ITEM_STACK.in(schema)));
      });
      schema.registerType(true, References.ENTITY_TREE, () -> {
         return DSL.optionalFields("Passengers", DSL.list(References.ENTITY_TREE.in(schema)), References.ENTITY.in(schema));
      });
   }
}
