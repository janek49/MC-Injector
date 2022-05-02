package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public class OptionsLowerCaseLanguageFix extends DataFix {
   public OptionsLowerCaseLanguageFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      return this.fixTypeEverywhereTyped("OptionsLowerCaseLanguageFix", this.getInputSchema().getType(References.OPTIONS), (typed) -> {
         return typed.update(DSL.remainderFinder(), (dynamic) -> {
            Optional<String> var1 = dynamic.get("lang").asString();
            return var1.isPresent()?dynamic.set("lang", dynamic.createString(((String)var1.get()).toLowerCase(Locale.ROOT))):dynamic;
         });
      });
   }
}
