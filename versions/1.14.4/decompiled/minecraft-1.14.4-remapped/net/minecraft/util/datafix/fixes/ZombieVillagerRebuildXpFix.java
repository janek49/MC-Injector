package net.minecraft.util.datafix.fixes;

import com.mojang.datafixers.DSL;
import com.mojang.datafixers.Typed;
import com.mojang.datafixers.schemas.Schema;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.util.datafix.fixes.NamedEntityFix;
import net.minecraft.util.datafix.fixes.References;
import net.minecraft.util.datafix.fixes.VillagerRebuildLevelAndXpFix;

public class ZombieVillagerRebuildXpFix extends NamedEntityFix {
   public ZombieVillagerRebuildXpFix(Schema schema, boolean var2) {
      super(schema, var2, "Zombie Villager XP rebuild", References.ENTITY, "minecraft:zombie_villager");
   }

   protected Typed fix(Typed typed) {
      return typed.update(DSL.remainderFinder(), (dynamic) -> {
         Optional<Number> var1 = dynamic.get("Xp").asNumber();
         if(!var1.isPresent()) {
            int var2 = ((Number)dynamic.get("VillagerData").get("level").asNumber().orElse(Integer.valueOf(1))).intValue();
            return dynamic.set("Xp", dynamic.createInt(VillagerRebuildLevelAndXpFix.getMinXpPerLevel(var2)));
         } else {
            return dynamic;
         }
      });
   }
}
