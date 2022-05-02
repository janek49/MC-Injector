package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.datafix.fixes.References;

public class ItemCustomNameToComponentFix extends DataFix {
   public ItemCustomNameToComponentFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   private Dynamic fixTag(Dynamic dynamic) {
      Optional<? extends Dynamic<?>> var2 = dynamic.get("display").get();
      if(var2.isPresent()) {
         Dynamic<?> var3 = (Dynamic)var2.get();
         Optional<String> var4 = var3.get("Name").asString();
         if(var4.isPresent()) {
            var3 = var3.set("Name", var3.createString(Component.Serializer.toJson(new TextComponent((String)var4.get()))));
         } else {
            Optional<String> var5 = var3.get("LocName").asString();
            if(var5.isPresent()) {
               var3 = var3.set("Name", var3.createString(Component.Serializer.toJson(new TranslatableComponent((String)var5.get(), new Object[0]))));
               var3 = var3.remove("LocName");
            }
         }

         return dynamic.set("display", var3);
      } else {
         return dynamic;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<?> var2 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("ItemCustomNameToComponentFix", var1, (var2x) -> {
         return var2x.updateTyped(var2, (typed) -> {
            return typed.update(DSL.remainderFinder(), this::fixTag);
         });
      });
   }
}
