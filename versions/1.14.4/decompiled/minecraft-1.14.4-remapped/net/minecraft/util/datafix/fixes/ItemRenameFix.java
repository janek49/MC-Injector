package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public abstract class ItemRenameFix extends DataFix {
   private final String name;

   public ItemRenameFix(Schema schema, String name) {
      super(schema, false);
      this.name = name;
   }

   public TypeRewriteRule makeRule() {
      Type<Pair<String, String>> var1 = DSL.named(References.ITEM_NAME.typeName(), DSL.namespacedString());
      if(!Objects.equals(this.getInputSchema().getType(References.ITEM_NAME), var1)) {
         throw new IllegalStateException("item name type is not what was expected.");
      } else {
         return this.fixTypeEverywhere(this.name, var1, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond(this::fixItem);
            };
         });
      }
   }

   protected abstract String fixItem(String var1);

   public static DataFix create(final Schema schema, final String string, final Function function) {
      return new ItemRenameFix(schema, string) {
         protected String fixItem(String string) {
            return (String)function.apply(string);
         }
      };
   }
}
