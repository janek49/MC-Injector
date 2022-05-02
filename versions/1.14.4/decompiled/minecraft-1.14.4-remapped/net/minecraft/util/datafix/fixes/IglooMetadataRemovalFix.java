package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import com.mojang.datafixers.types.Type;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;
import net.minecraft.util.datafix.fixes.References;

public class IglooMetadataRemovalFix extends DataFix {
   public IglooMetadataRemovalFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   protected TypeRewriteRule makeRule() {
      Type<?> var1 = this.getInputSchema().getType(References.STRUCTURE_FEATURE);
      Type<?> var2 = this.getOutputSchema().getType(References.STRUCTURE_FEATURE);
      return this.writeFixAndRead("IglooMetadataRemovalFix", var1, var2, IglooMetadataRemovalFix::fixTag);
   }

   private static Dynamic fixTag(Dynamic dynamic) {
      boolean var1 = ((Boolean)dynamic.get("Children").asStreamOpt().map((stream) -> {
         return Boolean.valueOf(stream.allMatch(IglooMetadataRemovalFix::isIglooPiece));
      }).orElse(Boolean.valueOf(false))).booleanValue();
      return var1?dynamic.set("id", dynamic.createString("Igloo")).remove("Children"):dynamic.update("Children", IglooMetadataRemovalFix::removeIglooPieces);
   }

   private static Dynamic removeIglooPieces(Dynamic dynamic) {
      Optional var10000 = dynamic.asStreamOpt().map((stream) -> {
         return stream.filter((dynamic) -> {
            return !isIglooPiece(dynamic);
         });
      });
      dynamic.getClass();
      return (Dynamic)var10000.map(dynamic::createList).orElse(dynamic);
   }

   private static boolean isIglooPiece(Dynamic dynamic) {
      return dynamic.get("id").asString("").equals("Iglu");
   }
}
