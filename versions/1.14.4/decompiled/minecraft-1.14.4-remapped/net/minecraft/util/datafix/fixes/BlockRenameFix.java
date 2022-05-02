package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.References;

public abstract class BlockRenameFix extends DataFix {
   private final String name;

   public BlockRenameFix(Schema schema, String name) {
      super(schema, false);
      this.name = name;
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.BLOCK_NAME);
      Type<Pair<String, String>> var2 = DSL.named(References.BLOCK_NAME.typeName(), DSL.namespacedString());
      if(!Objects.equals(var1, var2)) {
         throw new IllegalStateException("block type is not what was expected.");
      } else {
         TypeRewriteRule var3 = this.fixTypeEverywhere(this.name + " for block", var2, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond(this::fixBlock);
            };
         });
         TypeRewriteRule var4 = this.fixTypeEverywhereTyped(this.name + " for block_state", this.getInputSchema().getType(References.BLOCK_STATE), (typed) -> {
            return typed.update(DSL.remainderFinder(), (dynamic) -> {
               Optional<String> var2 = dynamic.get("Name").asString();
               return var2.isPresent()?dynamic.set("Name", dynamic.createString(this.fixBlock((String)var2.get()))):dynamic;
            });
         });
         return TypeRewriteRule.seq(var3, var4);
      }
   }

   protected abstract String fixBlock(String var1);

   public static DataFix create(final Schema schema, final String string, final Function function) {
      return new BlockRenameFix(schema, string) {
         protected String fixBlock(String string) {
            return (String)function.apply(string);
         }
      };
   }
}
