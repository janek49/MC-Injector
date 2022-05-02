package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.DataFix;
import com.mojang.datafixers.OpticFinder;
import com.mojang.datafixers.TypeRewriteRule;
import com.mojang.datafixers.schemas.Schema;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.EntityCustomNameToComponentFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityCustomNameToComponentFix extends DataFix {
   public BlockEntityCustomNameToComponentFix(Schema schema, boolean var2) {
      super(schema, var2);
   }

   public TypeRewriteRule makeRule() {
      OpticFinder<String> var1 = DSL.fieldFinder("id", DSL.namespacedString());
      return this.fixTypeEverywhereTyped("BlockEntityCustomNameToComponentFix", this.getInputSchema().getType(References.BLOCK_ENTITY), (var1x) -> {
         return var1x.update(DSL.remainderFinder(), (var2) -> {
            Optional<String> var3 = var1x.getOptional(var1);
            return var3.isPresent() && Objects.equals(var3.get(), "minecraft:command_block")?var2:EntityCustomNameToComponentFix.fixTagCustomName(var2);
         });
      });
   }
}
