package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class OptionsAddTextBackgroundFix extends DataFix {
   public OptionsAddTextBackgroundFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsAddTextBackgroundFix", this.getInputSchema().getType(References.OPTIONS), (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return (Dynamic)DataFixUtils.orElse(dynamic.get("chatOpacity").asString().map((string) -> {
               return dynamic.set("textBackgroundOpacity", dynamic.createDouble(this.calculateBackground(string)));
            }), dynamic);
         });
      });
   }

   private double calculateBackground(String string) {
      try {
         double var2 = 0.9D * Double.parseDouble(string) + 0.1D;
         return var2 / 2.0D;
      } catch (NumberFormatException var4) {
         return 0.5D;
      }
   }
}
