package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.util.datafix.fixes.References;

public class ItemBannerColorFix extends DataFix {
   public ItemBannerColorFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> var3 = var1.findField("tag");
      OpticFinder<?> var4 = var3.type().findField("BlockEntityTag");
      return this.fixTypeEverywhereTyped("ItemBannerColorFix", var1, (var3x) -> {
         Optional<Pair<String, String>> var4 = var3x.getOptional(var2);
         if(var4.isPresent() && Objects.equals(((Pair)var4.get()).getSecond(), "minecraft:banner")) {
            Dynamic<?> var5 = (Dynamic)var3x.get(DSL.remainderFinder());
            Optional<? extends Typed<?>> var6 = var3x.getOptionalTyped(var3);
            if(var6.isPresent()) {
               Typed<?> var7 = (Typed)var6.get();
               Optional<? extends Typed<?>> var8 = var7.getOptionalTyped(var4);
               if(var8.isPresent()) {
                  Typed<?> var9 = (Typed)var8.get();
                  Dynamic<?> var10 = (Dynamic)var7.get(DSL.remainderFinder());
                  Dynamic<?> var11 = (Dynamic)var9.getOrCreate(DSL.remainderFinder());
                  if(var11.get("Base").asNumber().isPresent()) {
                     var5 = var5.set("Damage", var5.createShort((short)(var11.get("Base").asInt(0) & 15)));
                     Optional<? extends Dynamic<?>> var12 = var10.get("display").get();
                     if(var12.isPresent()) {
                        Dynamic<?> var13 = (Dynamic)var12.get();
                        if(Objects.equals(var13, var13.emptyMap().merge(var13.createString("Lore"), var13.createList(Stream.of(var13.createString("(+NBT")))))) {
                           return var3x.set(DSL.remainderFinder(), var5);
                        }
                     }

                     var11.remove("Base");
                     return var3x.set(DSL.remainderFinder(), var5).set(var3, var7.set(var4, var9.set(DSL.remainderFinder(), var11)));
                  }
               }
            }

            return var3x.set(DSL.remainderFinder(), var5);
         } else {
            return var3x;
         }
      });
   }
}
