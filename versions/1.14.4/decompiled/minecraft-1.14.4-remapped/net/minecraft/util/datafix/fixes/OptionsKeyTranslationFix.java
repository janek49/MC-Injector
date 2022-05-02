package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.fixes.References;

public class OptionsKeyTranslationFix extends DataFix {
   public OptionsKeyTranslationFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsKeyTranslationFix", this.getInputSchema().getType(References.OPTIONS), (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return (Dynamic)dynamic.getMapValues().map((map) -> {
               return dynamic.createMap((Map)map.entrySet().stream().map((map$Entry) -> {
                  if(((Dynamic)map$Entry.getKey()).asString("").startsWith("key_")) {
                     String var2 = ((Dynamic)map$Entry.getValue()).asString("");
                     if(!var2.startsWith("key.mouse") && !var2.startsWith("scancode.")) {
                        return Pair.of(map$Entry.getKey(), dynamic.createString("key.keyboard." + var2.substring("key.".length())));
                     }
                  }

                  return Pair.of(map$Entry.getKey(), map$Entry.getValue());
               }).collect(Collectors.toMap(Pair::getFirst, Pair::getSecond)));
            }).orElse(dynamic);
         });
      });
   }
}
