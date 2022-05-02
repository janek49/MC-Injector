package net.minecraft.util.datafix;

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
import net.minecraft.util.datafix.fixes.References;

public class OminousBannerRenameFix extends DataFix {
   public OminousBannerRenameFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   private Dynamic fixTag(Dynamic dynamic) {
      Optional<? extends Dynamic<?>> var2 = dynamic.get("display").get();
      if(var2.isPresent()) {
         Dynamic<?> var3 = (Dynamic)var2.get();
         Optional<String> var4 = var3.get("Name").asString();
         if(var4.isPresent()) {
            String var5 = (String)var4.get();
            var5 = var5.replace("\"translate\":\"block.minecraft.illager_banner\"", "\"translate\":\"block.minecraft.ominous_banner\"");
            var3 = var3.set("Name", var3.createString(var5));
         }

         return dynamic.set("display", var3);
      } else {
         return dynamic;
      }
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.ITEM_STACK);
      OpticFinder<Pair<String, String>> var2 = DSL.fieldFinder("id", DSL.named(References.ITEM_NAME.typeName(), DSL.namespacedString()));
      OpticFinder<?> var3 = var1.findField("tag");
      return this.fixTypeEverywhereTyped("OminousBannerRenameFix", var1, (var3x) -> {
         Optional<Pair<String, String>> var4 = var3x.getOptional(var2);
         if(var4.isPresent() && Objects.equals(((Pair)var4.get()).getSecond(), "minecraft:white_banner")) {
            Optional<? extends Typed<?>> var5 = var3x.getOptionalTyped(var3);
            if(var5.isPresent()) {
               Typed<?> var6 = (Typed)var5.get();
               Dynamic<?> var7 = (Dynamic)var6.get(DSL.remainderFinder());
               return var3x.set(var3, var6.set(DSL.remainderFinder(), this.fixTag(var7)));
            }
         }

         return var3x;
      });
   }
}
