package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class ChunkLightRemoveFix extends DataFix {
   public ChunkLightRemoveFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.CHUNK);
      Type<?> var2 = var1.findFieldType("Level");
      OpticFinder<?> var3 = DSL.fieldFinder("Level", var2);
      return this.fixTypeEverywhereTyped("ChunkLightRemoveFix", var1, this.getOutputSchema().getType(References.CHUNK), (var1) -> {
         return var1.updateTyped(var3, (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
               return dynamic.remove("isLightOn");
            });
         });
      });
   }
}
