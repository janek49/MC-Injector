package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class SwimStatsRenameFix extends DataFix {
   public SwimStatsRenameFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> var1 = this.getOutputSchema().getType(References.STATS);
      Type<?> var2 = this.getInputSchema().getType(References.STATS);
      OpticFinder<?> var3 = var2.findField("stats");
      OpticFinder<?> var4 = var3.type().findField("minecraft:custom");
      OpticFinder<String> var5 = DSL.namespacedString().finder();
      return this.fixTypeEverywhereTyped("SwimStatsRenameFix", var2, var1, (var3x) -> {
         return var3x.updateTyped(var3, (var2) -> {
            return var2.updateTyped(var4, (var1) -> {
               return var1.update(var5, (string) -> {
                  return string.equals("minecraft:swim_one_cm")?"minecraft:walk_on_water_one_cm":(string.equals("minecraft:dive_one_cm")?"minecraft:walk_under_water_one_cm":string);
               });
            });
         });
      });
   }
}
