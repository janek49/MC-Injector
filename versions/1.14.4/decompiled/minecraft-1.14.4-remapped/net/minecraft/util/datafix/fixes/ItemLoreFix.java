package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.datafix.fixes.References;

public class ItemLoreFix extends DataFix {
   public ItemLoreFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<?> var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("Item Lore componentize", var1, (var1) -> {
         return var1.updateTyped(var2, (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
               return dynamic.update("display", (dynamic) -> {
                  return dynamic.update("Lore", (dynamic) -> {
                     Optional var10000 = dynamic.asStreamOpt().map(ItemLoreFix::fixLoreList);
                     dynamic.getClass();
                     return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createList), dynamic);
                  });
               });
            });
         });
      });
   }

   private static Stream fixLoreList(Stream stream) {
      return stream.map((dynamic) -> {
         Optional var10000 = dynamic.asString().map(ItemLoreFix::fixLoreEntry);
         dynamic.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createString), dynamic);
      });
   }

   private static String fixLoreEntry(String string) {
      return Component.Serializer.toJson(new TextComponent(string));
   }
}
