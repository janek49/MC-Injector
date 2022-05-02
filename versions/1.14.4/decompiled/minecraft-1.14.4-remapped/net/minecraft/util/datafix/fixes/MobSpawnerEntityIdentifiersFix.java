package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class MobSpawnerEntityIdentifiersFix extends DataFix {
   public MobSpawnerEntityIdentifiersFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   private Dynamic fix(Dynamic dynamic) {
      if(!"MobSpawner".equals(dynamic.get("id").asString(""))) {
         return dynamic;
      } else {
         Optional<String> var2 = dynamic.get("EntityId").asString();
         if(var2.isPresent()) {
            Dynamic<?> var3 = (Dynamic)DataFixUtils.orElse(dynamic.get("SpawnData").get(), dynamic.emptyMap());
            var3 = var3.set("id", var3.createString(((String)var2.get()).isEmpty()?"Pig":(String)var2.get()));
            dynamic = dynamic.set("SpawnData", var3);
            dynamic = dynamic.remove("EntityId");
         }

         Optional<? extends Stream<? extends Dynamic<?>>> var3 = dynamic.get("SpawnPotentials").asStreamOpt();
         if(var3.isPresent()) {
            dynamic = dynamic.set("SpawnPotentials", dynamic.createList(((Stream)var3.get()).map((dynamic) -> {
               Optional<String> var1 = dynamic.get("Type").asString();
               if(var1.isPresent()) {
                  Dynamic<?> var2 = ((Dynamic)DataFixUtils.orElse(dynamic.get("Properties").get(), dynamic.emptyMap())).set("id", dynamic.createString((String)var1.get()));
                  return dynamic.set("Entity", var2).remove("Type").remove("Properties");
               } else {
                  return dynamic;
               }
            })));
         }

         return dynamic;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getOutputSchema().getType(References.UNTAGGED_SPAWNER);
      return this.fixTypeEverywhereTyped("MobSpawnerEntityIdentifiersFix", this.getInputSchema().getType(References.UNTAGGED_SPAWNER), var1, (var2) -> {
         Dynamic<?> var3 = (Dynamic)var2.get(DSL.remainderFinder());
         var3 = var3.set("id", var3.createString("MobSpawner"));
         Pair<?, ? extends Optional<? extends Typed<?>>> var4 = var1.readTyped(this.fix(var3));
         return !((Optional)var4.getSecond()).isPresent()?var2:(Typed)((Optional)var4.getSecond()).get();
      });
   }
}
