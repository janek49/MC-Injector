package net.minecraft.util.datafix.fixes;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class ReorganizePoi extends DataFix {
   public ReorganizePoi(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<Pair<String, Dynamic<?>>> var1 = DSL.named(References.POI_CHUNK.typeName(), DSL.remainderType());
      if(!Objects.equals(var1, this.getInputSchema().getType(References.POI_CHUNK))) {
         throw new IllegalStateException("Poi type is not what was expected.");
      } else {
         return this.fixTypeEverywhere("POI reorganization", var1, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond(ReorganizePoi::cap);
            };
         });
      }
   }

   private static Dynamic cap(Dynamic dynamic) {
      Map<Dynamic<T>, Dynamic<T>> var1 = Maps.newHashMap();

      for(int var2 = 0; var2 < 16; ++var2) {
         String var3 = String.valueOf(var2);
         Optional<Dynamic<T>> var4 = dynamic.get(var3).get();
         if(var4.isPresent()) {
            Dynamic<T> var5 = (Dynamic)var4.get();
            Dynamic<T> var6 = dynamic.createMap(ImmutableMap.of(dynamic.createString("Records"), var5));
            var1.put(dynamic.createInt(var2), var6);
            dynamic = dynamic.remove(var3);
         }
      }

      return dynamic.set("Sections", dynamic.createMap(var1));
   }
}
