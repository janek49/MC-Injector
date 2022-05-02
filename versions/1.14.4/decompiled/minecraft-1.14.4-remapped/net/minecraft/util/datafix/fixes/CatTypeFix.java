package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class CatTypeFix extends NamedEntityFix {
   public CatTypeFix(Schema schema, boolean var2) {
      super(schema, var2, "CatTypeFix", References.ENTITY, "minecraft:cat");
   }

   public Dynamic fixTag(Dynamic dynamic) {
      return dynamic.get("CatType").asInt(0) == 9?dynamic.set("CatType", dynamic.createInt(10)):dynamic;
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixTag);
   }
}
