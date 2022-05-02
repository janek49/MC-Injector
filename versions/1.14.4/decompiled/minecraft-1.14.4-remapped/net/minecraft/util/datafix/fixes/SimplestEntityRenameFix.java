package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.TaggedChoice.TaggedChoiceType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public abstract class SimplestEntityRenameFix extends DataFix {
   private final String name;

   public SimplestEntityRenameFix(String name, Schema schema, boolean var3) {
      super(schema, var3);
      this.name = name;
   }

   public TypeRewriteRule makeRule() {
      TaggedChoiceType<String> var1 = this.getInputSchema().findChoiceType(References.ENTITY);
      TaggedChoiceType<String> var2 = this.getOutputSchema().findChoiceType(References.ENTITY);
      Type<Pair<String, String>> var3 = DSL.named(References.ENTITY_NAME.typeName(), DSL.namespacedString());
      if(!Objects.equals(this.getOutputSchema().getType(References.ENTITY_NAME), var3)) {
         throw new IllegalStateException("Entity name type is not what was expected.");
      } else {
         return TypeRewriteRule.seq(this.fixTypeEverywhere(this.name, var1, var2, (dynamicOps) -> {
            return (var3) -> {
               return var3.mapFirst((var3) -> {
                  String var4 = this.rename(var3);
                  Type<?> var5 = (Type)var1.types().get(var3);
                  Type<?> var6 = (Type)var2.types().get(var4);
                  if(!var6.equals(var5, true, true)) {
                     throw new IllegalStateException(String.format("Dynamic type check failed: %s not equal to %s", new Object[]{var6, var5}));
                  } else {
                     return var4;
                  }
               });
            };
         }), this.fixTypeEverywhere(this.name + " for entity name", var3, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond(this::rename);
            };
         }));
      }
   }

   protected abstract String rename(String var1);
}
