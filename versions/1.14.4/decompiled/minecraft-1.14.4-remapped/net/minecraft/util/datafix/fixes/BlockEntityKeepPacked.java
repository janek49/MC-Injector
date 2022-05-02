package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityKeepPacked extends NamedEntityFix {
   public BlockEntityKeepPacked(Schema schema, boolean var2) {
      super(schema, var2, "BlockEntityKeepPacked", References.BLOCK_ENTITY, "DUMMY");
   }

   private static Dynamic fixTag(Dynamic dynamic) {
      return dynamic.set("keepPacked", dynamic.createBoolean(true));
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), BlockEntityKeepPacked::fixTag);
   }
}
