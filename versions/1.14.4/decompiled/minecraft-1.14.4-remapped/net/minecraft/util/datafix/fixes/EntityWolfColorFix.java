package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityWolfColorFix extends NamedEntityFix {
   public EntityWolfColorFix(Schema schema, boolean var2) {
      super(schema, var2, "EntityWolfColorFix", References.ENTITY, "minecraft:wolf");
   }

   public Dynamic fixTag(Dynamic dynamic) {
      return dynamic.update("CollarColor", (dynamic) -> {
         return dynamic.createByte((byte)(15 - dynamic.asInt(0)));
      });
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixTag);
   }
}
