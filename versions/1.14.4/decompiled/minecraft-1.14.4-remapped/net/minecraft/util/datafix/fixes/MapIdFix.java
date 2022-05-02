package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class MapIdFix extends DataFix {
   public MapIdFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.SAVED_DATA);
      OpticFinder<?> var2 = var1.findField("data");
      return this.fixTypeEverywhereTyped("Map id fix", var1, (var1) -> {
         Optional<? extends Typed<?>> var2 = var1.getOptionalTyped(var2);
         return var2.isPresent()?var1:var1.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.emptyMap().merge(dynamic.createString("data"), dynamic);
         });
      });
   }
}
