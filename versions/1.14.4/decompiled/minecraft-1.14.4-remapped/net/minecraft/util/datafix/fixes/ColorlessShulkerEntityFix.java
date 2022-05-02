package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class ColorlessShulkerEntityFix extends NamedEntityFix {
   public ColorlessShulkerEntityFix(Schema schema, boolean var2) {
      super(schema, var2, "Colorless shulker entity fix", References.ENTITY, "minecraft:shulker");
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), (dynamic) -> {
         return dynamic.get("Color").asInt(0) == 10?dynamic.set("Color", dynamic.createByte((byte)16)):dynamic;
      });
   }
}
