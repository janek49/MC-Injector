package net.minecraft.util.datafix.schemas;

import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.templates.TypeTemplate;
import java.util.Map;
import java.util.function.Supplier;

public class V143 extends Schema {
   public V143(int var1, Schema schema) {
      super(var1, schema);
   }

   public Map registerEntities(Schema schema) {
      Map<String, Supplier<TypeTemplate>> map = super.registerEntities(schema);
      map.remove("TippedArrow");
      return map;
   }
}
