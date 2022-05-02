package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class BlockEntityShulkerBoxColorFix extends NamedEntityFix {
   public BlockEntityShulkerBoxColorFix(Schema schema, boolean var2) {
      super(schema, var2, "BlockEntityShulkerBoxColorFix", References.BLOCK_ENTITY, "minecraft:shulker_box");
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), (dynamic) -> {
         return dynamic.remove("Color");
      });
   }
}
