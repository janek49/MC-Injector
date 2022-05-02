package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.CompoundList.CompoundListType;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class NewVillageFix extends DataFix {
   public NewVillageFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      CompoundListType<String, ?> var1 = DSL.compoundList(DSL.string(), this.getInputSchema().getType(References.STRUCTURE_FEATURE));
      OpticFinder<? extends List<? extends Pair<String, ?>>> var2 = var1.finder();
      return this.cap(var1);
   }

   private TypeRewriteRule cap(CompoundListType compoundList$CompoundListType) {
      Type<?> var2 = this.getInputSchema().getType(References.CHUNK);
      Type<?> var3 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
      OpticFinder<?> var4 = var2.findField("Level");
      OpticFinder<?> var5 = var4.type().findField("Structures");
      OpticFinder<?> var6 = var5.type().findField("Starts");
      OpticFinder<List<Pair<String, SF>>> var7 = compoundList$CompoundListType.finder();
      return TypeRewriteRule.seq(this.fixTypeEverywhereTyped("NewVillageFix", var2, (var4x) -> {
         return var4x.updateTyped(var4, (var3) -> {
            return var3.updateTyped(var5, (var2) -> {
               return var2.updateTyped(var6, (var1) -> {
                  return var1.update(var7, (list) -> {
                     return (List)list.stream().filter((pair) -> {
                        return !Objects.equals(pair.getFirst(), "Village");
                     }).map((pair) -> {
                        return pair.mapFirst((string) -> {
                           return string.equals("New_Village")?"Village":string;
                        });
                     }).collect(Collectors.toList());
                  });
               }).update(DSL.remainderFinder(), (dynamic) -> {
                  return dynamic.update("References", (dynamic) -> {
                     Optional<? extends Dynamic<?>> var1 = dynamic.get("New_Village").get();
                     return ((Dynamic)DataFixUtils.orElse(var1.map((var1) -> {
                        return dynamic.remove("New_Village").merge(dynamic.createString("Village"), var1);
                     }), dynamic)).remove("Village");
                  });
               });
            });
         });
      }), this.fixTypeEverywhereTyped("NewVillageStartFix", var3, (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            return dynamic.update("id", (dynamic) -> {
               return Objects.equals(NamespacedSchema.ensureNamespaced(dynamic.asString("")), "minecraft:new_village")?dynamic.createString("minecraft:village"):dynamic;
            });
         });
      }));
   }
}
