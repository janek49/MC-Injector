package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class EntityRedundantChanceTagsFix extends DataFix {
   public EntityRedundantChanceTagsFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("EntityRedundantChanceTagsFix", this.getInputSchema().getType(References.ENTITY), (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            Dynamic<?> var1 = dynamic;
            if(Objects.equals(dynamic.get("HandDropChances"), Optional.of(dynamic.createList(Stream.generate(() -> {
               return dynamic.createFloat(0.0F);
            }).limit(2L))))) {
               dynamic = dynamic.remove("HandDropChances");
            }

            if(Objects.equals(dynamic.get("ArmorDropChances"), Optional.of(dynamic.createList(Stream.generate(() -> {
               return var1.createFloat(0.0F);
            }).limit(4L))))) {
               dynamic = dynamic.remove("ArmorDropChances");
            }

            return dynamic;
         });
      });
   }
}
