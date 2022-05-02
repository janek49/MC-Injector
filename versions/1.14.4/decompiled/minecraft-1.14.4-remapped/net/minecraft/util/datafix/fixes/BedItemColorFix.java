package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class BedItemColorFix extends DataFix {
   public BedItemColorFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<Pair<String, String>> var1 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), DSL.namespacedString()));
      return this.fixTypeEverywhereTyped("BedItemColorFix", this.getInputSchema().getType(References.ITEM_STACK), (var1x) -> {
         Optional<Pair<String, String>> var2 = var1x.getOptional(var1);
         if(var2.isPresent() && Objects.equals(((Pair)var2.get()).getSecond(), "minecraft:bed")) {
            Dynamic<?> var3 = (Dynamic)var1x.get(DSL.remainderFinder());
            if(var3.get("Damage").asInt(0) == 0) {
               return var1x.set(DSL.remainderFinder(), var3.set("Damage", var3.createShort((short)14)));
            }
         }

         return var1x;
      });
   }
}
