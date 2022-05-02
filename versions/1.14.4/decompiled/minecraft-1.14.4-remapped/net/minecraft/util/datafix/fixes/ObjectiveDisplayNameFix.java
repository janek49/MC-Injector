package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.datafix.fixes.References;

public class ObjectiveDisplayNameFix extends DataFix {
   public ObjectiveDisplayNameFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> var1 = DSL.named(References.OBJECTIVE.typeName(), DSL.remainderType());
      if(!Objects.equals(var1, this.getInputSchema().getType(References.OBJECTIVE))) {
         throw new IllegalStateException("Objective type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("ObjectiveDisplayNameFix", var1, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond((dynamic) -> {
                  return dynamic.update("DisplayName", (var1) -> {
                     Optional var10000 = var1.asString().map((string) -> {
                        return Component.Serializer.toJson(new TextComponent(string));
                     });
                     dynamic.getClass();
                     return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createString), var1);
                  });
               });
            };
         });
      }
   }
}
