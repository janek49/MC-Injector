package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.DSL.TypeReference;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;

public abstract class NamedEntityFix extends DataFix {
   private final String name;
   private final String entityName;
   private final TypeReference type;

   public NamedEntityFix(Schema schema, boolean var2, String name, TypeReference type, String entityName) {
      super(schema, var2);
      this.name = name;
      this.type = type;
      this.entityName = entityName;
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<?> var1 = DSL.namedChoice(this.entityName, this.getInputSchema().getChoiceType(this.type, this.entityName));
      return this.fixTypeEverywhereTyped(this.name, this.getInputSchema().getType(this.type), this.getOutputSchema().getType(this.type), (var2) -> {
         return var2.updateTyped(var1, this.getOutputSchema().getChoiceType(this.type, this.entityName), this::fix);
      });
   }

   protected abstract Typed fix(Typed var1);
}
