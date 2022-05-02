package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFixUtils;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityBannerColorFix extends NamedEntityFix {
   public BlockEntityBannerColorFix(Schema schema, boolean var2) {
      super(schema, var2, "BlockEntityBannerColorFix", References.BLOCK_ENTITY, "minecraft:banner");
   }

   public Dynamic fixTag(Dynamic dynamic) {
      dynamic = dynamic.update("Base", (dynamic) -> {
         return dynamic.createInt(15 - dynamic.asInt(0));
      });
      dynamic = dynamic.update("Patterns", (dynamic) -> {
         Optional var10000 = dynamic.asStreamOpt().map((stream) -> {
            return stream.map((dynamic) -> {
               return dynamic.update("Color", (dynamic) -> {
                  return dynamic.createInt(15 - dynamic.asInt(0));
               });
            });
         });
         dynamic.getClass();
         return (Dynamic)DataFixUtils.orElse(var10000.map(dynamic::createList), dynamic);
      });
      return dynamic;
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixTag);
   }
}
