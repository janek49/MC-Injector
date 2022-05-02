package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.world.scores.criteria.ObjectiveCriteria;

public class ObjectiveRenderTypeFix extends DataFix {
   public ObjectiveRenderTypeFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   private static ObjectiveCriteria.RenderType getRenderType(String string) {
      return string.equals("health")?ObjectiveCriteria.RenderType.HEARTS:ObjectiveCriteria.RenderType.INTEGER;
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> var1 = DSL.named(References.OBJECTIVE.typeName(), DSL.remainderType());
      if(!Objects.equals(var1, this.getInputSchema().getType(References.OBJECTIVE))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveRenderTypeFix", var1, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond((dynamic) -> {
                  Optional<String> var1 = dynamic.get("RenderType").asString();
                  if(!var1.isPresent()) {
                     String var2 = dynamic.get("CriteriaName").asString("");
                     ObjectiveCriteria.RenderType var3 = getRenderType(var2);
                     return dynamic.set("RenderType", dynamic.createString(var3.getId()));
                  } else {
                     return dynamic;
                  }
               });
            };
         });
      }
   }
}
