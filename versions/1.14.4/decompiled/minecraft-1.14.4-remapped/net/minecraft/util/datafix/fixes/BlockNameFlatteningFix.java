package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import java.util.Objects;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.BlockStateData;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.schemas.NamespacedSchema;

public class BlockNameFlatteningFix extends DataFix {
   public BlockNameFlatteningFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.BLOCK_NAME);
      Type<?> var2 = this.getOutputSchema().getType(References.BLOCK_NAME);
      Type<Pair<String, Either<Integer, String>>> var3 = DSL.named(References.BLOCK_NAME.typeName(), DSL.or(DSL.intType(), DSL.namespacedString()));
      Type<Pair<String, String>> var4 = DSL.named(References.BLOCK_NAME.typeName(), DSL.namespacedString());
      if(Objects.equals(var1, var3) && Objects.equals(var2, var4)) {
         return this.fixTypeEverywhere("BlockNameFlatteningFix", var3, var4, (dynamicOps) -> {
            return (pair) -> {
               return pair.mapSecond((either) -> {
                  return (String)either.map(BlockStateData::upgradeBlock, (string) -> {
                     return BlockStateData.upgradeBlock(NamespacedSchema.ensureNamespaced(string));
                  });
               });
            };
         });
      } else {
         throw new IllegalStateException("Expected and actual types don\'t match.");
      }
   }
}
