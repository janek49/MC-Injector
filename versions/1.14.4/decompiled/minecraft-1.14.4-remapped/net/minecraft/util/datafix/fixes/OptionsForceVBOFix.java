package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class OptionsForceVBOFix extends DataFix {
   public OptionsForceVBOFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsForceVBOFix", this.getInputSchema().getType(References.OPTIONS), (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.set("useVbo", dynamic.createString("true"));
         });
      });
   }
}
