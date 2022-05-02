package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;

public class EntityArmorStandSilentFix extends NamedEntityFix {
   public EntityArmorStandSilentFix(Schema schema, boolean var2) {
      super(schema, var2, "EntityArmorStandSilentFix", References.ENTITY, "ArmorStand");
   }

   public Dynamic fixTag(Dynamic dynamic) {
      return dynamic.get("Silent").asBoolean(false) && !dynamic.get("Marker").asBoolean(false)?dynamic.remove("Silent"):dynamic;
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), this::fixTag);
   }
}
