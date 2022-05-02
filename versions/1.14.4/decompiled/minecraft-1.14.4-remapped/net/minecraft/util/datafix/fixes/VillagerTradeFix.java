package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.types.templates.List.ListType;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class VillagerTradeFix extends NamedEntityFix {
   public VillagerTradeFix(Schema schema, boolean var2) {
      super(schema, var2, "Villager trade fix", References.ENTITY, "minecraft:villager");
   }

   protected Typed fix(Typed typed) {
      OpticFinder<?> var2 = typed.getType().findField("Offers");
      OpticFinder<?> var3 = var2.type().findField("Recipes");
      Type<?> var4 = var3.type();
      if(!(var4 instanceof ListType)) {
         throw new IllegalStateException("Recipes are expected to be a list.");
      } else {
         ListType<?> var5 = (ListType)var4;
         Type<?> var6 = var5.getElement();
         OpticFinder<?> var7 = DSL.typeFinder(var6);
         OpticFinder<?> var8 = var6.findField("buy");
         OpticFinder<?> var9 = var6.findField("buyB");
         OpticFinder<?> var10 = var6.findField("sell");
         OpticFinder<Pair<String, String>> var11 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), DSL.namespacedString()));
         Function<Typed<?>, Typed<?>> var12 = (var2) -> {
            return this.updateItemStack(var11, var2);
         };
         return typed.updateTyped(var2, (var6) -> {
            return var6.updateTyped(var3, (var5) -> {
               return var5.updateTyped(var7, (var4) -> {
                  return var4.updateTyped(var8, var12).updateTyped(var9, var12).updateTyped(var10, var12);
               });
            });
         });
      }
   }

   private Typed updateItemStack(OpticFinder opticFinder, Typed var2) {
      return var2.update(opticFinder, (pair) -> {
         return pair.mapSecond((string) -> {
            return Objects.equals(string, "minecraft:carved_pumpkin")?"minecraft:pumpkin":string;
         });
      });
   }
}
